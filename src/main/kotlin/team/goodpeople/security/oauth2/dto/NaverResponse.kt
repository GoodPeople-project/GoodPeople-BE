package team.goodpeople.security.oauth2.dto

class NaverResponse(
    attribute: Map<String, Any>
) : OAuth2Response {

    private val attribute = attribute["response"] as? Map<String, Any>
        ?: throw Exception("Response attribute not found")

    override fun getProvider(): String = "naver"

    override fun getProviderId(): String = attribute["id"] as String

    override fun getEmail(): String = attribute["email"] as String

    override fun getName(): String = attribute["name"] as String
}