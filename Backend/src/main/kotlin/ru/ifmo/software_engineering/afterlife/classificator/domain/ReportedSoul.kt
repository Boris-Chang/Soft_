package ru.ifmo.software_engineering.afterlife.classificator.domain

data class ReportedSoul(
    val soul: Soul,
    val sinsReport: SinsReport?,
    val goodnessReport: GoodnessReport?
)
