package team.goodpeople.user.dto

data class UserProfileDto(
    val username: String,
    val nickname: String,
    val email: String,
    val role: String,
    val loginType: String
)