package team.goodpeople.script.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import team.goodpeople.global.response.ApiResponse
import team.goodpeople.script.dto.PredictResponseDto
import team.goodpeople.script.dto.ScriptRequestDto
import team.goodpeople.script.dto.UserScriptDto
import team.goodpeople.script.dto.SimilarityResponseDto
import team.goodpeople.script.service.ScriptService
import team.goodpeople.security.jwt.annotation.GetUserId

@RequestMapping("/api/script")
@RestController
class ScriptController(
    private val scriptService: ScriptService
) {

    @PostMapping("/similarity")
    fun requestSimilarityScript(
        @GetUserId userId: Long,
        @RequestBody scriptRequestDto: ScriptRequestDto
    ): ApiResponse<SimilarityResponseDto> {
        val result = scriptService.saveAndReturnSimilarity(userId, scriptRequestDto)

        return ApiResponse.success(
            result = result,
            status = HttpStatus.OK.value(),
            message = "출력 완료"
        )
    }

    // TODO: 유사도와 같이 구동되도록
    @PostMapping("/predict")
    fun predictCase(
        @GetUserId userId: Long,
        @RequestBody scriptRequestDto: ScriptRequestDto
    ): ApiResponse<PredictResponseDto> {
        val result = scriptService.saveAndReturnPrediction(userId, scriptRequestDto)

        return ApiResponse.success(
            result = result,
            status = HttpStatus.OK.value(),
            message = "출력 완료"
        )
    }

    @GetMapping("/view/all")
    fun getAllUserScript(
        @GetUserId userId: Long,
    ): ApiResponse<List<UserScriptDto>?> {
        val result = scriptService.getAllUserScripts(userId)

        return if (result == null) {
            ApiResponse.success(
                result = null,
                status = HttpStatus.OK.value(),
                message = "조회된 스크립트가 없습니다."
            )
        } else {
            ApiResponse.success(
                result = result,
                status = HttpStatus.OK.value(),
                message = "모든 스크립트 조회 완료"
            )
        }
    }

    /** 사용자가 작성했던 스크립트 단건만 조회 */
    @GetMapping("/view/{scriptId}")
    fun getUserScript(
        @PathVariable scriptId: Long
    ): ApiResponse<UserScriptDto> {
        val result = scriptService.getUserScript(scriptId)

        return ApiResponse.success(
                result = result,
                status = HttpStatus.OK.value(),
                message = "스크립트 조회 완료"
        )

    }

    /** 유사도 모델 응답 단건 조회 */

}