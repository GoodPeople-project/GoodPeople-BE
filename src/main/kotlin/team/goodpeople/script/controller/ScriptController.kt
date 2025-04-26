package team.goodpeople.script.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import team.goodpeople.global.response.ApiResponse
import team.goodpeople.script.dto.ScriptRequestDto
import team.goodpeople.script.service.ScriptService
import team.goodpeople.security.jwt.annotation.GetUserId

@RequestMapping("/api/script")
@RestController
class ScriptController(
    private val scriptService: ScriptService
) {

    @GetMapping("/similarity")
    fun requestSimilarityScript(
        @GetUserId userId: Long,
        @RequestBody scriptRequestDto: ScriptRequestDto
    ): ApiResponse<String> {
        val result = scriptService.saveAndReturnSimilarity(userId, scriptRequestDto)

        return ApiResponse.success(
            result = result,
            status = HttpStatus.OK.value(),
            message = "출력 완료"
        )
    }
}