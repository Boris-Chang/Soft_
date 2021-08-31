package ru.ifmo.software_engineering.afterlife.classificator.database.unmappers

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessReport
import ru.ifmo.software_engineering.afterlife.database.tables.records.GoodnessReportsRecord

@Component
class GoodnessReportUnmapper : RecordUnmapper<GoodnessReport, GoodnessReportsRecord> {
    override fun unmap(report: GoodnessReport?): GoodnessReportsRecord {
        report!!

        val record = GoodnessReportsRecord()
        record.uploadedAt = report.uploadedAt.toOffsetDateTime()
        record.soulId = report.soul.id

        return record
    }
}