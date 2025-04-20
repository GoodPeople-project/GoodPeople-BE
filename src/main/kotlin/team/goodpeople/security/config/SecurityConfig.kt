package team.goodpeople.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutFilter
import team.goodpeople.global.response.ResponseWriter
import team.goodpeople.security.auth.CustomLogoutFilter
import team.goodpeople.security.jwt.JWTUtil
import team.goodpeople.security.auth.LoginFilter
import team.goodpeople.security.jwt.JWTAccessDeniedHandler
import team.goodpeople.security.jwt.JWTAuthenticationEntryPoint
import team.goodpeople.security.jwt.JWTFilter
import team.goodpeople.security.oauth2.CustomOAuth2AuthorizedClientService
import team.goodpeople.security.oauth2.CustomOAuth2UserService
import team.goodpeople.security.oauth2.OAuth2SuccessHandler
import team.goodpeople.security.oauth2.SocialClientRegistrationRepo
import team.goodpeople.security.refresh.RefreshService

@EnableWebSecurity
@Configuration
class SecurityConfig(
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val jwtUtil: JWTUtil,
    private val refreshService: RefreshService,
    private val jwtAccessDeniedHandler: JWTAccessDeniedHandler,
    private val jwtAuthenticationEntryPoint: JWTAuthenticationEntryPoint,
    private val responseWriter: ResponseWriter,

    private val socialClientRegistrationRepo: SocialClientRegistrationRepo,
    private val customOAuth2AuthorizedClientService: CustomOAuth2AuthorizedClientService,
    private val clientRegistrationRepo: SocialClientRegistrationRepo,
    private val jdbcTemplate: JdbcTemplate,
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val oAuth2SuccessHandler: OAuth2SuccessHandler
) {

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {

        val authenticationManager = authenticationConfiguration.authenticationManager

        http
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .logout { it. logoutUrl("/api/auth/logout") }
            .oauth2Login {
                it
                    .authorizationEndpoint { it.baseUri("/oauth2/authorization") }
                    .clientRegistrationRepository(socialClientRegistrationRepo.ClientRegistrationRepository())
                    .authorizedClientService(customOAuth2AuthorizedClientService.oAuth2AuthorizedClientService(jdbcTemplate, clientRegistrationRepo.ClientRegistrationRepository()))
                    .userInfoEndpoint { it.userService(customOAuth2UserService)}
                    .successHandler(oAuth2SuccessHandler)
            }

        http
            .exceptionHandling {
                it
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    .accessDeniedHandler(jwtAccessDeniedHandler)
            }

        http
            .authorizeHttpRequests {
                it
                    .requestMatchers("/api/auth/**", "/oauth2/**", "/").permitAll()
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers(("/api/user/**")).permitAll()
                    .anyRequest().permitAll()
            }

        http
            .addFilterBefore(JWTFilter(jwtUtil, responseWriter), LoginFilter::class.java)
            .addFilterAt(LoginFilter(authenticationManager, jwtUtil, responseWriter, refreshService), UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(CustomLogoutFilter(refreshService, jwtUtil, responseWriter), LogoutFilter::class.java )

        return http.build()
    }

}