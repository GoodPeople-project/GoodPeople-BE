package team.goodpeople.security.auth

data class LoginRequest(
    val username: String? = null,
    val password: String? = null
) {
}