package team.goodpeople.community.post.dto

data class CreatePostDto(
    val boardId: Long,
    val title: String,
    val body: String,
) {
}