package team.goodpeople.security.oauth2.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User
import team.goodpeople.common.BaseDetails
import team.goodpeople.user.entity.User

class CustomOAuth2User(
    private val username: String,
    private val userId: Long,
    private val role: String,
    private val loginType: String,
    private val attributes: Map<String, Any>,
) : OAuth2User, BaseDetails {

    /** 필수 구현 메서드 */
    override fun getAttributes(): Map<String, Any> = attributes

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority(this.role))
    }

    override fun getName(): String = this.username

    /** 공통 속성 관리 */
    override fun getUsername(): String = this.username
    override fun getUserId(): Long = this.userId
    override fun getRole(): String = this.role
    override fun getLoginType(): String = this.loginType
}