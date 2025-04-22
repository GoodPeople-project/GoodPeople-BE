package team.goodpeople.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import team.goodpeople.security.AuthConstants.ACCESS_CATEGORY
import team.goodpeople.security.AuthConstants.ACCESS_EXPIRED_MS
import team.goodpeople.security.AuthConstants.REFRESH_CATEGORY
import team.goodpeople.security.AuthConstants.TOKEN_TYPE
import team.goodpeople.security.AuthConstants.REFRESH_EXPIRED_MS
import team.goodpeople.security.jwt.dto.UserInfoDto
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Component
class JWTUtil(
    @Value("\${jwt.secret}") private val secret: String,
    private val objectMapper: ObjectMapper
) {

    private val secretKey: SecretKey =
        SecretKeySpec(Base64.getDecoder().decode(secret), "HmacSHA256")

    /** JWT 생성 */
    fun createAccessToken(
        userInfo: UserInfoDto,
    ): String {

        return Jwts.builder()
            .claim("type", TOKEN_TYPE)
            .claim("cat", ACCESS_CATEGORY)
            .claim("user", userInfo)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis()  + ACCESS_EXPIRED_MS))
            .signWith(secretKey)
            .compact()
    }

    fun createRefreshToken(
        userInfo: UserInfoDto,
    ): String {

        return Jwts.builder()
            .claim("type", TOKEN_TYPE)
            .claim("cat", REFRESH_CATEGORY)
            .claim("user", userInfo)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis()  + REFRESH_EXPIRED_MS))
            .signWith(secretKey)
            .compact()
    }

    /** Claim 추출 */
    fun getClaim(
        jwtToken: String,
        claim: String
    ): String {

        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(jwtToken)
            .payload
            .get(claim, String::class.java)
    }

    // String 이외의 타입 오버로딩
    fun <T> getClaim(
        jwtToken: String,
        claim: String,
        clazz: Class<T>
    ): T {

        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(jwtToken)
            .payload
            .get(claim, clazz)
    }

    /** 토큰에 담긴 사용자 정보를 파싱하는 메서드 */
    fun <T> getNestedClaim(
        jwtToken: String,
        claim: String,
        clazz: Class<T>
    ): T {
        val nestedClaim = getClaim(jwtToken, claim, LinkedHashMap::class.java)
        val convertedClaim = objectMapper.convertValue(nestedClaim, clazz)

        return convertedClaim
    }

    fun isAccessToken(
        jwtToken: String
    ): Boolean {

        return getClaim(jwtToken, "cat") == ACCESS_CATEGORY
    }

    fun isRefreshToken(
        jwtToken: String
    ): Boolean {

        return getClaim(jwtToken, "cat") == REFRESH_CATEGORY
    }

    fun isTokenExpired(
        jwtToken: String
    ): Boolean {

        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(jwtToken)
            .payload
            .expiration
            .before(Date(System.currentTimeMillis()))
    }

    fun convertTokenToResponse(
        accessToken: String,
        refreshToken: String
    ): Map<String, Any> {

        return mapOf(
            "access_token" to accessToken,
            "token_type" to TOKEN_TYPE,
            "expires_in" to ACCESS_EXPIRED_MS,
            "refresh_token" to refreshToken
        )
    }

    fun resolveAccessToken(request: HttpServletRequest): String? {
        val authHeader = request.getHeader("Authorization")

        val accessToken = authHeader
            ?.takeIf { it.startsWith("$TOKEN_TYPE ") }
            ?.removePrefix("$TOKEN_TYPE ")

        return accessToken
    }
}