package ru.ifmo.software_engineering.afterlife.classificator.database.unmappers

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinsReport
import ru.ifmo.software_engineering.afterlife.database.tables.records.SinsReportsRecord

@Component
class SinsReportUnmapper : RecordUnmapper<SinsReport, SinsReportsRecord>{
    override fun unmap(model: SinsReport?): SinsReportsRecord {
        model!!

        val record = SinsReportsRecord()
        record.uploadedAt = model.uploadedAt.toOffsetDateTime()
        record.soulId = model.soul.id

        return record
    }
}