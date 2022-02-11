package ru.ifmo.software_engineering.afterlife.classificator.repositories

import kotlinx.coroutines.future.await
import org.jooq.*
import org.jooq.impl.DSL.*
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.software_engineering.afterlife.classificator.database.jooq.domain.fromSouls
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoul
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoulsQueryFilter
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.core.models.PageRequest
import ru.ifmo.software_engineering.afterlife.core.models.PagedResult
import ru.ifmo.software_engineering.afterlife.database.tables.GoodnessReports.GOODNESS_REPORTS
import ru.ifmo.software_engineering.afterlife.database.tables.SinsReports.SINS_REPORTS
import ru.ifmo.software_engineering.afterlife.database.tables.Souls.SOULS
import ru.ifmo.software_engineering.afterlife.utils.jooq.paged

interface SoulRepository {
    suspend fun insertOne(soul: Soul): Soul
    suspend fun getReportedSouls(filter: ReportedSoulsQueryFilter? = null, pageRequest: PageRequest): PagedResult<ReportedSoul>
    suspend fun findById(id: Long): Soul?
}

@Repository
class SoulRepositoryImpl(
    private val dsl: DSLContext,
    private val soulMapper: RecordMapper<Record, Soul>,
    private val sinsReportRepository: SinsReportRepository,
    private val goodnessReportRepository: GoodnessReportRepository,
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
            .select().fromSouls()
            .where(SOULS.ID.eq(id))
            .fetchAsync()
            .await()
            .map { this.soulMapper.map(it) }
            .firstOrNull()
    }

    @Transactional(readOnly = true)
    //TODO: move aggregation to service. Here only souls by filter and page should be aggregated
    override suspend fun getReportedSouls(filter: ReportedSoulsQueryFilter?, pageRequest: PageRequest): PagedResult<ReportedSoul> {
        val totalCount = countSoulsMatchFilter(filter)
        val souls = getSoulsInFilter(filter, pageRequest)

        val goodnessReports = goodnessReportRepository.findBySouls(souls).associateBy { it.soul.id }
        val sinsReports = sinsReportRepository.findBySouls(souls).associateBy { it.soul.id }
        val reportedSouls = souls.map {
            ReportedSoul(soul = it, sinsReports.getOrDefault(it.id, null),
                    goodnessReports.getOrDefault(it.id, null))
        }

        return PagedResult(
                reportedSouls,
                totalCount,
                pageRequest.pageNumber
        )
    }

    private suspend fun countSoulsMatchFilter(filter: ReportedSoulsQueryFilter?): Int  {
        val countField = countDistinct(SOULS.ID)
        return this.dsl.select(countField)
                .fromReportedSouls()
                .whereReportedSoulInFilter(filter)
                .fetchAsync().await()
                .first().map { e -> e.getValue(countField) }
    }

    private suspend fun getSoulsInFilter(filter: ReportedSoulsQueryFilter?, pageRequest: PageRequest): List<Soul> {
        val lastUpdated = greatest(SOULS.DATE_OF_DEATH, GOODNESS_REPORTS.UPLOADED_AT, SINS_REPORTS.UPLOADED_AT)
                .`as`("last_updated")
        return this.dsl.select(asterisk(), lastUpdated)
                .fromReportedSouls()
                .whereReportedSoulInFilter(filter)
                .orderBy(lastUpdated.desc())
                .paged(pageRequest)
                .fetchAsync()
                .await()
                .map { soulMapper.map(it) }
    }

    private fun <T : Record> SelectFromStep<T>.fromReportedSouls(): SelectOnConditionStep<T> {
        return fromSouls()
                .leftJoin(GOODNESS_REPORTS).on(GOODNESS_REPORTS.SOUL_ID.eq(SOULS.ID))
                .leftJoin(SINS_REPORTS).on(SINS_REPORTS.SOUL_ID.eq(SOULS.ID))
    }

    private fun <T : Record> SelectWhereStep<T>.whereReportedSoulInFilter(filter: ReportedSoulsQueryFilter?) =
        when (filter) {
            null -> this.where()
            ReportedSoulsQueryFilter.REPORT_NOT_UPLOADED ->
                this.where(SINS_REPORTS.ID.isNull, GOODNESS_REPORTS.ID.isNull)
            ReportedSoulsQueryFilter.SINS_REPORT_NOT_UPLOADED ->
                this.where(SINS_REPORTS.ID.isNull)
            ReportedSoulsQueryFilter.GOODNESS_REPORT_NOT_UPLOADED ->
                this.where(GOODNESS_REPORTS.ID.isNull)
            ReportedSoulsQueryFilter.ALL_UPLOADED ->
                this.where(GOODNESS_REPORTS.ID.isNotNull, SINS_REPORTS.ID.isNotNull)
        }
}
