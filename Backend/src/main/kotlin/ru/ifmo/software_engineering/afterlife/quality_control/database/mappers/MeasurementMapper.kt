package ru.ifmo.software_engineering.afterlife.quality_control.database.mappers

import arrow.core.computations.nullable
import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.database.tables.records.MeasurementsRecord
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Measurement

@Component
class MeasurementMapper : RecordMapper<MeasurementsRecord, Measurement> {
    override fun map(nullableRecord: MeasurementsRecord?): Measurement? = nullable.eager {
        val record = nullableRecord.bind()
        record.id.bind()

        Measurement(
            record.id,
            record.title,
            record.xCaption,
            record.yCaption
        )
    }
}
