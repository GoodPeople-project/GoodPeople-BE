package team.goodpeople.security.auth

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import team.goodpeople.user.repository.UserRepository

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {

        val user = userRepository.findUserByUsername(username)
            ?: throw UsernameNotFoundException("사용자를 찾을 수 없습니다.")

        val userDetails = CustomUserDetails(
            username = user.username,
            password = user.password,
            userId = user.id ?: throw UsernameNotFoundException(username),
            role = user.role.toString(),
            loginType = user.loginType.toString()
        )

        return userDetails
    }
}