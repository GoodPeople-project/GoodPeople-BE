package team.goodpeople.security.jwt

object JWTConstants {

    /** - Common Constants */
    const val TOKEN_TYPE: String = "Bearer"

    /** - Access Token Constants */
    const val ACCESS_CATEGORY: String = "access"
    const val ACCESS_EXPIRED_MS: Long = 5 * 60 * 1000L // 5분

    /** - Refresh Token Constants */
    const val REFRESH_CATEGORY: String = "refresh"
    const val REFRESH_EXPIRED_MS: Long = 60 * 60 * 1000L // 1시간


    /** Cookie Constants */
    const val COOKIE_DOMAIN: String = "localhost"
    const val COOKIE_PATH: String = "/"
    const val COOKIE_MAX_AGE: Int = 24 * 60 * 60 * 1000 // 1일

    const val COOKIE_HTTP_ONLY: Boolean = true
    const val COOKIE_SET_SECURE: Boolean = true
}