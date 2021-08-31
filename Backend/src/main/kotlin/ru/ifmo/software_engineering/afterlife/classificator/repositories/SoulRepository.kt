package ru.ifmo.software_engineering.afterlife.classificator.repositories

import kotlinx.coroutines.future.await
import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.software_engineering.afterlife.classificator.database.mappers.ReportedSoulMapper
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoul
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoulsQueryFilter
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.core.models.PageRequest
import ru.ifmo.software_engineering.afterlife.core.models.PagedResult
import ru.ifmo.software_engineering.afterlife.database.tables.*
import ru.ifmo.software_engineering.afterlife.database.tables.Souls.SOULS
import ru.ifmo.software_engineering.afterlife.database.tables.records.SoulsRecord
import ru.ifmo.software_engineering.afterlife.utils.jooq.paged

interface SoulRepository {
    suspend fun insertOne(soul: Soul): Soul
    suspend fun getReportedSouls(filter: ReportedSoulsQueryFilter? = null, pageRequest: PageRequest): PagedResult<ReportedSoul>
    suspend fun findById(id: Long): Soul?
}

@Repository
class SoulRepositoryImpl(
    private val dsl: DSLContext,
    private val soulMapper: RecordMapper<SoulsRecord, Soul>,
    private val reportedSoulMapper: ReportedSoulMapper
) : SoulRepository {
    override suspend fun insertOne(soul: Soul): Soul {
        return this.dsl.insertInto(SOULS)
            .columns(SOULS.FIRST_NAME, SOULS.LAST_NAME, SOULS.DATE_OF_DEATH)
            .values(
                soul.firstName,
                soul.lastName,
                soul.dateOfDeath.toOffsetDateTime()
            )
            .returning()
            .fetchAsync()
            .thenApply { it.map(this.soulMapper).first() }
            .await()
    }

    override suspend fun findById(id: Long): Soul? {
        return this.dsl
            .select().from(SOULS)
            .where(SOULS.ID.eq(id))
            .fetchAsync()
            .await()
            .map{ this.soulMapper.map(it.into(SOULS)) }
            .firstOrNull()
    }

    @Transactional(readOnly = true)
    override suspend fun getReportedSouls(filter: ReportedSoulsQueryFilter?, pageRequest: PageRequest): PagedResult<ReportedSoul> {
        val countField = DSL.countDistinct(SOULS.ID)
        val totalCount = this.dsl.select(countField)
            .fromReportedSouls()
            .whereReportedSoulInFilter(filter)
            .fetchAsync().await()
            .first().map { e -> e.getValue(countField) }

        val results = this.dsl.select()
            .fromReportedSouls()
            .whereReportedSoulInFilter(filter)
            .paged(pageRequest)
            .fetchAsync().await()

        val reportedSouls = results
            .intoGroups {
                Triple(
                    it.into(SOULS),
                    if (it[SinsReports.SINS_REPORTS.ID] != null)
                        it.into(SinsReports.SINS_REPORTS)
                    else null,
                    if (it[GoodnessReports.GOODNESS_REPORTS.ID] != null)
                        it.into(GoodnessReports.GOODNESS_REPORTS)
                    else null
                )
            }
            .map { reportedSoulMapper.map(it) }

        return PagedResult(
            reportedSouls,
            totalCount,
            pageRequest.pageNumber
        )
    }

    private fun <T : Record> SelectFromStep<T>.fromReportedSouls() =
        this.from(SOULS)
            .leftJoin(SinsReports.SINS_REPORTS).on(SinsReports.SINS_REPORTS.SOUL_ID.eq(SOULS.ID))
            .leftJoin(GoodnessReports.GOODNESS_REPORTS).on(GoodnessReports.GOODNESS_REPORTS.SOUL_ID.eq(SOULS.ID))
            .leftJoin(SinEvidences.SIN_EVIDENCES).on(SinEvidences.SIN_EVIDENCES.SINNED_BY_SOUL_ID.eq(SOULS.ID))
            .leftJoin(GoodnessEvidences.GOODNESS_EVIDENCES).on(GoodnessEvidences.GOODNESS_EVIDENCES.DONE_BY_SOUL_ID.eq(SOULS.ID))

    private fun <T : Record> SelectWhereStep<T>.whereReportedSoulInFilter(filter: ReportedSoulsQueryFilter?) =
        when (filter) {
            null -> this.where()
            ReportedSoulsQueryFilter.REPORT_NOT_UPLOADED ->
                this.where(SinsReports.SINS_REPORTS.ID.isNull, GoodnessReports.GOODNESS_REPORTS.ID.isNull)
            ReportedSoulsQueryFilter.SINS_REPORT_NOT_UPLOADED ->
                this.where(SinsReports.SINS_REPORTS.ID.isNull)
            ReportedSoulsQueryFilter.GOODNESS_REPORT_NOT_UPLOADED ->
                this.where(GoodnessReports.GOODNESS_REPORTS.ID.isNull)
            ReportedSoulsQueryFilter.ALL_UPLOADED ->
                this.where(GoodnessReports.GOODNESS_REPORTS.ID.isNotNull, SinsReports.SINS_REPORTS.ID.isNotNull)
        }
}
