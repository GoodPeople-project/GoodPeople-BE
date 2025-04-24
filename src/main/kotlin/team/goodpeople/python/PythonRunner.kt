package team.goodpeople.python

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader

@Component
class PythonRunner(
    @Value("\${spring.python.path}")
    private val pythonPath: String,
) {
    private val log: Logger = LoggerFactory.getLogger(PythonRunner::class.java)

    /** 실행할 파이썬 스크립트 */
    private val similarityModelScript =  "scripts/model_similarity.py"

    /** inputScript를 사용자에게 받아 유사 판례 반환 스크립트를 실행한다. */
    fun runSimilarity(
        pythonScriptRequest: PythonScriptRequest,
    ): String {
        val startTime = System.currentTimeMillis()

        val inputScript = pythonScriptRequest.inputScript
        val scriptPath = similarityModelScript
        val command = listOf(pythonPath, scriptPath, inputScript)

        checkTime("1", startTime)

        val processBuilder = ProcessBuilder(command)
        processBuilder.redirectErrorStream(true)

        var result = ""

        checkTime("2", startTime)

        try {
            checkTime("3", startTime)
            val process = processBuilder.start()

            checkTime("4", startTime)
            // ✅ UTF-8로 명시적으로 인코딩 지정
            val reader = BufferedReader(InputStreamReader(process.inputStream, Charsets.UTF_8))

            checkTime("5", startTime)
            result = reader.readText()

            checkTime("6", startTime)
            process.waitFor() // 종료될 때까지 대기

            checkTime("7", startTime)
            result.trim()
        } catch (e: Exception) {
            "실행 실패: ${e.message}"
        }
        checkTime("8", startTime)

        val totalTime = (System.currentTimeMillis() - startTime).toString()
        log.warn("소요 시간: $totalTime")

        return result
    }

    private fun checkTime(
        tag: String,
        timeStandard: Long
    ) {
        val spent = (System.currentTimeMillis() - timeStandard) / 1000.0

        log.warn("$tag 소요 시간: $spent 초")
    }
}
