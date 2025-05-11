package team.goodpeople.script.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import team.goodpeople.user.entity.User

@Entity
class PredictAnalysis(
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
    companion object {
        fun createPredictAnalysis(
            content: String,
            userScript: UserScript,
        ): PredictAnalysis {
            return PredictAnalysis(
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
                nickname = "default"
            )
        )
    )
}