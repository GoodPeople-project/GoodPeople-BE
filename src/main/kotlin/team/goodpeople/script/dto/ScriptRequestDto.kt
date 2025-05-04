package team.goodpeople.script.dto

import jakarta.validation.constraints.NotNull

data class ScriptRequestDto(
    @NotNull
    val content: String = "",
) {
}