package team.goodpeople.security.auth

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import team.goodpeople.global.response.ApiResponse
import team.goodpeople.global.response.ResponseWriter
import team.goodpeople.security.jwt.JWTUtil

class LoginFilter(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JWTUtil,
    private val responseWriter: ResponseWriter
) : UsernamePasswordAuthenticationFilter() {

    init {
        setFilterProcessesUrl("/api/login")
    }

    override fun attemptAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Authentication {

        val loginRequest = ObjectMapper().readValue(request.inputStream, LoginRequest::class.java)

        val username = loginRequest.username
        val password = loginRequest.password

        val authenticationToken = UsernamePasswordAuthenticationToken(
            username,
            password,
            null
        )

        return authenticationManager.authenticate(authenticationToken)
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        // TODO: 성공 로직. UserDetails 구성 고민 및 리프레시 토큰 저장 로직 추가
        val authenticatedUser = authResult.principal as CustomUserDetails

        val accessToken = jwtUtil.createAccessToken(
            userId = authenticatedUser.getUserID(),
            username = authenticatedUser.username,
            role = authenticatedUser.authorities.toString()
        )

        val refreshToken = jwtUtil.createRefreshToken(
            userId = authenticatedUser.getUserID(),
            username = authenticatedUser.username,
            role = authenticatedUser.authorities.toString()
        )

        val result = jwtUtil.convertTokenToResponse(accessToken, refreshToken)

        responseWriter.writeJsonResponse(
            response = response,
            body = ApiResponse.success(
                result = result
            )
        )
    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException
    ) {
        responseWriter.writeJsonResponse(
            response = response,
            status = 401,
            body = ApiResponse.failure<String>(
                // TODO: CustomErrorCode와 연계가 될 것 같다.
                status = 400,
                message = "존재하지 않는 유저입니다.")
        )
    }
}