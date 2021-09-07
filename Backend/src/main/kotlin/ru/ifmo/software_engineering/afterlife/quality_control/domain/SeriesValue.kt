package ru.ifmo.software_engineering.afterlife.quality_control.domain

import java.time.ZonedDateTime

data class SeriesValue(
    val id: Long,
    val timestamp: ZonedDateTime,
    val value: Double,
)
