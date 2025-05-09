package team.goodpeople.script

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import team.goodpeople.script.dto.PredictResponseDto
import team.goodpeople.script.dto.ScriptRequestDto
import team.goodpeople.script.dto.SimilarityResponseDto

@Component
class FastApiClient(
    @Value("\${spring.external.fastapi.url}") private val fastApiUrl: String,
) {

    private val webClient: WebClient = WebClient.builder()
        .baseUrl(fastApiUrl)
        .build()

    fun analyzeSimilarCase(
        script: String
    ): SimilarityResponseDto {
        return webClient.post()
            .uri("/similarity")
            .bodyValue(ScriptRequestDto(script))
            .retrieve()
            .bodyToMono(SimilarityResponseDto::class.java)
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