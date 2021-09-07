package ru.ifmo.software_engineering.afterlife.quality_control.database.mappers

import arrow.core.computations.nullable
import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.database.tables.records.SeriesRecord
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Measurement
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Series

@Component
class SeriesMapper : RecordMapper<SeriesRecord, Series> {
    override fun map(nullableRecord: SeriesRecord?): Series? = nullable.eager {
        val record = nullableRecord.bind()
        record.id.bind()

        Series(
            record.id,
            Measurement.Empty,
            record.name,
            emptyList()
        )
    }
}
