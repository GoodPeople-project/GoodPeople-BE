package team.goodpeople.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.StringRedisSerializer

@EnableRedisRepositories
@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.host}") private val hostname: String,
    @Value("\${spring.data.redis.port}") private val port: Int,
    @Value("\${spring.data.redis.password}") private val password: String,
) {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val configuration = RedisStandaloneConfiguration(hostname, port)
        configuration.setPassword(password)

        return LettuceConnectionFactory(configuration)
    }

    @Bean
    fun redisRefreshTemplate(): RedisTemplate<String, String> {
        val refreshTemplate = RedisTemplate<String, String>()

        refreshTemplate.apply {
            connectionFactory = redisConnectionFactory()
            keySerializer = StringRedisSerializer()
            valueSerializer = StringRedisSerializer()
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = StringRedisSerializer()
        }

        return refreshTemplate
    }

    @Bean
    fun redisAuthenticationCodeTemplate(): RedisTemplate<String, String> {
        val authenticationCodeTemplate = RedisTemplate<String, String>()

        authenticationCodeTemplate.apply {
            connectionFactory = redisConnectionFactory()
            keySerializer = StringRedisSerializer()
            valueSerializer = StringRedisSerializer()
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = StringRedisSerializer()
        }

        return authenticationCodeTemplate
    }
}