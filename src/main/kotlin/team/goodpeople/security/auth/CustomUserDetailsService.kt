package team.goodpeople.security.auth

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import team.goodpeople.user.repository.UserRepository

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails? {

        val user = userRepository.findUserByUsername(username)

        if (user != null) {
            return CustomUserDetails(user)
        }

        return null
    }
}