package ru.ifmo.software_engineering.afterlife.utils

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

fun LocalDateTime.toDateUtc(): Date =
    Date.from(this.toInstant(ZoneOffset.UTC))