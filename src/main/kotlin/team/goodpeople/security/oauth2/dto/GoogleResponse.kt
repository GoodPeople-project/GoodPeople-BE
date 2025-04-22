package team.goodpeople.security.oauth2.dto

class GoogleResponse(
    attribute: Map<String, Any>
) : OAuth2Response {

    private val attribute = attribute as? Map<String, Any>
        ?: throw Exception("Response attribute not found")

    override fun getProvider(): String = "google"

    override fun getProviderId(): String = attribute["sub"] as String

    override fun getEmail(): String = attribute["email"] as String

    override fun getName(): String = attribute["name"] as String
}