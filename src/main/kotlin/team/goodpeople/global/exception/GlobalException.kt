package team.goodpeople.global.exception

class GlobalException(
    val errorCode: CustomErrorCode
) : RuntimeException(errorCode.message)