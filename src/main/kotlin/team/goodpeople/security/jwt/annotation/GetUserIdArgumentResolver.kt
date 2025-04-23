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
            ?: throw GlobalException(CustomErrorCode.INVALID_ATTRIBUTE)

        return userInfo.userId
    }
}