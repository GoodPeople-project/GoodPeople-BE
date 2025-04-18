package team.goodpeople.security.jwt

import io.jsonwebtoken.Jwts
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import team.goodpeople.security.jwt.JWTConstants.ACCESS_CATEGORY
import team.goodpeople.security.jwt.JWTConstants.ACCESS_EXPIRED_MS
import team.goodpeople.security.jwt.JWTConstants.REFRESH_CATEGORY
import team.goodpeople.security.jwt.JWTConstants.TOKEN_TYPE
import team.goodpeople.security.jwt.JWTConstants.REFRESH_EXPIRED_MS
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Component
class JWTUtil(
    @Value("\${jwt.secret}") private val secret: String
) {

    private val secretKey: SecretKey =
        SecretKeySpec(Base64.getDecoder().decode(secret), "HmacSHA256")

    /** JWT 생성 */
    fun createAccessToken(
        userId: Long,
        username: String,
        role: String
    ): String {

        return Jwts.builder()
            .claim("type", TOKEN_TYPE)
            .claim("cat", ACCESS_CATEGORY)
            .claim("id", userId)
            .claim("username", username)
            .claim("role", role)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis()  + ACCESS_EXPIRED_MS))
            .signWith(secretKey)
            .compact()
    }

    fun createRefreshToken(
        userId: Long,
        username: String,
        role: String
    ): String {

        return Jwts.builder()
            .claim("type", TOKEN_TYPE)
            .claim("cat", REFRESH_CATEGORY)
            .claim("id", userId)
            .claim("username", username)
            .claim("role", role)
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