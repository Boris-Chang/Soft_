package ru.ifmo.software_engineering.afterlife.classificator.database.mappers

import arrow.core.computations.nullable
import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportComment
import ru.ifmo.software_engineering.afterlife.database.tables.records.SoulReportCommentRecord

@Component
class ReportCommentMapper : RecordMapper<SoulReportCommentRecord, ReportComment> {
    override fun map(nullableRecord: SoulReportCommentRecord?): ReportComment? = nullable.eager {
        val record = nullableRecord.bind()
        record.id.bind()

        ReportComment(
            record.id,
            record.commentText,
            record.createdAt.toZonedDateTime(),
        )
    }
}
