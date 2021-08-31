package ru.ifmo.software_engineering.afterlife.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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
