package team.goodpeople.script.entity

import jakarta.persistence.*
import team.goodpeople.user.entity.User
import java.time.LocalDateTime
import java.util.*

/**
 * TODO: 양식 작성 기능
 * 현재는 content 필드만 채운다.
 * */
@Entity
class UserScript(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // TODO: 발생 일시로 수정
    /** 시작일 */
    @field:Column(name = "start_time")
    val startTime: Date? = null,

    /** 종료일 */
    @field:Column(name = "end_time")
    val endTime: Date? = null,

    /** 현재 진행 여부 */
    @field:Column(name = "onGoing")
    val onGoing: Boolean? = null,

    /** 가해자 직책 */
    @field:Column(name = "position")
    val position: String? = null,

    /** 신고자와의 관계 */
    @field:Column(name = "relation")
    val relation: String? = null,

    /** 기업 규모 */
    @field:Column(name = "size")
    val size: String? = null,

    /** 부서 */
    @field:Column(name = "department")
    val department: String? = null,


    /** Harassment[]
     * date
     * content - 사용자의 사례 스크립트
     * evidence
     * */
    @field:Column(name = "date")
    val date: Date? = null,

    @field:Column(name = "content", columnDefinition = "TEXT")
    val content: String,

    @field:Column(name = "evidence")
    val evidence: String? = null,

    /** 추가 정보 */
    @field:Column(name = "information")
    val information: String? = null,

    /** 요청 시간 */
    @field:Column(name = "requested_at")
    val requestedAt: LocalDateTime,

    /** AI 답변과 일대일 관계 매핑 */
    @field:OneToOne(mappedBy = "userScript", cascade = [(CascadeType.ALL)], orphanRemoval = true, fetch = FetchType.LAZY)
    val similarityAnalysis: SimilarityAnalysis? = null,

    /** User와 다대일 관계 매핑 */
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "user_id", nullable = false)
    val user: User? = null,
) {
    companion object {
        /** TODO: 현재는 필수 요소만 저장하는 팩토리 메서드 */
        fun createUserScriptWithNecessary(
            content: String,
            user: User,
        ): UserScript {
            return UserScript(
                content = content,
                requestedAt = LocalDateTime.now(),
                user = user
            )
        }
    }


    /** 필수 보조 생성자 */
    protected constructor() : this(
        id = null,
        startTime = Date(),
        endTime = Date(),
        onGoing = null,
        position = null,
        relation = null,
        size = null, department = null,
        date = null,
        content = "default",
        evidence = "default",
        information = null,
        requestedAt = LocalDateTime.now(),
//        scriptEntity = null,
//        user = null
    )
}