package ru.ifmo.software_engineering.afterlife.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

private val rfc3339Format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

fun LocalDateTime.toDateUtc(): Date =
    Date.from(this.toInstant(ZoneOffset.UTC))

fun String.tryParseDateRfc3339(): Date? =
    try {
        rfc3339Format.parse(this)
    } catch (e: ParseException)  {
        null
    }
