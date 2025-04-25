package team.goodpeople.python

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader

@Component
class PythonCachecInitializer(
    @Value("\${spring.python.path}") private val pythonPath: String
) {
    private val logger = LoggerFactory.getLogger(PythonCachecInitializer::class.java)

    private val prepareCacheScript = "scripts/prepare_law_cache.py"

    @PostConstruct
    fun init() {
        try {
            logger.warn("[캐싱 시작]: $prepareCacheScript")

            val process = ProcessBuilder(listOf(pythonPath, prepareCacheScript))
                .redirectErrorStream(true)
                .start()

            val reader = BufferedReader(InputStreamReader(process.inputStream, Charsets.UTF_8))
            val output = StringBuilder()

            reader.lineSequence().forEach { line ->
                logger.info("[Python Cache] $line")
                output.appendLine(line)
            }

            process.waitFor()
            logger.info("[캐싱 완료]")
        } catch (e: Exception) {
            logger.error("캐싱 실패", e)
        }
    }
}