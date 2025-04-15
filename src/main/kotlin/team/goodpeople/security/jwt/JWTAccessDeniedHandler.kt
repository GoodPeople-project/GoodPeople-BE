package team.goodpeople.security.jwt

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import team.goodpeople.global.response.ApiResponse
import team.goodpeople.global.response.ResponseWriter

@Component
class JWTAccessDeniedHandler(
    private val responseWriter: ResponseWriter
) : AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        responseWriter.writeJsonResponse(
            response = response,
            status = 403, // Forbidden
            body = ApiResponse.failure<String>(
                status = 403,
                message = "Access denied"
            )
        )
    }
}