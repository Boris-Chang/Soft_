package ru.ifmo.software_engineering.afterlife.quality_control.database.unmappers

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.database.tables.records.SeriesRecord
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Series

@Component
class SeriesUnmapper : RecordUnmapper<Series, SeriesRecord> {
    override fun unmap(model: Series?): SeriesRecord {
        val record = SeriesRecord()
        record.name = model!!.name
        record.measurementId = model.measurement.id

        return record
    }
}