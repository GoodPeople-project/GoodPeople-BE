package team.goodpeople.security.oauth2

import com.nimbusds.openid.connect.sdk.claims.UserInfo
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
import team.goodpeople.security.jwt.dto.UserInfoDto
import team.goodpeople.security.oauth2.dto.CustomOAuth2User
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
            val oAuth2User = authentication.principal as CustomOAuth2User

            // TODO: 덜 번거롭게 변환 가능한지
            val userId = oAuth2User.getUserId()
            val username = oAuth2User.getUsername()
            val role = oAuth2User.getRole() // as List<String>
            val loginType = oAuth2User.getLoginType()

            val userInfo = UserInfoDto(
                userId = userId,
                username = username,
                role = role,
                loginType = loginType)

            val accessToken = jwtUtil.createAccessToken(userInfo)
            val refreshToken = jwtUtil.createRefreshToken(userInfo)

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

            /** Access Token을 쿠키에 담아 응답한다. */
            sendCookie(
                response = response,
                key = "access_token",
                value = accessToken)

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