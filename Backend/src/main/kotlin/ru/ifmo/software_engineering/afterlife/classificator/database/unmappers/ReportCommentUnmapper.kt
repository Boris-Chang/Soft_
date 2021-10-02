package ru.ifmo.software_engineering.afterlife.classificator.database.unmappers

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportComment
import ru.ifmo.software_engineering.afterlife.database.tables.records.SoulReportCommentRecord

@Component
class ReportCommentUnmapper : RecordUnmapper<ReportComment, SoulReportCommentRecord> {
    override fun unmap(comment: ReportComment?): SoulReportCommentRecord {
        val record = SoulReportCommentRecord()
        record.commentText = comment!!.text
        record.createdAt = comment.createdAt.toOffsetDateTime()

        return record
    }
}
