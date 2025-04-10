package team.goodpeople.user.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import team.goodpeople.global.response.ApiResponse
import team.goodpeople.user.dto.SignUpRequest
import team.goodpeople.user.dto.UpdateNicknameRequest
import team.goodpeople.user.dto.UpdatePasswordRequest
import team.goodpeople.user.service.UserService

@RequestMapping("/api/user")
@RestController
class UserController(private val userService: UserService) {

    @PostMapping
    fun signUp(
            @RequestBody @Valid dto: SignUpRequest
    ): ApiResponse<Boolean> {

        return ApiResponse.success(
            userService.signUp(dto),
            HttpStatus.CREATED.value())
    }

    @PatchMapping("/nickname/{userId}")
    fun updateNickname(
            @PathVariable userId: Long,
            @RequestBody @Valid dto: UpdateNicknameRequest
    ): ApiResponse<Boolean> {

        return if (userService.updateNickname(userId, dto)) {
            ApiResponse.success(
                true,
                HttpStatus.NO_CONTENT.value()
            )
        } else {
            ApiResponse.success(
                true,
                HttpStatus.NOT_MODIFIED.value(),
                "변경 사항이 없습니다.")
        }
    }

    @GetMapping("/nickname/check")
    fun checkIsNicknameDuplicated(
        @RequestBody @Valid dto: UpdateNicknameRequest
    ): ApiResponse<Boolean> {

        return ApiResponse.success(
            userService.checkNicknameDuplication(dto),
            HttpStatus.OK.value()
        )
    }

    @PatchMapping(("/password/{userId}"))
    fun updatePassword(
            @PathVariable userId: Long,
            @RequestBody @Valid dto: UpdatePasswordRequest): ApiResponse<Boolean> {

        return if (userService.updatePassword(userId, dto)) {
            ApiResponse.success(
                true,
                HttpStatus.NO_CONTENT.value()
            )
        } else {
            ApiResponse.success(
                true,
                HttpStatus.NOT_MODIFIED.value(),
                "변경 사항이 없습니다."
            )
        }
    }
}