package team.goodpeople.global.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import team.goodpeople.global.response.ApiResponse

@RestControllerAdvice
class GlobalExceptionHandler {

    /* 커스텀 예외 응답 */
    @ExceptionHandler(GlobalException::class)
    fun handleGlobalException(e: GlobalException): ResponseEntity<ApiResponse<Nothing>> {
        val error = e.errorCode
        return ResponseEntity
            .status(error.status)
            .body(ApiResponse.failure(status = error.status.value(), message = error.message))
    }

    /* 그 외 예외 응답 */
    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        e.printStackTrace()
        val status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        return ResponseEntity
            .status(status)
            .body(ApiResponse.failure(status = status, message = "오류가 발생했습니다."))
    }
}