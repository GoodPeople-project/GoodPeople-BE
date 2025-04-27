package team.goodpeople.script.dto

import java.time.LocalDateTime

data class ScriptResponseDto (
    val scriptId: Long,
    val requestScript: String,
    val responseScript: String,
    val requestedAt: LocalDateTime,
){
}