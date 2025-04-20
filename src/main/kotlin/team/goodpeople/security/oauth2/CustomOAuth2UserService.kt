package team.goodpeople.security.oauth2

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import team.goodpeople.security.oauth2.dto.CustomOAuth2User
import team.goodpeople.security.oauth2.dto.NaverResponse
import team.goodpeople.user.entity.User
import team.goodpeople.user.repository.UserRepository

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository
) : DefaultOAuth2UserService() {

    override fun loadUser(
        userRequest: OAuth2UserRequest
    ): OAuth2User {

        /** 클라이언트 서버에서 사용자 정보 획득 */
        val oAuth2User = super.loadUser(userRequest)
        val registrationId = userRequest.clientRegistration.registrationId

        val oAuth2Response = when(registrationId) {
            "naver" -> NaverResponse(oAuth2User.attributes)
            else -> null
        }

        // TODO: 예외처리
        if (oAuth2Response == null) {
            throw Exception()
        }

        val username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId()
        val savedUser = userRepository.findUserByUsername(username)

        if (savedUser == null) {
            // TODO: 유저 생성
            // TODO: 보안 문제. password 프로퍼티가 존재하므로 폼 로그인이 가능하긴 하다. 테이블을 나누거나 폼 로그인에서 따로 처리가 필요할 듯
            val newUser = User.signUpWithOAuth2(
                username = username,
                password = "",
                nickname = getTempNickname(),
                email = oAuth2Response.getEmail(),
            )

            userRepository.save(newUser)

            return CustomOAuth2User(newUser)
        }
        else {

            return CustomOAuth2User(savedUser)
        }
    }


    // 임시 닉네임 생성
    fun getTempNickname(): String {
        val charSet = charArrayOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'
        )

        var tempNickname: String
        do {
            val nicknameBuilder = StringBuilder()
            for (i in 0..9) {
                val idx = (charSet.size * Math.random()).toInt()
                nicknameBuilder.append(charSet[idx])
            }
            tempNickname = nicknameBuilder.toString()
        } while (userRepository.existsByNickname(tempNickname))

        return tempNickname
    }
}