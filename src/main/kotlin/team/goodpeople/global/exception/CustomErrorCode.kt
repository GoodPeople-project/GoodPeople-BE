package team.goodpeople.global.exception

import org.springframework.http.HttpStatus

enum class CustomErrorCode(
    val status: HttpStatus,
    val message: String
) {
    EXAMPLE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "테스트 예외"),
}