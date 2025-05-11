package team.goodpeople.script.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import team.goodpeople.global.exception.CustomErrorCode
import team.goodpeople.global.exception.GlobalException
import team.goodpeople.script.FastApiClient
import team.goodpeople.script.dto.PredictResponseDto
import team.goodpeople.script.dto.ScriptRequestDto
import team.goodpeople.script.dto.UserScriptDto
import team.goodpeople.script.dto.SimilarityResponseDto
import team.goodpeople.script.entity.PredictAnalysis.Companion.createPredictAnalysis
import team.goodpeople.script.entity.SimilarityAnalysis.Companion.createSimilarityAnalysis
import team.goodpeople.script.entity.UserScript
import team.goodpeople.script.entity.UserScript.Companion.createUserScriptWithNecessary
import team.goodpeople.script.repository.PredictAnalysisRepository
import team.goodpeople.script.repository.SimilarityAnalysisRepository
import team.goodpeople.script.repository.UserScriptRepository
import team.goodpeople.user.repository.UserRepository

@Service
class ScriptService(
    private val userRepository: UserRepository,
    private val userScriptRepository: UserScriptRepository,
    private val similarityAnalysisRepository: SimilarityAnalysisRepository,
    private val predictAnalysisRepository: PredictAnalysisRepository,
    private val fastApiClient: FastApiClient,
    private val objectMapper: ObjectMapper
) {

    /** 기본 제공 기능: 유사도 모델 */
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
        val userScript = createUserScriptWithNecessary(
            content = content,
            user = user)
        userScriptRepository.save(userScript)

        /** DB에 AI 응답 저장 */
        // TODO: 새로운 스키마에 맞춰 수정하기. 현재는 단순 직렬화
        val mappedResult = objectMapper.writeValueAsString(result)

        val similarityAnalysis = createSimilarityAnalysis(
            content = mappedResult,
            userScript = userScript
        )

        similarityAnalysisRepository.save(similarityAnalysis)

        /** 결과 반환 */
        return result
    }

    /** 유료 기능: 예측 모델 */
    // TODO: 유사도 모델하고 같이 처리하기. 현재 분리됨
    fun saveAndReturnPrediction(
        userId: Long,
        scriptRequestDto: ScriptRequestDto,
    ): PredictResponseDto {
        /** 유저 유효성 확인 */
        val user = userRepository.findById(userId)
            .orElseThrow { throw GlobalException(CustomErrorCode.USER_NOT_EXISTS) }

        val content = scriptRequestDto.content

        /** FastAPI 서버에 스크립트 요청 */
        // TODO: 결과 스키마 맞출 것
        val result = fastApiClient.predictCase(content)

        /** DB에 사용자 요청 스크립트 저장 */
        val userScript = createUserScriptWithNecessary(
            content = content,
            user = user)
        userScriptRepository.save(userScript)

        /** DB에 AI 응답 저장 */
        val predictAnalysis = createPredictAnalysis(
            content = result.content,
            userScript = userScript
        )

        predictAnalysisRepository.save(predictAnalysis)

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