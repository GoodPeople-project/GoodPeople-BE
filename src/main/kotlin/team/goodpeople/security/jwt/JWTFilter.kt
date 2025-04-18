package team.goodpeople.security.jwt

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import team.goodpeople.global.response.ApiResponse
import team.goodpeople.global.response.ResponseWriter
import team.goodpeople.security.auth.CustomUserDetails
import team.goodpeople.user.entity.Role
import team.goodpeople.user.entity.User

class JWTFilter(
    private val jwtUtil: JWTUtil,
    private val responseWriter: ResponseWriter
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            /** Auth 헤더에서 Access Token 추출 */
            val accessToken: String = jwtUtil.resolveAccessToken(request)
                ?: return filterChain.doFilter(request, response)

            /** cat이 Access Token을 나타내는지 확인 */
            if (!jwtUtil.isAccessToken(accessToken)) {
                return filterChain.doFilter(request, response)
            }

            /** 토큰 만료 여부 확인 */
            if (jwtUtil.isTokenExpired(accessToken)) {
                return filterChain.doFilter(request, response)
            }

            /**
             * 검증 후 Context에 사용자 등록
             * 팩토리 메서드로 user 객체를 생성하여 Details에 넘긴다.
             * */
            val username = jwtUtil.getClaim(accessToken, "username")
            val role = jwtUtil.getClaim(accessToken, "role")
            val userId = jwtUtil.getClaim(accessToken, "id", Long::class.java)

            val user = User.createEntityForJWT(
                id = userId,
                username = username,
                role = Role.valueOf(role))
            val userDetails = CustomUserDetails(user)

            val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, null)

            /** Security Context Holder에 저장 */
            SecurityContextHolder.getContext().authentication = authenticationToken

            filterChain.doFilter(request, response)
        // TODO: 예외처리 구체화
        } catch (e: ExpiredJwtException) {
            SecurityContextHolder.clearContext()
            responseWriter.writeJsonResponse(
                response = response,
                status = 401,
                body = ApiResponse.failure<String>(
                    401,
                    "Expired Token"
                )
            )
        } catch (e: JwtException) {
            SecurityContextHolder.clearContext()
            responseWriter.writeJsonResponse(
                response = response,
                status = 401,
                body = ApiResponse.failure<String>(
                    401,
                    "Invalid Token"
                )
            )
        } catch (e: Exception) {
            SecurityContextHolder.clearContext()
            responseWriter.writeJsonResponse(
                response = response,
                status = 500,
                body = ApiResponse.failure<String>(
                    status = 500,
                    message = "Error during Token Authentication"
                )
            )
        }
    }
}