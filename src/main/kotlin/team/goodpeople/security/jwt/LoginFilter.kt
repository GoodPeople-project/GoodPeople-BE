package team.goodpeople.security.jwt

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
import team.goodpeople.security.dto.CustomUserDetails
import team.goodpeople.security.dto.LoginRequest

class LoginFilter(
    private val authenticationManager: AuthenticationManager,
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
        // TODO: 성공 로직. UserDetails 구성 고민 및 JWT 추가 후 작성
        val authenticatedUser = authResult.principal as CustomUserDetails
        val result = mapOf("access token" to "PLEASE ADD ACCESS TOKEN")

        response.status = HttpServletResponse.SC_OK
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"

        val apiResponse = ApiResponse.success(result)

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