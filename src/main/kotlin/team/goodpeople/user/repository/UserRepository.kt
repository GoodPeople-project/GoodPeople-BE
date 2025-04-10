package team.goodpeople.user.repository

import org.springframework.data.repository.CrudRepository
import team.goodpeople.user.entity.User

interface UserRepository : CrudRepository<User, Long> {

    fun existsByUsername(username: String): Boolean

    fun existsByNickname(nickname: String): Boolean

}