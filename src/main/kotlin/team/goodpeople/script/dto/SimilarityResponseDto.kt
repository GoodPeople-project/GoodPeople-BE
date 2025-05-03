package team.goodpeople.script.dto

/** FastAPI는 JSON 형식으로 응답한다.
 * 따라서 아래의 형태를 맞추기 위한 DTO다.
 * "top3": [
 *  { "story": "유사 사례1", "result": "인정"},
 *  { "story": "유사 사례2", "result": "불인정"},
 *  { "story": "유사 사례3", "result": "인정"}
 * ]
 * */
data class SimilarityResponseDto(
    val top3: List<SimilarityCaseDto> = listOf(),
)

data class SimilarityCaseDto(
    val story: String,
    val result: String
)