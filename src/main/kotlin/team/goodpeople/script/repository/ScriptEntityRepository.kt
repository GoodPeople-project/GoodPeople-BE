package team.goodpeople.script.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import team.goodpeople.script.entity.ScriptEntity

@Repository
interface ScriptEntityRepository : JpaRepository<ScriptEntity, Long> {
}