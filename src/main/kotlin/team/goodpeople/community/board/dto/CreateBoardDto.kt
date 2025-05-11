package team.goodpeople.community.board.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateBoardDto(
    @field:Size(min = 2, max = 50)
    @field:NotNull
    val topic: String,

    @field:Size(min = 10, max = 100)
    @field:NotNull
    val description: String
) {
}