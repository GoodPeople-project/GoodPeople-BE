package team.goodpeople.security.oauth2

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.stereotype.Component
import team.goodpeople.security.AuthConstants.DEVELOP_BASE_URL

@Component
class SocialClientRegistration(
    // NAVER
    @Value("\${spring.security.oauth2.client.registration.naver.client-id}")
    val naverClientId: String,
    @Value("\${spring.security.oauth2.client.registration.naver.client-secret}")
    val naverClientSecret: String,
    // TODO
    //  KAKAO
    //  GOOGLE

) {
    private val redirectUri: String = "$DEVELOP_BASE_URL/login/oauth2/code/{registrationId}"

    //  TODO: 헬퍼 클래스 ClientRegistrations 있으나, 사용법 미숙지
    // 네이버 클라이언트 정보
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
            .build();
    }
}