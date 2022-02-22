package ru.ifmo.software_engineering.afterlife.classificator.repositories

import kotlinx.coroutines.future.await
import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.jooq.RecordUnmapper
import org.springframework.stereotype.Repository
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportComment
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.database.tables.SoulReportComment.SOUL_REPORT_COMMENT
import ru.ifmo.software_engineering.afterlife.database.tables.records.SoulReportCommentRecord

interface ReportCommentRepository {
    suspend fun findCommentsBySoul(soul: Soul): List<ReportComment>
    suspend fun save(soul: Soul, comment: ReportComment): ReportComment
}

@Repository
class ReportCommentRepositoryImpl(
    private val dsl: DSLContext,
    private val recordMapper: RecordMapper<SoulReportCommentRecord, ReportComment>,
    private val recordUnmapper: RecordUnmapper<ReportComment, SoulReportCommentRecord>
) : ReportCommentRepository {
    override suspend fun findCommentsBySoul(soul: Soul): List<ReportComment> =
        this.dsl.selectFrom(SOUL_REPORT_COMMENT)
            .where(SOUL_REPORT_COMMENT.SOUL_ID.eq(soul.id))
            .orderBy(SOUL_REPORT_COMMENT.CREATED_AT.desc())
            .fetchAsync()
            .await()
            .map(this.recordMapper)

    override suspend fun save(soul: Soul, comment: ReportComment): ReportComment =
        this.dsl.insertInto(SOUL_REPORT_COMMENT)
            .set(this.recordUnmapper.unmap(comment))
            .set(SOUL_REPORT_COMMENT.SOUL_ID, soul.id)
            .returning()
            .fetchAsync()
            .await()
            .map(this.recordMapper)
            .first()
}
