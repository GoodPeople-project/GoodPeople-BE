package team.goodpeople.security.jwt

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import team.goodpeople.security.AuthConstants.COOKIE_DOMAIN
import team.goodpeople.security.AuthConstants.COOKIE_HTTP_ONLY
import team.goodpeople.security.AuthConstants.COOKIE_MAX_AGE
import team.goodpeople.security.AuthConstants.COOKIE_PATH
import team.goodpeople.security.AuthConstants.COOKIE_SET_SECURE

class CookieUtil {

    companion object {
        fun sendCookie(
            response: HttpServletResponse,
            key: String,
            value: String
        ) {
            val cookie = Cookie(key, value)

            cookie.apply {
                maxAge = COOKIE_MAX_AGE
                domain = COOKIE_DOMAIN
                path = COOKIE_PATH
                secure = COOKIE_SET_SECURE
                isHttpOnly = COOKIE_HTTP_ONLY
            }

            response.addCookie(cookie)
        }

        fun deleteCookie(
            response: HttpServletResponse,
            key: String
        ) {
            val cookie = Cookie(key, null)

            cookie.apply {
                maxAge = 0
                domain = COOKIE_DOMAIN
                path = COOKIE_PATH
                secure = COOKIE_SET_SECURE
                isHttpOnly = COOKIE_HTTP_ONLY
            }

            response.addCookie(cookie)
        }

        fun getStringFromCookies(
            request: HttpServletRequest,
            key: String
        ): String? {
            val cookie = request.cookies?.firstOrNull { it.name == key }

            return cookie?.value
        }
    }
}