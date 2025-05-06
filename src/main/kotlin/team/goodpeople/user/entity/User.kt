package team.goodpeople.user.entity

import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import team.goodpeople.common.BaseEntity
import team.goodpeople.script.entity.UserScript

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
     * - DB에서는 null이 될 수 없음을 명시했다.
     * - 조회 가능 / 수정 불가능
     */
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    @field:Column(name = "id", nullable = false)
    val id: Long? = null,

    /**
     * @property username
     * - 5자 ~ 100자
     * - 폼 로그인 회원가입 시, ID로 사용하는 이메일 값을 저장한다.
     * - OAuth2 로그인 시, 식별자를 가져와 저장한다.
     * - 조회 가능 / 수정 불가능
     */
    @field:NotNull
    @field:Size(min = 5, max = 100)
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
    @field:Nullable
    @field:Size(max = 100)
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

    /**
     * @property loginType
     * - 로그인 타입을 구분한다.
     * - 폼 로그인 -> "FORM"
     * - 소셜 로그인(OAuth2) -> "SOCIAL"
     * */
    @field:NotNull
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "login_type")
    val loginType: LoginType,

    @field:OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    val userScripts: List<UserScript> = emptyList(),

    ) : BaseEntity() {
    @field:Nullable
    @field:Size(min = 8)
    @field:Column(name = "password")
    var password = password
        private set

    companion object {
        /** 폼 회원가입 팩토리 메서드 */
        fun signUpWithForm(username: String, password: String, nickname: String): User {
            return User(
                username = username,
                password = password,
                nickname = nickname,
                email = "",
                loginType = LoginType.FORM
            )
        }

        /** OAuth2 회원가입 팩토리 메서드 */
        fun signUpWithOAuth2(username: String, password: String, nickname: String, email: String): User {
            return User(
                username = username,
                password = password,
                nickname = nickname,
                email = email,
                loginType = LoginType.SOCIAL
            )
        }
    }

    fun updateNickname(nickname: String) {
        this.nickname = nickname
    }

    fun updatePassword(password: String) {
        this.password = password
    }


    /** 필수 보조 생성자 */
    protected constructor() : this(
        id = null,
        username = "",
        password = "",
        nickname = "",
        email = "",
        loginType = LoginType.FORM
    )
}
