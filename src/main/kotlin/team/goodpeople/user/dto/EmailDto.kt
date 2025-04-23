package team.goodpeople.user.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class EmailDto(
    @field:Size(min = 5, max = 100)
    @field:NotNull(message = "username cannot be blank")
    @field:Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    val email: String = ""
) {
}