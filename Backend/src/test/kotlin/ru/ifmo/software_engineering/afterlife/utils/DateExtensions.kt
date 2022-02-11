package ru.ifmo.software_engineering.afterlife.utils

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

fun ZonedDateTime.truncatedToSeconds(): ZonedDateTime =
    this.truncatedTo(ChronoUnit.SECONDS)

fun ZonedDateTime.inCurrentZone(): ZonedDateTime =
        this.withZoneSameInstant(ZoneId.systemDefault())
