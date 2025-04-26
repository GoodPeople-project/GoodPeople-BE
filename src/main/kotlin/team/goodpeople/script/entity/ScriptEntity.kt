package team.goodpeople.script.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import team.goodpeople.user.entity.User
import java.time.LocalDateTime

@Entity
class ScriptEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @NotNull
    @Column(name = "request_script", columnDefinition = "TEXT")
    val requestScript: String,

    @NotNull
    @Column(name = "response_script", columnDefinition = "TEXT")
    val responseScript: String,

    @NotNull
    @Column(name = "requested_at", updatable = false)
    var requestedAt: LocalDateTime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User? = null

    ) {
    constructor() : this(
        requestScript = "default",
        responseScript = "default",
        requestedAt = LocalDateTime.now(),
    )

    @PrePersist
    fun prePersist() {
        val now = LocalDateTime.now()
        requestedAt = now
    }

//    @PrePersist
//    fun prePersist() {
//        this.createdAt = LocalDateTime.now()
//    }
}