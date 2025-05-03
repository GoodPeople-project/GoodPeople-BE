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
import team.goodpeople.security.oauth2.dto.CustomOAuth2User
import team.goodpeople.security.path.SecurityPath.AUTH_WHITELIST
import team.goodpeople.security.path.SecurityPath.isWhitelistedURI

class JWTFilter(
    private val jwtUtil: JWTUtil,
    private val responseWriter: ResponseWriter
) : OncePerRequestFilter() {

    /** JWT 검증이 필요 없는 API에 대해서는 필터가 작동하지 않는다. */
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
             * 로그인 타입에 따라 분기, Context에 저장된다.
             * */
            val userInfo = jwtUtil.getNestedClaim(accessToken, "user", UserInfoDto::class.java)

            val userDetails =
                if (userInfo.loginType == "FORM") {
                    CustomUserDetails(
                        username = userInfo.username,
                        password = MASKED_PASSWORD,
                        userId = userInfo.userId,
                        role = userInfo.role,
                        loginType = userInfo.loginType) }
                else {
                    CustomOAuth2User(
                        username = userInfo.username,
                        userId = userInfo.userId,
                        role = userInfo.role,
                        loginType = userInfo.loginType,
                        attributes = emptyMap())
                }

            val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, null)

            /** Security Context Holder에 저장 */
            SecurityContextHolder.getContext().authentication = authenticationToken

            request.setAttribute("user", userInfo)

            filterChain.doFilter(request, response)
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