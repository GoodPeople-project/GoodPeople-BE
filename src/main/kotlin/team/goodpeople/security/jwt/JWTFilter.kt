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
import team.goodpeople.security.AuthConstants.MASKED_PASSWORD
import team.goodpeople.security.auth.CustomUserDetails
import team.goodpeople.security.jwt.dto.UserInfoDto
import team.goodpeople.security.path.SecurityPath.AUTH_WHITELIST
import team.goodpeople.security.path.SecurityPath.isWhitelistedURI

class JWTFilter(
    private val jwtUtil: JWTUtil,
    private val responseWriter: ResponseWriter
) : OncePerRequestFilter() {

    override fun shouldNotFilter(
        request: HttpServletRequest
    ): Boolean {
        return isWhitelistedURI(request, AUTH_WHITELIST)
    }

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
            /*TODO: OAuth2 이식하기
            *  현재 UserDetails를 상속한 CustomUserDetails에 정보를 담은 후,
            *  ContextHolder에 저장하고 있음.
            *  로그인 방식이 두 가지임을 고려하여 공통 DTO를 작성할지,
            *  로그인 방식에 따라 DTO를 다르게 할지 고려 필요
            *  근데 여기서 역직렬화를 굳이 하는 이유가 있냐?
            * */
            val userInfo = jwtUtil.getNestedClaim(accessToken, "user", UserInfoDto::class.java)

            // TODO: OAuth2일 경우, OAuth2User로 저장하도록 분기
            val userDetails = CustomUserDetails(
                username = userInfo.username,
                password = MASKED_PASSWORD,
                userId = userInfo.userId,
                role = userInfo.role,
                loginType = userInfo.loginType
            )

            val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, null)

            /** Security Context Holder에 저장 */
            SecurityContextHolder.getContext().authentication = authenticationToken

            request.setAttribute("user", userInfo)

            filterChain.doFilter(request, response)
        // TODO: 예외처리 구체화
        // 검증 실패 시 로그인 페이지로 리다이렉트
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
            response.sendRedirect("/login")
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
            response.sendRedirect("/login")
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
            response.sendRedirect("/login")
        }
    }

//    private fun isWhitelisted(
//        request: HttpServletRequest
//    ): Boolean {
//
//        val matcher = AntPathMatcher()
//        val requestURI = request.requestURI
//
//        return AUTH_WHITELIST.any { pattern ->
//            matcher.match(pattern, requestURI)
//        }
//    }
}