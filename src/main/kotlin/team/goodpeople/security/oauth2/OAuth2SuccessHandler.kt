package team.goodpeople.security.oauth2

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import team.goodpeople.global.response.ApiResponse
import team.goodpeople.global.response.ResponseWriter
import team.goodpeople.security.jwt.CookieUtil.Companion.sendCookie
import team.goodpeople.security.jwt.JWTUtil
import team.goodpeople.security.refresh.RefreshService

@Component
class OAuth2SuccessHandler(
    private val jwtUtil: JWTUtil,
    private val responseWriter: ResponseWriter,
    private val refreshService: RefreshService
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        try {
            val oAuth2User = authentication.principal as OAuth2User

            // TODO: 덜 번거롭게 변환 가능한지
            val userId = oAuth2User.attributes["id"].toString().toLong()
            val username = oAuth2User.attributes["username"] as String
            val role = oAuth2User.attributes["roles"].toString() // as List<String>

            val accessToken = jwtUtil.createAccessToken(
                userId = userId,
                username = username,
                role = role
            )

            val refreshToken = jwtUtil.createRefreshToken(
                userId = userId,
                username = username,
                role = role
            )

            /** 발급한 Refresh Token은 Redis에 저장한다. */
            refreshService.saveRefreshToken(username, refreshToken)

            /** Access / Refresh Token 모두 body에 담아 응답한다. */
            val result = jwtUtil.convertTokenToResponse(accessToken, refreshToken)

            responseWriter.writeJsonResponse(
                response = response,
                body = ApiResponse.success(
                    result = result
                )
            )

            /** Refresh Token을 쿠키에 담아 응답한다. */
            sendCookie(
                response = response,
                key = "refresh_token",
                value = refreshToken)

//        response.sendRedirect("/")
            //TODO: 예외처리
        } catch (e: Exception) {
            responseWriter.writeJsonResponse(
                response = response,
                body = ApiResponse.failure<String>(
                    status = 500,
                    message = "Success Logic Failed")
            )
        }
    }
}