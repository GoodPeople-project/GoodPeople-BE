package team.goodpeople.python

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDateTime

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
        val startTime = System.currentTimeMillis() /** 로깅 시작 */

        val inputScript = pythonScriptRequest.inputScript
        val scriptPath = similarityModelScript
        val command = listOf(pythonPath, scriptPath, inputScript)

        checkTime("1", startTime)

        val processBuilder = ProcessBuilder(command)
        processBuilder.redirectErrorStream(true)

        var result = ""

        checkTime("2", startTime)

        try {
            /** 파이썬 프로세스 시작 */
            checkTime("3", startTime)
            val process = processBuilder.start()

            /** 파이썬의 stdout 스트림과 연결 */
            checkTime("4", startTime)
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

        val totalTime = ((System.currentTimeMillis() - startTime) / 1000.0).toString()
        log.warn("전체 소요 시간: $totalTime 초")

        return result
    }

    private fun checkTime(
        tag: String,
        timeStandard: Long
    ) {
        val spent = (System.currentTimeMillis() - timeStandard) / 1000.0
        val time = LocalDateTime.now().toString()

        log.warn("$tag 소요 시간: $spent 초, $time")
    }
}
