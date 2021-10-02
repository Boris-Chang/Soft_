package ru.ifmo.software_engineering.afterlife.classificator.domain

import java.time.ZonedDateTime

data class ReportComment(
    val id: Long,
    val text: String,
    val createdAt: ZonedDateTime,
)
