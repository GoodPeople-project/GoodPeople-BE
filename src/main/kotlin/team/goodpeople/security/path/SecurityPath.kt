package team.goodpeople.security.path

import jakarta.servlet.http.HttpServletRequest
import org.springframework.util.AntPathMatcher

object SecurityPath {

    /** 인증을 필요로 하지 않는 API */
    val AUTH_WHITELIST: Array<String> = arrayOf(
        "/api/auth/**",
        "/oauth2/**",
        "/"
    )

    val ONLY_ADMIN_WHITELIST: Array<String> = arrayOf(
        "/api/admin/**"
    )



    fun isWhitelistedURI(
        request: HttpServletRequest,
        whitelist: Array<String>
    ): Boolean {

        val matcher = AntPathMatcher()
        val requestURI = request.requestURI

        return whitelist.any { pattern ->
            matcher.match(pattern, requestURI)
        }
    }
}