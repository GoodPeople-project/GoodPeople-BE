package team.goodpeople.user.service

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import team.goodpeople.global.exception.CustomErrorCode
import team.goodpeople.global.exception.GlobalException
import team.goodpeople.user.dto.SignUpRequest
import team.goodpeople.user.dto.UpdateNicknameRequest
import team.goodpeople.user.dto.UpdatePasswordRequest
import team.goodpeople.user.entity.User.Companion.signUpWithForm
import team.goodpeople.user.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) {

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
            email = dto.email
        )

        userRepository.save(newUser)

        return true
    }

    fun updateNickname(
        userId: Long,
        dto: UpdateNicknameRequest
    ): Boolean {

        /** nickname 필드의 null 여부는 프론트에서 1차 검증 */

        /** 요청자 ID 유효성 검사 */
        val user = userRepository.findById(userId)
            .orElseThrow { throw GlobalException(CustomErrorCode.USER_NOT_EXISTS) }

        /** 중복 재검사 */
        if (checkNicknameDuplication(dto)) {
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

    fun checkNicknameDuplication(
        dto: UpdateNicknameRequest
    ): Boolean {

        return userRepository.existsByNickname(dto.nickname)
    }

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

    fun softDeleteUser(
        userId: Long
    ): Boolean {

        val user = userRepository.findUserById(userId) ?: throw GlobalException(CustomErrorCode.USER_NOT_EXISTS)

        if (user.isDeleted()) throw GlobalException(CustomErrorCode.USER_ALREADY_DELETED)

        user.softDelete()
        userRepository.save(user)

        return true
    }

    fun recoverUser(
        userId: Long
    ): Boolean {

        val user = userRepository.findUserById(userId) ?: throw GlobalException(CustomErrorCode.USER_NOT_EXISTS)

        if (!user.isDeleted()) throw GlobalException(CustomErrorCode.USER_NOT_DELETED)

        user.recoveryDelete()
        userRepository.save(user)

        return true
    }

    fun hardDeleteUser(
        userId: Long
    ): Boolean {

        userRepository.findUserById(userId) ?: throw GlobalException(CustomErrorCode.USER_NOT_EXISTS)

        userRepository.deleteUserById(userId)

        return true
    }

}