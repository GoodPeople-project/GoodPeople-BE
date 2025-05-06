package team.goodpeople.script.dto

import java.time.LocalDateTime

data class UserScriptDto (
    val scriptId: Long,
    val content: String,
    val requestedAt: LocalDateTime,
){
}