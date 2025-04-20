package team.goodpeople.security.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import team.goodpeople.global.response.ApiResponse
import team.goodpeople.security.refresh.RefreshService

@RequestMapping("/api/auth")
@RestController
class AuthController(
    private val refreshService: RefreshService
) {

    @PostMapping("/refresh")
    fun reissueAccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ApiResponse<Boolean> {
        val result = refreshService.reissueAccessToken(request, response)

        return ApiResponse.success(
            result = result,
            status = 200,
            message = "Reissue Successful"
        )
    }

    /**
     * 내부 로직으로 생성되는 엔드포인트
     * - 폼 로그인
     * @PostMapping("/login")
     *
     * - 소셜 로그인
     * @URL ("/oauth2/authorization/{provider}")
     *
     * - 공통 로그아웃
     * @PostMapping("/logout")
     * */
}