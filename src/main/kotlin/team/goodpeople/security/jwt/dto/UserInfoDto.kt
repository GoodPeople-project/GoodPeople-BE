package team.goodpeople.security.jwt.dto

data class UserInfoDto(
    val username: String,
    val userId: Long,
    val role: String,
    val loginType: String
) {
}