package team.goodpeople.global.response

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

@Component
class ResponseWriter(
    private val objectMapper: ObjectMapper
) {

    /** JSON 형식으로 Response 작성
     *
     * @param status Http 응답 상태 코드
     * @param body ApiResponse 인자로 사용할 수 있다.
     */
    fun writeJsonResponse(
        response: HttpServletResponse,
        status: Int = 200,
        body: Any,
    ) {
        response.status = status
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.writer.write(objectMapper.writeValueAsString(body))
    }
}