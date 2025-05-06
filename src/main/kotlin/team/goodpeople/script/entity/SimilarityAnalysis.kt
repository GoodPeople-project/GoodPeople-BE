package team.goodpeople.script.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import team.goodpeople.user.entity.User
import java.time.LocalDateTime
import java.util.*

@Entity
class SimilarityAnalysis(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    /** AI 답변 본문 */
    @NotNull
    @Column(name = "response_script", columnDefinition = "TEXT")
    val content: String,

    /** 사용자 요청 본문과 일대일 관계 매핑 */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "additional_info_id")
    val userScript: UserScript
) {


    /** 필수 보조 생성자 */
    constructor() : this(
        content = "default",
        userScript = UserScript.createUserScriptWithNecessary(
            content = "default",
            user = User.signUpWithForm(
                username = "default",
                password = "default",
                nickname = "default"
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