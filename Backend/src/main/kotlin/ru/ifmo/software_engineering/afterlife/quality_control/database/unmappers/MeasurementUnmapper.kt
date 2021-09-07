package ru.ifmo.software_engineering.afterlife.quality_control.database.unmappers

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.database.tables.records.MeasurementsRecord
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Measurement

@Component
class MeasurementUnmapper : RecordUnmapper<Measurement, MeasurementsRecord> {
    override fun unmap(model: Measurement?): MeasurementsRecord {
        val record = MeasurementsRecord()
        record.title = model!!.title
        record.xCaption = model.captionForX
        record.yCaption = model.captionForY

        return record
    }
}
