package team.goodpeople.user.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SignUpRequest(
    @field:Size(min = 5, max = 20)
    @field:NotNull(message = "username cannot be blank")
    val username: String,

    @field:Size(min = 8)
    @field:NotNull(message = "password cannot be blank")
    val password: String,

    @field:Size(min = 2, max = 10)
    @field:NotNull(message = "nickname cannot be blank")
    val nickname: String,

    @field:Size(max = 100)
    @field:NotNull(message = "email cannot be blank")
    @field:Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    val email: String
) {
}