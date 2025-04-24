package team.goodpeople.user.service

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import team.goodpeople.global.exception.CustomErrorCode
import team.goodpeople.global.exception.GlobalException
import team.goodpeople.mail.service.SendEmailService
import team.goodpeople.user.dto.*
import team.goodpeople.user.entity.User.Companion.signUpWithForm
import team.goodpeople.user.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val sendEmailService: SendEmailService,
    private val authenticationCodeService: AuthenticationCodeService
) {

    /** 폼 회원가입 */
    fun signUp(
        dto: SignUpRequest
    ): Boolean {

        if (userRepository.existsByUsername(dto.username)) {
            throw GlobalException(CustomErrorCode.USERNAME_DUPLICATED)
        }

        val newUser = signUpWithForm(
            username = dto.username,
            password = bCryptPasswordEncoder.encode(dto.password),
            nickname = dto.nickname,
        )

        userRepository.save(newUser)

        return true
    }

    /** 이메일 인증 번호 전송 */
    // TODO: 재전송
    fun sendEmailAuthenticationCode(
        dto: EmailDto
    ): Boolean {
        /** 이메일 중복 재검사 */
        val newEmail = dto.email
        if (userRepository.existsByEmail(newEmail)) {
            throw GlobalException(CustomErrorCode.EMAIL_DUPLICATED)
        }

        /** 인증 번호 생성 */
        val authenticationCode = generateAuthenticationCode()

        /** 이메일 내용 작성 */
        val emailMessage = sendEmailService.createMail(
            to = newEmail,
            subject = "[GoodPeople] 이메일 인증 번호를 입력해주세요.",
            content = """
                GoodPeople에서 이메일 인증 번호를 보내드립니다.
                이메일 인증 번호를 입력하여, 이메일 변경을 완료해주세요.
                $authenticationCode
            """.trimIndent()
        )

        /** Redis에 인증 번호 저장 */
        authenticationCodeService.saveEmailAuthCode(newEmail, authenticationCode)

        /** 이메일 전송 */
        try {
            sendEmailService.sendMail(emailMessage)
            //TODO: 예외처리
        } catch (e: Exception) {
            throw GlobalException(CustomErrorCode.INTERNAL_SERVER_ERROR)
        }

        return true
    }

    /** 이메일 인증 완료 처리 */
    fun matchEmailAuthenticationCode(
        dto: EmailAuthDto
    ): Boolean {
        // 입력값 획득
        val newEmail = dto.email
        val emailAuthCode = dto.emailAuthCode

        // Redis에 저장된 코드 조회
        val savedEmailAuthCode = authenticationCodeService.getEmailAuthCode(newEmail)

        // 동등성 확인
        if (emailAuthCode != savedEmailAuthCode) {
            return false
        }

        // 일치하면 Redis에 저장된 값 삭제
        authenticationCodeService.deleteEmailAuthCode(newEmail)

        return true
    }

    /** 이메일 중복 검사 */
    fun checkIsEmailDuplicated(
        email: String,
    ): Boolean {
        return userRepository.existsByUsername(email)
    }

    /** 닉네임 변경 */
    fun updateNickname(
        userId: Long,
        dto: UpdateNicknameRequest
    ): Boolean {

        /** nickname 필드의 null 여부는 프론트에서 1차 검증 */

        /** 요청자 ID 유효성 검사 */
        val user = userRepository.findById(userId)
            .orElseThrow { throw GlobalException(CustomErrorCode.USER_NOT_EXISTS) }

        /** 중복 재검사 */
        if (checkIsNicknameDuplicated(dto)) {
            throw GlobalException(CustomErrorCode.NICKNAME_DUPLICATED)
        }

        /** nickname 필드가 null로 들어왔다면 Valid 검증에 걸렸을 것이다. */
        val newNickname = dto.nickname

        /** 새로운 닉네임이 이전 닉네임과 동일한 경우, 컨트롤러에서 '변경 없음' 응답 */
        if (newNickname == user.nickname) {
            return false
        }

        /** 저장 */
        user.updateNickname(newNickname)
        userRepository.save(user)

        return true
    }

    /** 닉네임 중복 검사 */
    fun checkIsNicknameDuplicated(
        dto: UpdateNicknameRequest
    ): Boolean {

        return userRepository.existsByNickname(dto.nickname)
    }

    /** 비밀번호 변경 */
    fun updatePassword(
        userId: Long,
        dto: UpdatePasswordRequest
    ): Boolean {

        /** 요청자 ID 유효성 검사 */
        val user = userRepository.findById(userId)
            .orElseThrow { throw GlobalException(CustomErrorCode.USER_NOT_EXISTS) }

        /** 새로운 비밀번호가 이전 비밀번호와 동일한 경우, 컨트롤러에서 '변경 없음' 응답 */
        if (bCryptPasswordEncoder.matches(dto.password, user.password)) {
            return false
        }

        val newPassword = bCryptPasswordEncoder.encode(dto.password)

        user.updatePassword(newPassword)
        userRepository.save(user)

        return true
    }

    /** 유저 Soft Delete */
    fun softDeleteUser(
        userId: Long
    ): Boolean {

        val user = userRepository.findUserById(userId) ?: throw GlobalException(CustomErrorCode.USER_NOT_EXISTS)

        if (user.isDeleted()) throw GlobalException(CustomErrorCode.USER_ALREADY_DELETED)

        user.softDelete()
        userRepository.save(user)

        return true
    }

    /** Soft Delete 된 유저 복구 */
    fun recoverUser(
        userId: Long
    ): Boolean {

        val user = userRepository.findUserById(userId) ?: throw GlobalException(CustomErrorCode.USER_NOT_EXISTS)

        if (!user.isDeleted()) throw GlobalException(CustomErrorCode.USER_NOT_DELETED)

        user.recoveryDelete()
        userRepository.save(user)

        return true
    }

    /** 유저 Hard Delete */
    fun hardDeleteUser(
        userId: Long
    ): Boolean {

        userRepository.findUserById(userId) ?: throw GlobalException(CustomErrorCode.USER_NOT_EXISTS)

        userRepository.deleteUserById(userId)

        return true
    }


    /** 인증번호 생성 */
    private fun generateAuthenticationCode(
        length: Int = 6
    ): String {
        val authenticationCode = ""
        return (1..length)
            .map { (0..9).random() }
            .joinToString { authenticationCode }
    }
}