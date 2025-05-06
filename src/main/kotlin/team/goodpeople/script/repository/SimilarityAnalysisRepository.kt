package team.goodpeople.script.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.goodpeople.script.entity.UserScript

interface SimilarityAnalysisRepository : JpaRepository<UserScript, Long> {

    fun getAdditionalInfosByUserId(userId: Long): List<UserScript>?
}