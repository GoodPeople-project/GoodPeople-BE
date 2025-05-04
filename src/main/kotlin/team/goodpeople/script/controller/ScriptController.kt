package team.goodpeople.script.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import team.goodpeople.global.response.ApiResponse
import team.goodpeople.script.dto.ScriptRequestDto
import team.goodpeople.script.dto.ScriptResponseDto
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

    @GetMapping("/view/all")
    fun getAllScript(
        @GetUserId userId: Long,
    ): ApiResponse<List<ScriptResponseDto>?> {
        val result = scriptService.getAllScripts(userId)

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
                message = "모든 스크립트 조회"
            )
        }
    }

    @GetMapping("/view/{scriptId}")
    fun getScript(
        @PathVariable scriptId: Long
    ): ApiResponse<ScriptResponseDto> {
        val result = scriptService.getScript(scriptId)

        return ApiResponse.success(
                result = result,
                status = HttpStatus.OK.value(),
                message = ""
        )

    }
}