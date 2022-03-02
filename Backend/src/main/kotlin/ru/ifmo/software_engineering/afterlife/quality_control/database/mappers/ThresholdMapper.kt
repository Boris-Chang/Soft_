package ru.ifmo.software_engineering.afterlife.quality_control.database.mappers

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.database.tables.records.ThresholdsRecord
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Threshold

@Component
class ThresholdMapper : RecordMapper<ThresholdsRecord, Threshold> {
    override fun map(p0: ThresholdsRecord?): Threshold? {
        return p0?.measurementId?.let {
            Threshold(p0.value)
        }
    }
}