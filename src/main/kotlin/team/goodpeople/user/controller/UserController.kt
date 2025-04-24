package team.goodpeople.user.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import team.goodpeople.global.response.ApiResponse
import team.goodpeople.security.jwt.annotation.GetUserId
import team.goodpeople.user.dto.*
import team.goodpeople.user.service.UserService

@RequestMapping("/api/user")
@RestController
class UserController(private val userService: UserService) {

    /**
     * 폼 회원가입
     *
     * @param signUpRequest 회원가입 정보를 담을 DTO 클래스
     * - username: 로그인 시 입력할 이메일 형식의 아이디다.
     * - password: 8 - 20자 사이의 문자열 비밀번호다.
     * - nickname: 2 - 10자 사이의 문자열 닉네임이다.
     * @return 정상 작동 시 OK 응답을 반환
     */
    @PostMapping
    fun signUp(
        @RequestBody @Valid signUpRequest: SignUpRequest
    ): ApiResponse<Boolean> {

        return ApiResponse.success(
            result = userService.signUp(signUpRequest),
            status = HttpStatus.CREATED.value())
    }

    /**
     * 닉네임 업데이트
     *
     * @param userId 어노테이션으로 획득
     * @param updateNicknameRequest 변경 희망하는 닉네임을 담을 DTO 클래스
     * - nickname: 2 - 10자 사이의 문자열 닉네임
     * @return
     * - 정상 변경 시: OK 응답
     * - 동일 닉네임일 경우 변경 없음 응답
     */
    @PatchMapping("/nickname")
    fun updateNickname(
        @GetUserId userId: Long,
        @RequestBody @Valid updateNicknameRequest: UpdateNicknameRequest
    ): ApiResponse<Boolean> {

        return if (userService.updateNickname(userId, updateNicknameRequest)) {
            ApiResponse.success(
                result = true,
                status = HttpStatus.NO_CONTENT.value(),
                message = "닉네임이 변경되었습니다."
            )
        } else {
            ApiResponse.success(
                result = true,
                status = HttpStatus.NOT_MODIFIED.value(),
                message = "변경 사항이 없습니다.")
        }
    }

    /**
     * 닉네임 중복 검사
     *
     * @param updateNicknameRequest 중복 검사를 실행할 닉네임을 담은 DTO 클래스
     * - nickname: 2 - 10자 사이의 문자열 닉네임
     * @return
     * - 중복 닉네임이 존재하면 true를 담아 응답
     * - 중복 닉네임이 존재하지 않으면 false를 담아 응답
     */
    @GetMapping("/nickname/check")
    fun checkIsNicknameDuplicated(
        @RequestBody @Valid updateNicknameRequest: UpdateNicknameRequest
    ): ApiResponse<Boolean> {

        val result = userService.checkIsNicknameDuplicated(updateNicknameRequest)

        return if (result) {
            ApiResponse.success(
                result = true,
                status = HttpStatus.OK.value(),
                message = "이미 사용중인 닉네임입니다."
            )
        } else {
            ApiResponse.success(
                result = false,
                status = HttpStatus.OK.value(),
                message = "사용 가능한 닉네임입니다."
            )
        }
    }

    /**
     * 회원 가입 시 이메일 인증 번호 전송
     *
     * @param emailDto 등록을 원하는 이메일이 담긴 DTO 클래스
     * - email: 이메일 형식의 문자열
     * @return
     * - 인증 번호가 정상적으로 정상 되면 OK 응답을 반환
     * - //TODO 실패 응답 메시지 분기
     */
    @PostMapping("/email")
    fun sendEmailAuthenticationCode(
        @RequestBody @Valid emailDto: EmailDto
    ): ApiResponse<Boolean> {

        // TODO: 프론트에서 인증 번호 요청 시간을 저장, 값을 받아서 키로 사용하면 될 듯
        val result = userService.sendEmailAuthenticationCode(emailDto)

        return if (result) {
            ApiResponse.success(
                result = true,
                status = HttpStatus.OK.value(),
                message = "인증 번호가 전송되었습니다."
            )
        }
        else {
            ApiResponse.success(
                result = false,
                status = HttpStatus.BAD_REQUEST.value(),
                message = "인증 번호 전송에 실패했습니다."
            )
        }
    }

