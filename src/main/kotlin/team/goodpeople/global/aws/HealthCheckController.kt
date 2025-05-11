package team.goodpeople.global.aws

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import team.goodpeople.global.response.ApiResponse

@RequestMapping("/aws")
@RestController
class HealthCheckController {

    @GetMapping("/health")
    fun responseHealthCheck(): ApiResponse<Nothing?> {
        return ApiResponse.success(
            result = null,
            status = HttpStatus.OK.value()
        )
    }
}