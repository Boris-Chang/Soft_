package ru.ifmo.software_engineering.afterlife.quality_control.domain

data class ThresholdAlert (
        val id: Int,
        val measurementId: Long,
        val text: String
)