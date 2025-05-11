package team.goodpeople.script.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import team.goodpeople.user.entity.User
import java.time.LocalDateTime
import java.util.*

@Entity
class SimilarityAnalysis(
    @Id
    val id: Long? = null,

    /** 사용자 요청 본문과 일대일 관계 매핑 */
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_script_id")
    val userScript: UserScript,

    /** AI 답변 본문 */
    @NotNull
    @Column(name = "content", columnDefinition = "TEXT")
    val content: String,
) {
    /** */
    companion object {
        fun createSimilarityAnalysis(
            content: String,
            userScript: UserScript,
        ): SimilarityAnalysis {
            return SimilarityAnalysis(
                content = content,
                userScript = userScript
            )
        }
    }


    /** 필수 보조 생성자 */
    constructor() : this(
        content = "default",
        userScript = UserScript.createUserScriptWithNecessary(
            content = "default",
            user = User.signUpWithForm(
                username = "default",
                password = "default",
                nickname = "default",
                email = "default@gmail.com"
            )
        )
//        userScript = UserScript(
//            id = null,
//            startTime = Date(),
//            endTime = Date(),
//            onGoing = null,
//            position = null,
//            relation = null,
//            size = null,
//            department = null,
//            date = null,
//            content = "default",
//            evidence = "default",
//            information = null,
//            requestedAt = LocalDateTime.now(),
//        )
    )
}