    /**
     * 이메일 인증 처리 완료
     *
     * @param emailAuthDto
     * - email: 이메일 형식의 문자열
     * - emailAuthCode: 이메일로 전송된 6자리의 인증 번호 문자열
     * @return
     * - 올바른 인증 번호 입력 시, OK 응답과 인증 완료 메시지
     * - 잘못된 인증 번호 입력 시, OK 응답과 인증 실패 메시지
     */
    @PostMapping("/email/code")
    fun matchEmailAuthenticationCode(
        @RequestBody @Valid emailAuthDto: EmailAuthDto
    ): ApiResponse<Boolean> {

        val result = userService.matchEmailAuthenticationCode(emailAuthDto)

        return if (result) {
            ApiResponse.success(
                result = true,
                status = HttpStatus.OK.value(),
                message = "인증이 완료되었습니다."
            )
        }
        else {
            ApiResponse.success(
                result = false,
                status = HttpStatus.OK.value(),
                message = "인증 번호가 일치하지 않습니다."
            )
        }
    }

    /**
     * 이메일 중복 검사
     *
     * @param emailDto 중복 검사 대상이 될 이메일이 담긴 DTO 클래스
     * - email: 이메일 형식의 문자열
     * @return
     * - 중복 이메일이 존재하면 true를 담아 응답
     * - 중복 이메일이 존재하지 않으면 false를 담아 응답
     */
    @GetMapping("/email")
    fun checkIsEmailDuplicated(
        @RequestBody @Valid emailDto: EmailDto
    ): ApiResponse<Boolean> {

        val result = userService.checkIsEmailDuplicated(emailDto.email)

        return if (result) {
            ApiResponse.success(
                result = true,
                status = HttpStatus.OK.value(),
                message = "이미 사용중인 이메일입니다."
            )
        } else {
            ApiResponse.success(
                result = false,
                status = HttpStatus.OK.value(),
                message = "사용 가능한 이메일입니다."
            )
        }
    }

    /**
     * 비밀번호 변경
     *
     * @param userId 어노테이션으로 획득
     * @param updatePasswordRequest 변경을 원하는 비밀번호가 담긴 DTO 클래스
     * - password: 8 - 30자 사이의 문자열
     * @return
     * - 변경 완료 시 OK 응답
     * - 이전 비밀번호와 동일하면 변경 없음 응답
     */
    @PatchMapping(("/password"))
    fun updatePassword(
        @GetUserId userId: Long,
        @RequestBody @Valid updatePasswordRequest: UpdatePasswordRequest
    ): ApiResponse<Boolean> {

        return if (userService.updatePassword(userId, updatePasswordRequest)) {
            ApiResponse.success(
                result = true,
                status = HttpStatus.NO_CONTENT.value()
            )
        } else {
            ApiResponse.success(
                result = true,
                status = HttpStatus.NOT_MODIFIED.value(),
                message = "변경 사항이 없습니다."
            )
        }
    }

    /**
     * 사용자 Soft Delete
     *
     * @param userId 삭제 대상 유저의 id 값
     * @return
     * - 정상 삭제 시 OK 응답
     */
    @PatchMapping("/{userId}/delete")
    fun deleteUser(
        @PathVariable userId: Long
    ): ApiResponse<Boolean> {

        return ApiResponse.success(
            result = userService.softDeleteUser(userId),
            status = HttpStatus.OK.value()
        )
    }

    /**
     * Soft Delete 사용자 복구
     *
     * @param userId 복구 대상 유저의 id 값
     * @return
     * - 정상 복구 시 OK 응답
     */
    @PatchMapping("/{userId}/recovery")
    fun recoverUser(
        @PathVariable userId: Long
    ): ApiResponse<Boolean> {

        return ApiResponse.success(
            result = userService.recoverUser(userId),
            status = HttpStatus.OK.value()
        )
    }

    /**
     * 사용자 데이터 완전 삭제
     *
     * @param userId 완전 삭제 대상 유저의 id 값
     * @return
     * - 정상 삭제 시 OK 응답
     */
    @DeleteMapping("/{userId}/deletion")
    fun hardDeleteUser(
        @PathVariable userId: Long
    ): ApiResponse<Boolean> {

        return ApiResponse.success(
            result = userService.hardDeleteUser(userId),
            status = HttpStatus.NO_CONTENT.value()
        )
    }
}