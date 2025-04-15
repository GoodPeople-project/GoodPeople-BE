package team.goodpeople.global.exception

import org.springframework.security.access.AccessDeniedException

class JWTAccessDeniedException(
    message: String
) : AccessDeniedException(message){
}