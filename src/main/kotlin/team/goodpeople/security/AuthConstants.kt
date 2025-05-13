package team.goodpeople.security

object AuthConstants {

    /** Security Common Constants */
    const val DEPLOY_BASE_URL: String = "https://goodpeople.ai.kr"
//    const val DEVELOP_BASE_URL: String = "http://localhost:8080"
    val ALLOWED_ORIGIN: List<String> = listOf("https://good-people-fe.vercel.app", "https://goodpeople.ai.kr")
    val ALLOWED_METHOD: List<String> = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")

    const val MASKED_PASSWORD: String = "**********"

    /** - JWT Common Constants */
    const val TOKEN_TYPE: String = "Bearer"

    /** - Access Token Constants */
    const val ACCESS_CATEGORY: String = "access"
//    const val ACCESS_EXPIRED_MS: Long = 5 * 60 * 1000L // 5분
    const val ACCESS_EXPIRED_MS: Long = 24 * 60 * 60 * 1000L // 1일

    /** - Refresh Token Constants */
    const val REFRESH_CATEGORY: String = "refresh"
//    const val REFRESH_EXPIRED_MS: Long = 60 * 60 * 1000L // 1시간
    const val REFRESH_EXPIRED_MS: Long = 24 * 60 * 60 * 1000L // 1일


    /** Cookie Constants */
    const val COOKIE_DOMAIN: String = "goodpeople.ai.kr"
//    const val COOKIE_DOMAIN: String = "localhost"
    const val COOKIE_PATH: String = "/"
    const val COOKIE_MAX_AGE: Int = 24 * 60 * 60 * 1000 // 1일

    const val COOKIE_HTTP_ONLY: Boolean = true
    const val COOKIE_SET_SECURE: Boolean = true


    /** Authentication Code Constants */
    const val EMAIL_CODE_EXPIRED_MS: Long = 5 * 60 * 1000L // 5분
}