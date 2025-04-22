package team.goodpeople.mail.service

import org.springframework.mail.MailException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import team.goodpeople.global.exception.CustomErrorCode
import team.goodpeople.global.exception.GlobalException

@Service
class SendEmailService(
    private val mailSender: JavaMailSender,
) {
    private val serverEmail = "hth130598@gmail.com".trim()

    fun createMail(
        to: String,
        subject: String,
        content: String,
    ): SimpleMailMessage {

        val toAddress = to.trim()
        if (toAddress.isEmpty() || !isValidEmail(toAddress)) {
            // TODO: 예외처리
            throw GlobalException(CustomErrorCode.EXAMPLE_ERROR)
        }

        val mailMessage = SimpleMailMessage()
        mailMessage.setFrom(serverEmail)
        mailMessage.setTo(toAddress)
        mailMessage.setSubject(subject)
        mailMessage.setText(content)

        return mailMessage
    }

    fun sendMail(mailMessage: SimpleMailMessage) {

        try {
            mailSender.send(mailMessage)
        } catch (e: MailException) {
            // TODO: 예외처리
            throw GlobalException(CustomErrorCode.EXAMPLE_ERROR)
        }
    }

    private fun isValidEmail(
        email: String
    ): Boolean {
        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        return email.matches(emailRegex.toRegex())
    }
}