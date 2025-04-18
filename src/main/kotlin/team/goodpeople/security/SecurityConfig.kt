package team.goodpeople.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
import team.goodpeople.security.refresh.RefreshService

@EnableWebSecurity
@Configuration
class SecurityConfig(
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val jwtUtil: JWTUtil,
    private val refreshService: RefreshService,
    private val jwtAccessDeniedHandler: JWTAccessDeniedHandler,
    private val jwtAuthenticationEntryPoint: JWTAuthenticationEntryPoint,
    private val responseWriter: ResponseWriter
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

        http
            .exceptionHandling {
                it
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    .accessDeniedHandler(jwtAccessDeniedHandler)
            }

        http
            .authorizeHttpRequests {
                it
                    .requestMatchers("/api/auth/**", "/").permitAll()
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