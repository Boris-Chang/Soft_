package ru.ifmo.software_engineering.afterlife.classificator.repositories.impl

import kotlinx.coroutines.future.await
import org.jooq.*
import org.jooq.impl.DSL.*
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.software_engineering.afterlife.classificator.database.mappers.ReportedSoulMapper
import ru.ifmo.software_engineering.afterlife.classificator.database.mappers.SoulMapper
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoul
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoulsQueryFilter
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.classificator.repositories.SoulRepository
import ru.ifmo.software_engineering.afterlife.core.models.PageRequest
import ru.ifmo.software_engineering.afterlife.core.models.PagedResult
import ru.ifmo.software_engineering.afterlife.database.tables.GoodnessEvidences.GOODNESS_EVIDENCES
import ru.ifmo.software_engineering.afterlife.database.tables.GoodnessReports.GOODNESS_REPORTS
import ru.ifmo.software_engineering.afterlife.database.tables.SinsReports.SINS_REPORTS
import ru.ifmo.software_engineering.afterlife.database.tables.Souls.SOULS
import ru.ifmo.software_engineering.afterlife.database.tables.SinEvidences.SIN_EVIDENCES
import ru.ifmo.software_engineering.afterlife.utils.jooq.paged
import java.time.LocalDateTime
import java.time.ZoneId

@Repository
class SoulRepositoryImpl(
    private val dsl: DSLContext,
    private val soulMapper: SoulMapper,
    private val reportedSoulMapper: ReportedSoulMapper
) : SoulRepository {
    override suspend fun insertOne(soul: Soul): Soul {
        return this.dsl.insertInto(SOULS)
            .columns(SOULS.FIRST_NAME, SOULS.LAST_NAME, SOULS.DATE_OF_DEATH)
            .values(
                soul.firstName,
                soul.lastName,
                LocalDateTime.ofInstant(soul.dateOfDeath.toInstant(), ZoneId.of("UTC"))
            )
            .returning()
            .fetchAsync()
            .thenApply { it.map(this.soulMapper).first() }
            .await()
    }

    @Transactional(readOnly = true)
    override suspend fun getReportedSouls(filter: ReportedSoulsQueryFilter?, pageRequest: PageRequest): PagedResult<ReportedSoul> {
        val countField = countDistinct(SOULS.ID)
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
            .intoGroups{
                Triple(
                    it.into(SOULS),
                    if (it[SINS_REPORTS.ID] != null)
                        it.into(SINS_REPORTS)
                    else null,
                    if (it[GOODNESS_REPORTS.ID] != null)
                        it.into(GOODNESS_REPORTS)
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

    private fun<T : Record> SelectFromStep<T>.fromReportedSouls() =
        this.from(SOULS)
            .leftJoin(SINS_REPORTS).on(SINS_REPORTS.SOUL_ID.eq(SOULS.ID))
            .leftJoin(GOODNESS_REPORTS).on(GOODNESS_REPORTS.SOUL_ID.eq(SOULS.ID))
            .leftJoin(SIN_EVIDENCES).on(SIN_EVIDENCES.SINNED_BY_SOUL_ID.eq(SOULS.ID))
            .leftJoin(GOODNESS_EVIDENCES).on(GOODNESS_EVIDENCES.DONE_BY_SOUL_ID.eq(SOULS.ID))

    private fun<T : Record> SelectWhereStep<T>.whereReportedSoulInFilter(filter: ReportedSoulsQueryFilter?) =
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
