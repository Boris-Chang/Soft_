package ru.ifmo.software_engineering.afterlife.quality_control.database.mappers

import arrow.core.computations.nullable
import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.database.tables.records.SeriesValuesRecord
import ru.ifmo.software_engineering.afterlife.quality_control.domain.SeriesValue

@Component
class SeriesValueMapper : RecordMapper<SeriesValuesRecord, SeriesValue> {
    override fun map(nullableRecord: SeriesValuesRecord?): SeriesValue? = nullable.eager {
        val record = nullableRecord.bind()
        record.id.bind()

        SeriesValue(
            record.id,
            record.timestamp.toZonedDateTime(),
            record.value,
        )
    }
}
