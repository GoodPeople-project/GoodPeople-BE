package team.goodpeople.user.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UpdateNicknameRequest(
    @field:NotNull
    @field:Size(min = 2, max = 10)
    val nickname: String = ""
)