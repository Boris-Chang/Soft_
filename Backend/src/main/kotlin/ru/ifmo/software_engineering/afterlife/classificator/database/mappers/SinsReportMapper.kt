package ru.ifmo.software_engineering.afterlife.classificator.database.mappers

import arrow.core.computations.nullable
import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinsReport
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.database.tables.records.SinsReportsRecord
import ru.ifmo.software_engineering.afterlife.users.domain.User

@Component
class SinsReportMapper : RecordMapper<SinsReportsRecord, SinsReport> {
    override fun map(recordNullable: SinsReportsRecord?): SinsReport? = nullable.eager{
        val record = recordNullable.bind()
        SinsReport(
            record.id,
            Soul.empty,
            emptyList(),
            User.empty,
            record.uploadedAt.toZonedDateTime(),
        )
    }
}