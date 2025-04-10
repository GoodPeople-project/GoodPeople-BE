package team.goodpeople.common

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import java.time.LocalDateTime

@MappedSuperclass
open class BaseEntity {

    @field:Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null
        protected set

    @field:Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
        protected set

//    @field:Column(name = "updated_at")
//    var updatedAt: LocalDateTime? = null
//        protected set

    @PrePersist
    fun prePersist() {
        val now = LocalDateTime.now()
        createdAt = now
    }

    fun softDelete() {
        val now = LocalDateTime.now()
        deletedAt = now
    }

    fun isDeleted(): Boolean {
        return deletedAt != null
    }

    fun recoveryDelete() {
        deletedAt = null
    }
}