package team.goodpeople.security.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import team.goodpeople.security.AuthConstants.ALLOWED_METHOD
import team.goodpeople.security.AuthConstants.ALLOWED_ORIGIN

@Configuration
class CorsConfig : CorsConfigurationSource{

    override fun getCorsConfiguration(
        request: HttpServletRequest
    ): CorsConfiguration {

        val config = CorsConfiguration()

        config.apply {
            allowedOrigins = ALLOWED_ORIGIN
            allowedMethods = ALLOWED_METHOD
            allowedHeaders = listOf("*")
            exposedHeaders = listOf("Authorization")
            allowCredentials = true
        }

        return config
    }
}