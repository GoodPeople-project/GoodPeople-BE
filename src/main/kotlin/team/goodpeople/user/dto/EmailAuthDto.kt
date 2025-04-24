package team.goodpeople.user.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class EmailAuthDto(
    @field:Size(min = 5, max = 100)
    @field:NotNull(message = "email cannot be blank")
    @field:Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    val email: String,
    @field:Size(min = 6, max = 6)
    @field:NotNull(message = "auth code cannot be blank")
    val emailAuthCode: String
) {
}