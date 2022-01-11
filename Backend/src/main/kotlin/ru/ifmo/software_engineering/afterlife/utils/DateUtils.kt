package ru.ifmo.software_engineering.afterlife.utils

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

val UtcZone: ZoneId = ZoneId.of("UTC")
val UndefinedZonedDateTime: ZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(Long.MIN_VALUE), UtcZone)

fun String.tryParseDateRfc3339(): ZonedDateTime? =
    try {
        ZonedDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
    } catch (e: DateTimeParseException) {
        null
    }

fun ZonedDateTime.toUtc(): LocalDateTime =
    LocalDateTime.ofInstant(this.toInstant(), UtcZone)

fun LocalDateTime.toDateUtc(): Date =
        Date.from(this.toInstant(ZoneOffset.UTC))
