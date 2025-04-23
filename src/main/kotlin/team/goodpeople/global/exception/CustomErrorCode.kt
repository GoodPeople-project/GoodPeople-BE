package team.goodpeople.global.exception

import org.springframework.http.HttpStatus

enum class CustomErrorCode(
    val status: HttpStatus,
    val message: String
) {
    /** 작성 예시 */
    EXAMPLE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "테스트 예외"),

    /** ETC */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "오류가 발생했습니다."),

    /** User */
    USER_NOT_EXISTS(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다."),

    USERNAME_DUPLICATED(HttpStatus.UNPROCESSABLE_ENTITY, "중복된 아이디입니다."),
    NICKNAME_DUPLICATED(HttpStatus.UNPROCESSABLE_ENTITY, "중복된 닉네임입니다."),
    EMAIL_DUPLICATED(HttpStatus.UNPROCESSABLE_ENTITY, "중복된 이메일입니다."),

    USER_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    USER_NOT_DELETED(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),


    /** Security */
    LOGIN_AUTHENTICATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "로그인에 실패하였습니다."),

    INVALID_ATTRIBUTE(HttpStatus.UNAUTHORIZED, "유효하지 않은 값입니다.")
}