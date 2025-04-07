package team.goodpeople

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GoodpeopleApplication

fun main(args: Array<String>) {
    runApplication<GoodpeopleApplication>(*args)
}
