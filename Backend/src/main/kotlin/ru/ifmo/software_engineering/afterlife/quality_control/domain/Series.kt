package ru.ifmo.software_engineering.afterlife.quality_control.domain

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

data class Series(
    @Schema(readOnly = true, accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val id: Long,

    @Schema(readOnly = true, accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val measurement: Measurement,

    val name: String,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val values: List<SeriesValue>,
) {
    fun withUpdatedName(name: String) = copy(name = name)
}
