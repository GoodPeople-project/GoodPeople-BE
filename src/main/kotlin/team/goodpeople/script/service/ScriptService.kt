package team.goodpeople.script.service

import org.springframework.stereotype.Service
import team.goodpeople.global.exception.CustomErrorCode
import team.goodpeople.global.exception.GlobalException
import team.goodpeople.script.FastApiClient
import team.goodpeople.script.dto.ScriptRequestDto
import team.goodpeople.script.dto.UserScriptDto
import team.goodpeople.script.dto.SimilarityResponseDto
import team.goodpeople.script.entity.UserScript.Companion.createUserScriptWithNecessary
import team.goodpeople.script.repository.SimilarityAnalysisRepository
import team.goodpeople.script.repository.UserScriptRepository
import team.goodpeople.user.repository.UserRepository

@Service
class ScriptService(
    private val userRepository: UserRepository,
    private val userScriptRepository: UserScriptRepository,
    private val similarityAnalysisRepository: SimilarityAnalysisRepository,
    private val fastApiClient: FastApiClient
) {

    fun saveAndReturnSimilarity(
        userId: Long,
        scriptRequestDto: ScriptRequestDto,
    ): SimilarityResponseDto {
        /** 유저 유효성 확인 */
        val user = userRepository.findById(userId)
            .orElseThrow { throw GlobalException(CustomErrorCode.USER_NOT_EXISTS) }

        val content = scriptRequestDto.content

        /** FastAPI 서버에 스크립트 요청 */
        val result = fastApiClient.analyzeSimilarCase(content)

        /** DB에 사용자 요청 스크립트 저장 */
        userScriptRepository.save(
            createUserScriptWithNecessary(
                content = content,
                user = user)
        )

        /** DB에 AI 응답 저장 */
        // TODO: 새로운 스키마에 맞춰 수정하기
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

    fun getAllUserScripts(
        userId: Long,
    ): List<UserScriptDto>? {

        val userScripts = userScriptRepository.getUserScriptsByUserId(userId)
            ?: return null

        return userScripts.map { userScript ->
            UserScriptDto(
                userScript.id!!,
                userScript.content,
                userScript.requestedAt
            )
        }
    }

    fun getUserScript(
        scriptId: Long,
    ): UserScriptDto {

        val userScript = userScriptRepository.getUserScriptByUserId(scriptId)
            ?: throw GlobalException(CustomErrorCode.SCRIPT_NOT_EXISTS)

        return UserScriptDto(
            userScript.id!!,
            userScript.content,
            userScript.requestedAt)
    }
}