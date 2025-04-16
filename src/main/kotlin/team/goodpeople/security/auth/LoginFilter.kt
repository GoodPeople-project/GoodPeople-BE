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
import team.goodpeople.security.jwt.CookieUtil.Companion.sendCookie
import team.goodpeople.security.jwt.JWTUtil
import team.goodpeople.security.refresh.RefreshService

class LoginFilter(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JWTUtil,
    private val responseWriter: ResponseWriter,
    private val refreshService: RefreshService
) : UsernamePasswordAuthenticationFilter() {

    init {
        setFilterProcessesUrl("/api/login")
    }

    override fun attemptAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Authentication {

        /**
         * 로그인 요청에서 아이디, 비밀번호를 꺼내 authenticationManager에게 위임.
         * */
        val loginRequest = ObjectMapper().readValue(request.inputStream, LoginRequest::class.java)

        val username = loginRequest.username
        val password = loginRequest.password

        /** 초기 인증이므로 authorities는 null로 설정한다.*/
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
        // TODO: UserDetails 구성 고민
        /** 인증이 완료된 사용자의 정보를 추출하여 토큰 생성 */
        val authenticatedUser = authResult.principal as CustomUserDetails

        val userId = authenticatedUser.getUserID()
        val username = authenticatedUser.username
        val role = authenticatedUser.authorities.toString()

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
        // TODO: Redis에 저장된 토큰의 만료 시간과 실제 만료 시간이 일치하는지 확인할 것.
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
                message = "Login failed")
        )
    }
}