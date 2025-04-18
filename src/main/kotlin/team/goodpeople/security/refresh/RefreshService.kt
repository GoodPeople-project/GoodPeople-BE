package team.goodpeople.security.refresh

import io.jsonwebtoken.JwtException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import team.goodpeople.global.response.ApiResponse
import team.goodpeople.global.response.ResponseWriter
import team.goodpeople.security.jwt.CookieUtil.Companion.getStringFromCookies
import team.goodpeople.security.jwt.CookieUtil.Companion.sendCookie
import team.goodpeople.security.jwt.JWTConstants.REFRESH_EXPIRED_MS
import team.goodpeople.security.jwt.JWTUtil
import java.time.Duration

@Service
class RefreshService(
    @Qualifier("redisRefreshTemplate")
    private val redisTemplate: RedisTemplate<String, String>,
    private val jwtUtil: JWTUtil,
    private val responseWriter: ResponseWriter,
) {
    /**
     * Redis에 저장될 Refresh Token
     * - 저장 기간은 JWT Constants를 참조
     * - TODO: 저장 형식
     * 현재 사용자 인증 시에는 username을 사용하므로, prefix + username 형식으로 키를 설정했다.
     *
     * 사용자 id 등을 Value로 해서 검증해도 될 것 같다.
     * 더 나은 방식을 고민해볼 것
     * */
    // Key에 사용할 prefix
    private val prefix = "refresh_token:"

    // Refresh Token 저장
    fun saveRefreshToken(
        username: String,
        refreshToken: String,
    ) {
        val key = prefix + username

        redisTemplate.opsForValue()
            .set(key, refreshToken)

        redisTemplate.expire(key, Duration.ofMillis(REFRESH_EXPIRED_MS))
    }

    // 저장된 Refresh Token 조회
    // 없을 경우 빈 문자열을 반환
    fun getRefreshToken(
        username: String
    ): String {
        val key = prefix + username

        return redisTemplate.opsForValue().get(key) ?: ""
    }

    // 저장된 Refresh Token 삭제
    fun deleteRefreshToken(
        username: String
    ) {
       val key = prefix + username

       redisTemplate.delete(key)
    }

    /** Access Token 재발급 */
    fun reissueAccessToken(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Boolean {
        /** 쿠키에서 기존 Refresh Token 추출 */
        val oldRefreshToken: String = getStringFromCookies(request, "refresh_token")
            ?: throw JwtException("No Refresh Token found")

        /** username으로 Redis에 저장된 Refresh Token을 조회한다. */
        val username = jwtUtil.getClaim(oldRefreshToken, "username")
        val savedRefreshToken = getRefreshToken(username)

        /** 요청의 Refresh Token과 값 비교 */
        if (savedRefreshToken != oldRefreshToken) {
            throw JwtException("Refresh Token does not match")
        }

        val userId = jwtUtil.getClaim(oldRefreshToken, "id", Integer::class.java).toLong()
        val role = jwtUtil.getClaim(oldRefreshToken, "role")

        val newAccessToken = jwtUtil.createAccessToken(
            userId = userId,
            username = username,
            role = role
        )

        val newRefreshToken = jwtUtil.createRefreshToken(
            userId = userId,
            username = username,
            role = role
        )

        /** 발급한 Refresh Token은 Redis에 저장한다. */
        // TODO: Redis에 저장된 토큰의 만료 시간과 실제 만료 시간이 일치하는지 확인할 것.
        saveRefreshToken(username, newRefreshToken)

        /** Access / Refresh Token 모두 body에 담아 응답한다. */
        val result = jwtUtil.convertTokenToResponse(newAccessToken, newRefreshToken)

        responseWriter.writeJsonResponse(
            response = response,
            body = ApiResponse.success(
                result = result
            )
        )

        /** Refresh Token을 쿠키에 담아 응답한다. */
        sendCookie(
            response = response,
            key = "refresh_token",
            value = oldRefreshToken)

        return true
    }
}