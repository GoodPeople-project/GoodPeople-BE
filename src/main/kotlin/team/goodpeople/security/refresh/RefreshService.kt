package team.goodpeople.security.refresh

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import team.goodpeople.security.jwt.JWTConstants.REFRESH_EXPIRED_MS
import java.time.Duration

@Service
class RefreshService(
    @Qualifier("redisRefreshTemplate")
    private val redisTemplate: RedisTemplate<String, String>
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

}