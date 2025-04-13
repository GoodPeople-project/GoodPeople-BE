package team.goodpeople.security.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean

//@Component
class CustomLogoutFilter(

) : GenericFilterBean() {

    override fun doFilter(p0: ServletRequest, p1: ServletResponse, p2: FilterChain) {

         // TODO: 유효한 로그아웃 URL인지 검증


         // TODO: Request에서 Refresh Token 추출


         // TODO: Refresh Token 유효성 검증


         // TODO: Request의 Refresh Token으로, Redis 내부의 동일한 토큰을 조회


         // TODO: Redis 내부의 Refresh Token 삭제.

    }


}