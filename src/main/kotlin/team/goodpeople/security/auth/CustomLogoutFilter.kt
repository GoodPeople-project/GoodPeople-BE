package team.goodpeople.security.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import team.goodpeople.global.response.ApiResponse
import team.goodpeople.global.response.ResponseWriter
import team.goodpeople.security.jwt.CookieUtil.Companion.deleteCookie
import team.goodpeople.security.jwt.CookieUtil.Companion.getStringFromCookies
import team.goodpeople.security.jwt.JWTUtil
import team.goodpeople.security.refresh.RefreshService

class CustomLogoutFilter(
    private val refreshService: RefreshService,
    private val jwtUtil: JWTUtil,
    private val responseWriter: ResponseWriter
) : OncePerRequestFilter() {

    /**
     * 로그아웃 요청이 유효한지 검증한다.
     * 유효하지 않으면 필터를 실행하지 않는다.
     * */
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return !isLogoutRequest(request)
    }

    /**
     * 로그아웃 요청이 유효하면, 로그아웃 절차를 시작한다.
     * @param request
     * @param response
     * @param filterChain
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        /** Request에서 Refresh Token 추출 */
        val refreshToken = getStringFromCookies(
            request, "refresh_token")
            ?: return filterChain.doFilter(request, response)

        /** TODO: 유효성 검증 로직 합쳐서 JWTFilter에서 재사용하기 */
        /** cat이 Access Token을 나타내는지 확인 */
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            return filterChain.doFilter(request, response)
        }

        /** 토큰 만료 여부 확인 */
        if (jwtUtil.isTokenExpired(refreshToken)) {
            return filterChain.doFilter(request, response)
        }

        /** Request의 Refresh Token으로, Redis 내부의 동일한 토큰을 조회 */
        val username = jwtUtil.getClaim(refreshToken, "username")

        refreshService.getRefreshToken(username)

        /** Redis 내부의 Refresh Token 삭제. */
        refreshService.deleteRefreshToken(username)

        /** 쿠키의 Refresh Token 삭제 */
        deleteCookie(response, "refresh_token")

        responseWriter.writeJsonResponse(
            response = response,
            status = 200,
            body = ApiResponse.success(
                status = 200,
                message = "Logout success",
                result = null
            )
        )
    }


    /** 검증 메서드 */
    fun isLogoutRequest(
        request: HttpServletRequest
    ): Boolean {

        val requestURI = request.requestURI
        if (!requestURI.startsWith("/api/auth/logout")) {
            return false
        }

        val requestMethod = request.method
        if (requestMethod != ("POST")) {
            return false
        }

        return true
    }

}