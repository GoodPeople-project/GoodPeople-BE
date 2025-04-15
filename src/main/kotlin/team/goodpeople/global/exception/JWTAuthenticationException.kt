package team.goodpeople.global.exception

import org.springframework.security.core.AuthenticationException

class JWTAuthenticationException(
    message: String
) : AuthenticationException(message) {
}