package team.goodpeople.user.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UpdatePasswordRequest(
    @field:NotNull
    @field:Size(min = 8)
    val password: String = ""
)