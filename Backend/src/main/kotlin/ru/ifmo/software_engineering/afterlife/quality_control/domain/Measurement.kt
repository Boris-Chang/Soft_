package ru.ifmo.software_engineering.afterlife.quality_control.domain

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

data class Measurement(
    @Schema(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val id: Long,
    val title: String,
    val captionForX: String,
    val captionForY: String,
    val threshold: Threshold?
) {
    private constructor() : this (0, "", "", "", null)

    companion object {
        val Empty = Measurement()
    }
}
