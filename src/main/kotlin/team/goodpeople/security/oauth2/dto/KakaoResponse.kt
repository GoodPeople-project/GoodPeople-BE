package team.goodpeople.security.oauth2.dto

class KakaoResponse(
    attribute: Map<String, Any>
) : OAuth2Response {

    private val attribute = attribute as? Map<String, Any>
        ?: throw Exception("Response attribute not found")

    override fun getProvider(): String = "kakao"

    override fun getProviderId(): String = attribute["id"] as String

    override fun getEmail(): String = attribute["kakao_account"] as String

    override fun getName(): String = attribute["nickname"] as String
}