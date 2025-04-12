package team.goodpeople.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import team.goodpeople.security.jwt.JWTUtil
import team.goodpeople.security.jwt.LoginFilter

@EnableWebSecurity
@Configuration
class SecurityConfig(
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val jwtUtil: JWTUtil
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

        http
            .authorizeHttpRequests {
                it
                    .requestMatchers("/", "/api/login").permitAll()
                    .requestMatchers("/api/admin", "/api/admin/**").hasRole("ADMIN")
                    .requestMatchers(("/api/user/**")).permitAll()
                    .anyRequest().permitAll()
            }

        http
            .addFilterAt(LoginFilter(authenticationManager, jwtUtil), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

}