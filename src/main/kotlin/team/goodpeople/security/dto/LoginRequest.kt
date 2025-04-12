package team.goodpeople.security.dto

data class LoginRequest(
    val username: String? = null,
    val password: String? = null
) {
}