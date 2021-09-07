package ru.ifmo.software_engineering.afterlife.api.models

import ru.ifmo.software_engineering.afterlife.quality_control.domain.Measurement
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Series

data class SeriesRequestData(
    val name: String
)

fun SeriesRequestData.asModel(id: Long): Series =
    Series(id, Measurement.Empty, this.name, emptyList())