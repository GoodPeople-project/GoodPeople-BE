package team.goodpeople.script.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import team.goodpeople.global.exception.CustomErrorCode
import team.goodpeople.global.exception.GlobalException
import team.goodpeople.python.PythonRunner
import team.goodpeople.script.dto.ScriptRequestDto
import team.goodpeople.script.dto.ScriptResponseDto
import team.goodpeople.script.entity.ScriptEntity
import team.goodpeople.script.repository.ScriptEntityRepository
import team.goodpeople.user.repository.UserRepository
import java.time.LocalDateTime

@Service
class ScriptService(
    private val userRepository: UserRepository,
    private val pythonRunner: PythonRunner,
    private val scriptEntityRepository: ScriptEntityRepository,
) {
    private val logger: Logger = LoggerFactory.getLogger(ScriptService::class.java)

    fun saveAndReturnSimilarity(
        userId: Long,
        scriptRequestDto: ScriptRequestDto
    ): String {
        /** 유저 유효성 확인 */
        val user = userRepository.findById(userId)
            .orElseThrow { throw GlobalException(CustomErrorCode.USER_NOT_EXISTS) }

        val requestScript = scriptRequestDto.script
        val requestedAt = LocalDateTime.now()

        /** 사용자 스크립트를 파이썬 프로세스로 전달 */
        // TODO: 일단 결과 반환하고 저장 등등 가능?
        val responseScript = pythonRunner.runSimilarity(requestScript)

        /** 내용 저장 */
        val scriptEntity = ScriptEntity(
            requestScript = requestScript,
            responseScript = responseScript,
            requestedAt = requestedAt,
            user = user
        )

        scriptEntityRepository.save(scriptEntity)

        /** 결과 전달 */
        return responseScript
    }

    fun getAllScript(
        userId: Long,
    ): List<ScriptResponseDto> {

        val result = scriptEntityRepository.getScriptEntitiesByUserId(userId)
            .map { ScriptResponseDto(
                it.id!!,
                it.requestScript,
                it.responseScript,
                it.requestedAt) }

        return result
    }
}