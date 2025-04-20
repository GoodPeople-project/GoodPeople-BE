package team.goodpeople.user.entity

import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import team.goodpeople.common.BaseEntity

/**
 * User Entity
 *
 * User는 두 가지 방식으로 로그인 할 수 있다.
 * - 폼 로그인
 * - oAuth2 로그인 (소셜 로그인)
 * */

@Entity
class User private constructor (

    /**
     * @property id
     * - DB 관리용 ID
     * - Long 타입의 자동 생성 값이다.
     * - 조회 가능 / 수정 불가능
     */
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    @field:Column(name = "id")
    val id: Long? = null,

    /**
     * @property username
     * - 5자 ~ 50자
     * - 폼 로그인 회원가입 시, ID로 사용한다.
     * - OAuth2 로그인 시, username을 받아온다.
     * - 조회 가능 / 수정 불가능
     */
    @field:NotNull
    @field:Size(min = 5, max = 50)
    @field:Column(name = "username", unique = true)
    val username: String,

    /**
     * @property password
     * - 8자 ~ 30자
     * - 폼 로그인에 사용하는 비밀번호다.
     * - OAuth2 로그인 시에는 사용하지 않으므로 Nullable 처리한다.
     * - 조회 가능 / 수정 가능 - setter 숨김
     */
    password: String,

    /**
     * @property nickname
     * - 커뮤니티에서 사용할 닉네임이다.
     * - 조회 가능 / 수정 가능
     */
    @field:NotNull
    @field:Size(min = 2, max = 10)
    @field:Column(name = "nickname", unique = true)
    var nickname: String,

    /**
     * @property email
     * - 정보성 메일 수신 등, 서비스 이용을 위해 사용한다.
     * - 조회 가능 / 수정 가능
     */
    @field:NotNull
    @field:Size(max = 100)
    @field:Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    @field:Column(name = "email", unique = true)
    var email: String,

    /**
     * @property role
     * - 사용자 권한.
     * - 크게는 관리자/일반 사용자, 추후 기능 확장으로 권한 추가
     * - 조회 가능 / 수정 가능
     */
    @field:NotNull
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "role")
    var role: Role = Role.ROLE_USER,

//    TODO: LoginType 프로퍼티 추가
//    @field:NotNull
//    @field:Enumerated(EnumType.STRING)
//    @field:Column(name = "login_type")
//    val loginType: LoginType

) : BaseEntity() {
    @field:Nullable
    @field:Size(min = 8)
    @field:Column(name = "password")
    var password = password
        private set

    /** 기본 생성자 */
    protected constructor() : this(
        id = null,
        username = "",
        password = "",
        nickname = "",
        email = "",
//        loginType = LoginType.FORM
    )

    companion object {
        /** 팩토리 메서드 사용으로 생성자 노출 방지 */
        fun signUpWithForm(username: String, password: String, nickname: String, email: String): User {
            return User(
                id = null,
                username = username,
                password = password,
                nickname = nickname,
                email = email,
//                loginType = LoginType.FORM
            )
        }

        /** JWT 생성을 위한 팩토리 메서드 */
        // TODO: DTO 클래스 새로 만들어서 사용할 것
//        fun createEntityForJWT(id: Long, username: String, role: Role): User
//        {
//            return User(
//                id = id,
//                username = username,
//                password = "",
//                nickname = "",
//                email = "",
//                role = role)
//        }
    }

    fun updateNickname(nickname: String) {
        this.nickname = nickname
    }

    fun updatePassword(password: String) {
        this.password = password
    }
}
