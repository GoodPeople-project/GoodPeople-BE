package team.goodpeople.script.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.goodpeople.script.entity.UserScript

interface UserScriptRepository : JpaRepository<UserScript, Long> {

    fun getUserScriptsByUserId(userId: Long): List<UserScript>?

    fun getUserScriptByUserId(userId: Long): UserScript?
}