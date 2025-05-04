package team.goodpeople.script.service

import org.springframework.stereotype.Service
import team.goodpeople.global.exception.CustomErrorCode
import team.goodpeople.global.exception.GlobalException
import team.goodpeople.script.FastApiClient
import team.goodpeople.script.dto.ScriptRequestDto
import team.goodpeople.script.dto.ScriptResponseDto
import team.goodpeople.script.dto.SimilarityResponseDto
import team.goodpeople.script.repository.ScriptEntityRepository
import team.goodpeople.user.repository.UserRepository
import java.time.LocalDateTime

@Service
class ScriptService(
    private val userRepository: UserRepository,
    private val scriptEntityRepository: ScriptEntityRepository,
    private val fastApiClient: FastApiClient
) {

    fun saveAndReturnSimilarity(
        userId: Long,
        scriptRequestDto: ScriptRequestDto,
    ): SimilarityResponseDto {
        /** 유저 유효성 확인 */
        val user = userRepository.findById(userId)
            .orElseThrow { throw GlobalException(CustomErrorCode.USER_NOT_EXISTS) }

        val requestScript = scriptRequestDto.content
        val requestedAt = LocalDateTime.now()

        /** FastAPI 서버에 스크립트 요청 */
//        val result = fastApiClient.analyzeSimilarCase(requestScript)
//            .top3.joinToString("\n\n") { case ->
//                "Top 사례:\n${case.story}\n\n판별 결과: ${case.result}"
//            }

        val result = fastApiClient.analyzeSimilarCase(requestScript)

        /** DB에 내용 저장 */
//        TODO: 새로운 스키마에 맞춰 수정하기
//        val scriptEntity = ScriptEntity(
//            requestScript = requestScript,
//            responseScript = result,
//            requestedAt = requestedAt,
//            user = user
//        )
//        scriptEntityRepository.save(scriptEntity)

        /** 결과 반환 */
        return result
    }

    fun getAllScripts(
        userId: Long,
    ): List<ScriptResponseDto>? {

        val scriptEntities = scriptEntityRepository.getScriptEntitiesByUserId(userId)
            ?: return null

        return scriptEntities.map { scriptEntity ->
            ScriptResponseDto(
                scriptEntity.id!!,
                scriptEntity.requestScript,
                scriptEntity.responseScript,
                scriptEntity.requestedAt
            )
        }
    }

    fun getScript(
        scriptId: Long,
    ): ScriptResponseDto {

        val scriptEntity = scriptEntityRepository.getScriptEntityById(scriptId)
            ?: throw GlobalException(CustomErrorCode.SCRIPT_NOT_EXISTS)

        return ScriptResponseDto(
            scriptEntity.id!!,
            scriptEntity.requestScript,
            scriptEntity.responseScript,
            scriptEntity.requestedAt)
    }
}