package team.goodpeople.global.exception

import org.springframework.http.HttpStatus

enum class CustomErrorCode(
    val status: HttpStatus,
    val message: String
) {
    EXAMPLE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "테스트 예외"),
    USERNAME_DUPLICATED(HttpStatus.UNPROCESSABLE_ENTITY, "중복된 아이디입니다."),
    NICKNAME_DUPLICATED(HttpStatus.UNPROCESSABLE_ENTITY, "중복된 닉네임입니다."),
    USER_NOT_EXISTS(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다."),
}