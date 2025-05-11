package team.goodpeople.community.post.dto

import java.time.LocalDateTime

data class PostDto(
    val id: Long,
    val title: String,
    val body: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val viewCount: Long,
    val likeCount: Long,
    val nickname: String,
    val boardTopic: String
) {
}