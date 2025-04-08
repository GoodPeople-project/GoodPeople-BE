package team.goodpeople.global.response

data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val result: T? = null
) {
    companion object {
        /** 요청 성공
         * @param result 요청 결과를 담는다.
         * @param status 응답 본문에 담길 상태 코드.
         * @param message 응답 본문에 담길 메시지.
         */
        fun <T> success(result: T, status: Int = 200, message: String = "request success"): ApiResponse<T> =
            ApiResponse(result = result, status = status, message = message)

        /** 요청 실패
         * @param status 위와 동일하다. default 값을 400으로 설정.
         * @param message 위와 동일하다.
         * */
        fun <T> failure(status: Int = 400, message: String = "request failed"): ApiResponse<T> =
            ApiResponse(status = status, message = message)
    }
}