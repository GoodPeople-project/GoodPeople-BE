package team.goodpeople.security.jwt

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import team.goodpeople.global.response.ApiResponse
import team.goodpeople.global.response.ResponseWriter

/** 401 예외 응답 */
@Component
class JWTAuthenticationEntryPoint(
    private val responseWriter: ResponseWriter
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        responseWriter.writeJsonResponse(
            response = response,
            status = 401, // Unauthorized
            body = ApiResponse.failure<String>(
                status = 401,
                message = "Require Login"
            )
        )
    }
}