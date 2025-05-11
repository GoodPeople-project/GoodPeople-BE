package team.goodpeople.script.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.goodpeople.script.entity.SimilarityAnalysis
import team.goodpeople.script.entity.UserScript

interface SimilarityAnalysisRepository : JpaRepository<SimilarityAnalysis, Long> {
}