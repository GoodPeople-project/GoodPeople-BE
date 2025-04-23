package team.goodpeople.security.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import team.goodpeople.security.jwt.annotation.GetUserIdArgumentResolver

@Configuration
class WebMvcConfig(
    private val getUserIdArgumentResolver: GetUserIdArgumentResolver
) : WebMvcConfigurer {

    override fun addArgumentResolvers(
        resolvers: MutableList<HandlerMethodArgumentResolver>
    ) {
        resolvers.add(getUserIdArgumentResolver)
    }
}