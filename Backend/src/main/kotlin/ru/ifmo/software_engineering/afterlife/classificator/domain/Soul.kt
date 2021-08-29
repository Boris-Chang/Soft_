package ru.ifmo.software_engineering.afterlife.classificator.domain

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.time.ZonedDateTime

data class Soul(
    @Schema(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val id: Long,
    val firstName: String,
    val lastName: String,
    val dateOfDeath: ZonedDateTime,
)
