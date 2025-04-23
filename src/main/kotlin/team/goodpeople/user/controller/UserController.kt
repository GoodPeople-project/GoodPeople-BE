package team.goodpeople.user.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import team.goodpeople.global.response.ApiResponse
import team.goodpeople.security.jwt.annotation.GetUserId
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
            result = userService.signUp(dto),
            status = HttpStatus.CREATED.value())
    }

    @PatchMapping("/nickname")
    fun updateNickname(
        @GetUserId userId: Long,
        @RequestBody @Valid dto: UpdateNicknameRequest
    ): ApiResponse<Boolean> {

        return if (userService.updateNickname(userId, dto)) {
            ApiResponse.success(
                result = true,
                status = HttpStatus.NO_CONTENT.value()
            )
        } else {
            ApiResponse.success(
                result = true,
                status = HttpStatus.NOT_MODIFIED.value(),
                message = "변경 사항이 없습니다.")
        }
    }

    @GetMapping("/nickname/check")
    fun checkIsNicknameDuplicated(
        @RequestBody @Valid dto: UpdateNicknameRequest
    ): ApiResponse<Boolean> {

        return ApiResponse.success(
            result = userService.checkNicknameDuplication(dto),
            status = HttpStatus.OK.value()
        )
    }

    @PatchMapping(("/password"))
    fun updatePassword(
        @GetUserId userId: Long,
        @RequestBody @Valid dto: UpdatePasswordRequest
    ): ApiResponse<Boolean> {

        return if (userService.updatePassword(userId, dto)) {
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

    @PatchMapping("/{userId}/delete")
    fun deleteUser(
        @PathVariable userId: Long
    ): ApiResponse<Boolean> {

        return ApiResponse.success(
            result = userService.softDeleteUser(userId),
            status = HttpStatus.OK.value()
        )
    }

    @PatchMapping("/{userId}/recovery")
    fun recoverUser(
        @PathVariable userId: Long
    ): ApiResponse<Boolean> {

        return ApiResponse.success(
            result = userService.recoverUser(userId),
            status = HttpStatus.OK.value()
        )
    }

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