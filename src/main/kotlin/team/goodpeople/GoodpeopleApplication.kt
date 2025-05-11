package team.goodpeople

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class GoodpeopleApplication

fun main(args: Array<String>) {
    runApplication<GoodpeopleApplication>(*args)
}
