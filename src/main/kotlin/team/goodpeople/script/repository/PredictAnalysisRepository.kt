package team.goodpeople.script.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.goodpeople.script.entity.PredictAnalysis

interface PredictAnalysisRepository : JpaRepository<PredictAnalysis, Long> {
}