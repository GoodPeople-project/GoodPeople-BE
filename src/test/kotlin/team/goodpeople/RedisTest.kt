package team.goodpeople

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.ActiveProfiles
import team.goodpeople.global.config.RedisConfig
import team.goodpeople.security.refresh.RefreshService
import kotlin.test.AfterTest
import org.junit.jupiter.api.Test


@DataRedisTest
@ActiveProfiles("test")
@Import(RedisConfig::class, RefreshService::class)
class RedisTest() {

    /** Setting */
    @Autowired
    private lateinit var refreshService: RefreshService

    @Qualifier("redisRefreshTemplate")
    @Autowired
    private lateinit var redisRefreshTemplate: RedisTemplate<String, String>

    private val testUser = "testUser"
    private val testToken = "testRefresh"

    @AfterTest
    fun cleanup() {
        redisRefreshTemplate.delete("refresh_token:$testUser")
    }

    /** Test */
    @Test
    @DisplayName("Redis_저장소에_데이터를_저장한다")
    fun testSaveAndGetToken() {

        //given
        refreshService.saveRefreshToken(testUser, testToken)

        //when
        val result = refreshService.getRefreshToken(testUser)

        //then
        assertThat(result).isEqualTo(testToken)
    }

    @Test
    @DisplayName("Redis_저장소에_저장된_데이터를_삭제한다")
    fun testDeleteToken() {

        // given
        refreshService.saveRefreshToken(testUser, testToken)

        // when
        refreshService.deleteRefreshToken(testUser)
        val result = refreshService.getRefreshToken(testUser)

        // then
        assertThat(result).isEmpty()
    }

    // TODO: JWT의 만료 시간, Redis 내부에서의 만료 시간 비교 테스트
}