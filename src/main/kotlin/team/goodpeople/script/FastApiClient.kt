package team.goodpeople.script

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import team.goodpeople.script.dto.*

@Component
class FastApiClient(
    @Value("\${spring.external.fastapi.url}") private val fastApiUrl: String,
) {

    private val webClient: WebClient = WebClient.builder()
        .baseUrl(fastApiUrl)
        .build()

    fun analyzeSimilarCase(
        script: String
    ): SimilarityResult {
        return webClient.post()
            .uri("/similarity")
            .bodyValue(ScriptRequestDto(script))
            .exchangeToMono { response ->
                when {
                    response.statusCode() == HttpStatus.NO_CONTENT ->
                        Mono.just(SimilarityNoCaseResponseDto())

                    response.statusCode().is2xxSuccessful ->
                        response.bodyToMono(SimilarityResponseDto::class.java)
                else ->
                    response.bodyToMono(ErrorResultDto::class.java)
                }
            }
            .onErrorResume {
                Mono.error(RuntimeException("FastAPI 호출 실패: ${it.message}"))
            }
            .block() ?: throw RuntimeException("FastAPI 응답이 null입니다.")
    }

    fun predictCase(
        script: String
    ): PredictResponseDto {
        return webClient.post()
            .uri("/predict")
            .bodyValue(ScriptRequestDto(script))
            .retrieve()
            .bodyToMono(PredictResponseDto::class.java)
            .onErrorResume {
                Mono.error(RuntimeException("FastAPI 호출 실패: ${it.message}"))

            }
            .block() ?: throw RuntimeException("FastAPI 응답이 null입니다.")
    }
}