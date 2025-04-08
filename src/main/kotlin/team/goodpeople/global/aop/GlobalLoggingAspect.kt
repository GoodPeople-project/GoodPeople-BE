package team.goodpeople.global.aop

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class GlobalLoggingAspect {

    private val log: Logger = LoggerFactory.getLogger(GlobalLoggingAspect::class.java)

    @Pointcut("execution(* team.goodpeople..*(..))")
    private fun globalPointcut() {}

    @Before("globalPointcut()")
    fun logBeforeMethod(joinPoint: JoinPoint) {
        val methodName: String = joinPoint.getSignature().toShortString()
        val args: Array<Any> = joinPoint.getArgs()

        log.info("[실행 메서드]: {} [매개변수]: {}", methodName, args.contentToString())
    }

    @AfterReturning(value = "globalPointcut()", returning = "result")
    fun logAfterMethod(joinPoint: JoinPoint, result: Any?) {
        val methodName: String = joinPoint.getSignature().toShortString()

        log.info("[종료 메서드]: {} [반환값]: {}", methodName, result)
    }

    @AfterThrowing(value = "globalPointcut()", throwing = "ex")
    fun logAfterThrowing(joinPoint: JoinPoint, ex: Throwable) {
        val methodName: String = joinPoint.getSignature().toShortString()

        log.error("[예외 발생 메서드]: {} [예외]: {}", methodName, ex.message)
    }
}