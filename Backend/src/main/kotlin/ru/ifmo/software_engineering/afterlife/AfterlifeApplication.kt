package ru.ifmo.software_engineering.afterlife

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = arrayOf(R2dbcAutoConfiguration::class))
class AfterlifeApplication

fun main(args: Array<String>) {
    runApplication<AfterlifeApplication>(*args)
}
