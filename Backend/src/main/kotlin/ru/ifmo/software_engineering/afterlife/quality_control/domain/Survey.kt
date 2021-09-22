package ru.ifmo.software_engineering.afterlife.quality_control.domain

import org.hibernate.validator.constraints.URL

data class Survey(
    val id: Long,
    val title: String,
    @get:URL(message = "Should be a valid URL")
    val url: String,
    val addressee: SurveyAddressee
)
