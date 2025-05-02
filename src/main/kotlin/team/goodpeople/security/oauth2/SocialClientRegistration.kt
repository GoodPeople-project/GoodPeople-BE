package team.goodpeople.security.oauth2

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames
import org.springframework.stereotype.Component
import team.goodpeople.security.AuthConstants.DEPLOY_BASE_URL

@Component
class SocialClientRegistration(
    // NAVER
    @Value("\${spring.security.oauth2.client.registration.naver.client-id}")
    val naverClientId: String,
    @Value("\${spring.security.oauth2.client.registration.naver.client-secret}")
    val naverClientSecret: String,

    // KAKAO
    @Value("\${spring.security.oauth2.client.registration.kakao.client-id}")
    val kakaoClientId: String,

    // GOOGLE
    @Value("\${spring.security.oauth2.client.registration.google.client-id}")
    val googleClientId: String,
    @Value("\${spring.security.oauth2.client.registration.google.client-secret}")
    val googleClientSecret: String,
) {
    private val redirectUri: String = "$DEPLOY_BASE_URL/login/oauth2/code/{registrationId}"

    fun naverClientRegistration(): ClientRegistration {
        return ClientRegistration
            .withRegistrationId("naver")
            .clientId(naverClientId)
            .clientSecret(naverClientSecret)
            .redirectUri(redirectUri)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .scope("name", "email")
            .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
            .tokenUri("https://nid.naver.com/oauth2.0/token")
            .userInfoUri("https://openapi.naver.com/v1/nid/me")
            .userNameAttributeName("response")
            .clientName("Naver")
            .build()
    }

    fun kakaoClientRegistration(): ClientRegistration {
        return ClientRegistration.withRegistrationId("kakao")
            .clientId(kakaoClientId)
            .redirectUri(redirectUri)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .scope("account_email", "profile_nickname")
            .authorizationUri("https://kauth.kakao.com/oauth/authorize")
            .tokenUri("https://kauth.kakao.com/oauth/token")
            .userInfoUri("https://kapi.kakao.com/v2/user/me")
            .userNameAttributeName("id")
            .clientName("Kakao")
            .build()
    }

    fun googleClientRegistration(): ClientRegistration {
        return ClientRegistration.withRegistrationId("google")
            .clientId(googleClientId)
            .clientSecret(googleClientSecret)
            .redirectUri(redirectUri)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .scope("profile", "email")
            .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
            .tokenUri("https://oauth2.googleapis.com/token")
            .jwkSetUri("https://www.googleapis.com/oauth2/v2/jwks")
            //.jwkSetUri("https://www.googleapis.com/oauth2/v2/certs")
            .issuerUri("https://accounts.google.com")
            .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
            .userNameAttributeName(IdTokenClaimNames.SUB)
            .clientName("Google")
            .build()
    }
}