package team.goodpeople.security.auth

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import team.goodpeople.common.BaseDetails
import team.goodpeople.user.entity.User

class CustomUserDetails(
    private val username: String,
    private val password: String,
    private val userId: Long,
    private val role: String,
    private val loginType: String
) : UserDetails, BaseDetails {

    /** 필수 구현 메서드 */
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority(this.role))}

    override fun getPassword(): String = this.password

    override fun getUsername(): String = this.username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

    /** 공통 속성 관리 */
    override fun getUserId(): Long = this.userId
    override fun getRole(): String = this.role
    override fun getLoginType(): String = this.loginType
}