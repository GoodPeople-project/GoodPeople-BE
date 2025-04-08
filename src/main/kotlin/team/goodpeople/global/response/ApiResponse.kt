package team.goodpeople.global.response

data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val result: T? = null
) {
    companion object {
        fun <T> success(result: T, status: Int = 200, message: String = "request success"): ApiResponse<T> =
            ApiResponse(result = result, status = status, message = message)

        fun <T> failure(status: Int = 400, message: String = "request failed"): ApiResponse<T> =
            ApiResponse(status = status, message = message)
    }
}