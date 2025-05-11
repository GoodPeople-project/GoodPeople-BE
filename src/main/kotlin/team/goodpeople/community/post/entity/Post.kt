package team.goodpeople.community.post.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import team.goodpeople.community.board.entity.Board
import team.goodpeople.community.board.entity.Board.Companion.createBoard
import team.goodpeople.user.entity.User
import team.goodpeople.user.entity.User.Companion.signUpWithForm
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@Table(
    name = "post",
    indexes = [
        Index(name = "idx_post_title", columnList = "title"),
        Index(name = "idx_post_created_at", columnList = "created_at"),
        Index(name = "idx_post_board_id", columnList = "board_id")
    ])
@Entity
class Post(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @field:Column(name = "title", nullable = false)
    val title: String,

    @field:Column(name = "body", nullable = false, columnDefinition = "TEXT")
    var body: String,

    @field:Column(name = "view_count", nullable = false)
    var viewCount: Long = 0,

    @field:Column(name = "like_count", nullable = false)
    var likeCount: Long = 0,

    @field:LastModifiedDate
    @field:Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @field:CreatedDate
    @field:Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "board_id", nullable = false)
    val board: Board,

    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "user_id", nullable = false, updatable = false)
    val author: User,
) {
    /** 게시글 생성 팩토리 메서드 */
    companion object {
        fun createPost(
            title: String,
            body: String,
            board: Board,
            author: User,
        ): Post {
            return Post(
                title = title,
                body = body,
                board = board,
                author = author,
            )
        }
    }


    /** 필수 보조 생성자 */
    protected constructor() : this(
        title = "default",
        body = "default",
        board = createBoard(
            topic = "default",
            description = "default",
        ),
        author = signUpWithForm(
            username = "default@gmail.com",
            password = "default",
            nickname = "default",
            email = "default@gmail.com",
        )
    )
}