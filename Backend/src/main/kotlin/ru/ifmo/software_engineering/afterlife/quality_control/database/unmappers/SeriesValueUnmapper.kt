package ru.ifmo.software_engineering.afterlife.quality_control.database.unmappers

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.database.tables.records.SeriesValuesRecord
import ru.ifmo.software_engineering.afterlife.quality_control.domain.SeriesValue

@Component
class SeriesValueUnmapper : RecordUnmapper<SeriesValue, SeriesValuesRecord> {
    override fun unmap(model: SeriesValue?): SeriesValuesRecord {
        val record = SeriesValuesRecord()
        record.value = model!!.value
        record.timestamp = model.timestamp.toOffsetDateTime()

        return record
    }
}
