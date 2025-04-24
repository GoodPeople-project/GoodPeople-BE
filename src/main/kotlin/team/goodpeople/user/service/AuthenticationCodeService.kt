package team.goodpeople.user.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import team.goodpeople.security.AuthConstants.EMAIL_CODE_EXPIRED_MS
import java.time.Duration

@Service
class AuthenticationCodeService(
    @Qualifier("redisAuthenticationCodeTemplate")
    private val redisTemplate: RedisTemplate<String, String>
) {

    // Key에 사용할 prefix
    private val email_auth_prefix = "email_auth_code:"

    // TODO: 키값을 인증 요청 시간으로 사용

    // 이메일 인증 번호 저장
    fun saveEmailAuthCode(
        email: String,
        emailAuthCode: String,
    ) {
        val key = email_auth_prefix + email

        redisTemplate.opsForValue()
            .set(key, emailAuthCode)

        redisTemplate.expire(key, Duration.ofMillis(EMAIL_CODE_EXPIRED_MS))
    }

    fun getEmailAuthCode(
        email: String,
    ): String {
        val key = email_auth_prefix + email

        return redisTemplate.opsForValue().get(key) ?: ""
    }

    fun deleteEmailAuthCode(
        email: String
    ) {
        val key = email_auth_prefix + email

        redisTemplate.delete(key)
    }
}