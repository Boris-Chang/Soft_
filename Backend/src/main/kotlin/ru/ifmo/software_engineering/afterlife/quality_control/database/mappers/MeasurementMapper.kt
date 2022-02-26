package ru.ifmo.software_engineering.afterlife.quality_control.database.mappers

import arrow.core.computations.nullable
import org.jooq.RecordMapper
import org.jooq.Record
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.database.Tables.MEASUREMENTS
import ru.ifmo.software_engineering.afterlife.database.Tables.THRESHOLDS
import ru.ifmo.software_engineering.afterlife.database.tables.records.ThresholdsRecord
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Measurement
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Threshold

@Component
class MeasurementMapper(
        private val thresholdMapper: RecordMapper<ThresholdsRecord, Threshold>
) : RecordMapper<Record, Measurement> {
    override fun map(nullableRecord: Record?): Measurement? = nullable.eager {
        val record = nullableRecord.bind()
        val measurementRecord = record.into(MEASUREMENTS)
        measurementRecord.id.bind()

        Measurement(
            measurementRecord.id,
            measurementRecord.title,
            measurementRecord.xCaption,
            measurementRecord.yCaption,
            thresholdMapper.map(record.into(THRESHOLDS))
        )
    }
}
