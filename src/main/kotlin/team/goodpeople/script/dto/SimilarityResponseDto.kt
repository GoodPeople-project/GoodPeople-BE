package team.goodpeople.script.dto

/** FastAPI는 JSON 형식으로 응답한다.
 * 따라서 아래의 형태를 맞추기 위한 DTO다.
 * "top3": [
 *  { "story": "유사 사례1", "result": "인정"},
 *  { "story": "유사 사례2", "result": "불인정"},
 *  { "story": "유사 사례3", "result": "인정"}
 * ]
 * */
sealed class SimilarityResult

data class SimilarMainCase(
    val case: String,
    val caseNo: String,
    val score: Int,
    val judgementResult: String,
    val judgementReason: String
)

data class SimilarOtherCase(
    val case: String,
    val caseNo: String,
    val score: Int,
    val judgementResult: String
)

data class SimilarityResponseDto(
    val myCase: String,
    val mainCase: SimilarMainCase,
    val keyword: String,
    val aiPredict: String,
    val otherCases: List<SimilarOtherCase>
) : SimilarityResult()

data class SimilarityNoCaseResponseDto(
    val content: String = "회원님의 사례와 유사한 사례가 존재하지 않습니다."
) : SimilarityResult()

data class ErrorResultDto(
    val content: String = ""
) : SimilarityResult()