package team.goodpeople.security.oauth2

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository

@Configuration
class SocialClientRegistrationRepo(
    private val socialClientRegistration: SocialClientRegistration
) {
    @Bean
    fun ClientRegistrationRepository(): ClientRegistrationRepository {
        return InMemoryClientRegistrationRepository(
            socialClientRegistration.naverClientRegistration()
        )
    }
}