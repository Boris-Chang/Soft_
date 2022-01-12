package ru.ifmo.software_engineering.afterlife.utils

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(locations= ["classpath:application.properties"])
annotation class IntegrationTest
