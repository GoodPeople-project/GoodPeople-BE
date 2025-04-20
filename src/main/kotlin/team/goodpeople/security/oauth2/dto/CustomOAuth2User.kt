package team.goodpeople.security.oauth2.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User
import team.goodpeople.user.entity.User

class CustomOAuth2User(
    private val user: User,
//    private val attributes: Map<String, Any>,
) : OAuth2User {

    /** 필수 구현 메서드 */
    override fun getAttributes(): Map<String, Any> = attributes

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority(user.role.name))
    }

    override fun getName(): String = user.username

    fun getUser(): User = this.user
}