package team.goodpeople.security.jwt.annotation

import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import team.goodpeople.global.exception.CustomErrorCode
import team.goodpeople.global.exception.GlobalException
import team.goodpeople.security.jwt.JWTUtil
import team.goodpeople.security.jwt.dto.UserInfoDto

@Component
class GetUserIdArgumentResolver(
    private val jwtUtil: JWTUtil
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(GetUserId::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Long {

        val request = webRequest.nativeRequest as HttpServletRequest
        val userInfo = request.getAttribute("user") as? UserInfoDto
            ?: throw GlobalException(CustomErrorCode.INVALID_INFORMATION)

        println(userInfo)
        return userInfo.userId

//        /** 요청의 Authorization 헤더에서 사용자의 id를 추출한다. */
//        val request = webRequest.nativeRequest as HttpServletRequest
//
//        val accessToken = jwtUtil.resolveAccessToken(request)
//            ?: throw GlobalException(CustomErrorCode.PARSE_FAILED)
//
//        val userInfo = jwtUtil.getClaim(accessToken, "user", UserInfoDto::class.java)
//
//        return userInfo.userId
    }
}