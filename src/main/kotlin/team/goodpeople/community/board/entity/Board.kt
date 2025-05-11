package team.goodpeople.community.board.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import team.goodpeople.community.post.entity.Post

@Entity
class Board(

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @field:Size(min = 2, max = 50)
    @field:NotNull
    @field:Column(nullable = false, unique = true)
    var topic: String,

    @field:Size(min = 10, max = 100)
    @field:NotNull
    @field:Column(nullable = false)
    val description: String,

    @field:OneToMany(mappedBy = "board", cascade = [CascadeType.ALL])
    val posts: MutableList<Post> = mutableListOf(),
) {

    /** Board Entity 생성 팩토리 메서드 */
    companion object {
        fun createBoard(
            topic: String,
            description: String
        ): Board {
            return Board(
                topic = topic,
                description = description
            )
        }
    }


    /** 필수 보조 생성자*/
    protected constructor() : this(
        topic = "",
        description = "")
}