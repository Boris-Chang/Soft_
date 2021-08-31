package ru.ifmo.software_engineering.afterlife.classificator.database.mappers

import arrow.core.computations.nullable
import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessReport
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.database.tables.records.GoodnessReportsRecord
import ru.ifmo.software_engineering.afterlife.users.domain.User

@Component
class GoodnessReportMapper : RecordMapper<GoodnessReportsRecord, GoodnessReport> {
    override fun map(recordNullable: GoodnessReportsRecord?): GoodnessReport? = nullable.eager {
        val record = recordNullable.bind()
        GoodnessReport(
            record.id,
            Soul.empty,
            emptyList(),
            User.empty,
            record.uploadedAt.toZonedDateTime())
    }
}