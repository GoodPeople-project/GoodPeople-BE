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
import team.goodpeople.global.exception.CustomErrorCode
import team.goodpeople.global.exception.GlobalException
import team.goodpeople.global.response.ApiResponse
import team.goodpeople.security.jwt.JWTUtil

class LoginFilter(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JWTUtil
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

        // TODO: Response 작성 메서드 구현 고려
        response.status = HttpServletResponse.SC_OK
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"

        val apiResponse = ApiResponse.success(
            result = result,
            message = "login successful",)

        val objectMapper = ObjectMapper()
        response.writer.write(objectMapper.writeValueAsString(apiResponse))
    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException
    ) {
        throw GlobalException(CustomErrorCode.LOGIN_AUTHENTICATION_FAILED)
    }
}