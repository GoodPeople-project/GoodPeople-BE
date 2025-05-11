package team.goodpeople.community.post.dto

import java.time.LocalDateTime

data class PostListDto(
    val id: Long,
    val title: String,
    val nickname: String,
    val createdAt: LocalDateTime,
    val viewCount: Long,
    val likeCount: Long,
) {
}