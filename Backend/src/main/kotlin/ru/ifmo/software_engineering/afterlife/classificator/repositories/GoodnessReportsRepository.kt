package ru.ifmo.software_engineering.afterlife.classificator.repositories

import kotlinx.coroutines.future.await
import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.jooq.RecordUnmapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessEvidence
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessReport
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.database.tables.GoodnessEvidences.GOODNESS_EVIDENCES
import ru.ifmo.software_engineering.afterlife.database.tables.GoodnessReports.GOODNESS_REPORTS
import ru.ifmo.software_engineering.afterlife.database.tables.Souls.SOULS
import ru.ifmo.software_engineering.afterlife.database.tables.records.GoodnessEvidencesRecord
import ru.ifmo.software_engineering.afterlife.database.tables.records.GoodnessReportsRecord
import ru.ifmo.software_engineering.afterlife.database.tables.records.SoulsRecord
import java.util.stream.Collectors

interface GoodnessReportRepository {
    suspend fun save(report: GoodnessReport): GoodnessReport
    suspend fun findBySoul(soul: Soul): GoodnessReport?
    @Transactional
    suspend fun update(report: GoodnessReport): GoodnessReport
}

@Repository
class GoodnessReportRepositoryImpl(
    private val dsl: DSLContext,
    private val unmapper: RecordUnmapper<GoodnessReport, GoodnessReportsRecord>,
    private val goodnessEvidenceUnmapper: RecordUnmapper<GoodnessEvidence, GoodnessEvidencesRecord>,
    private val mapper: RecordMapper<GoodnessReportsRecord, GoodnessReport>,
    private val goodnessEvidenceMapper: RecordMapper<GoodnessEvidencesRecord, GoodnessEvidence>,
    private val soulMapper: RecordMapper<SoulsRecord, Soul>,
) : GoodnessReportRepository {
    @Transactional
    override suspend fun save(report: GoodnessReport): GoodnessReport {
        val savedReport = this.dsl
            .insertInto(GOODNESS_REPORTS)
            .set(this.unmapper.unmap(report))
            .returning()
            .fetchAsync()
            .await()
            .map(this.mapper)
            .map { it.copy(soul = report.soul) }
            .first()

        val savedEvidences = this.saveGoodnessEvidences(report.goodnessEvidences, savedReport)

        return savedReport.copy(goodnessEvidences = savedEvidences)
    }

    @Transactional
    override suspend fun update(report: GoodnessReport): GoodnessReport {
        val updatedReport = this.dsl.update(GOODNESS_REPORTS)
            .set(this.unmapper.unmap(report))
            .where(GOODNESS_REPORTS.ID.eq(report.id))
            .returning()
            .fetchAsync()
            .await()
            .map(this.mapper)
            .map { it.copy(soul = report.soul) }
            .first()

        this.deleteGoodnessEvidences(report)
        val updatedEvidences = this.saveGoodnessEvidences(report.goodnessEvidences, updatedReport)

        return updatedReport.copy(goodnessEvidences = updatedEvidences)
    }

    override suspend fun findBySoul(soul: Soul): GoodnessReport? =
        this.dsl.select().from(GOODNESS_REPORTS)
            .leftJoin(GOODNESS_EVIDENCES)
            .on(GOODNESS_EVIDENCES.DONE_BY_SOUL_ID.eq(GOODNESS_REPORTS.SOUL_ID))
            .join(SOULS)
            .on(SOULS.ID.eq(GOODNESS_REPORTS.SOUL_ID))
            .where(GOODNESS_REPORTS.SOUL_ID.eq(soul.id))
            .fetchAsync()
            .await()
            .intoGroups(
                {
                    Pair(
                        this.soulMapper.map(it.into(SOULS)),
                        this.mapper.map(it.into(GOODNESS_REPORTS))
                    )
                },
                {
                    this.goodnessEvidenceMapper.map(it.into(GOODNESS_EVIDENCES))
                }
            )
            .map {
                val (dbSoul, report) = it.key
                report!!.copy(soul = dbSoul!!, goodnessEvidences = it.value)
            }
            .firstOrNull()

    private suspend fun saveGoodnessEvidences(evidences: List<GoodnessEvidence>, report: GoodnessReport): List<GoodnessEvidence> {
        val fieldsForInsert = GOODNESS_EVIDENCES.fieldStream()
            .filter { it != GOODNESS_EVIDENCES.ID }
            .collect(Collectors.toList()).toTypedArray()

        val recordsToInsert = evidences.map {
            this.goodnessEvidenceUnmapper.unmap(it)
                .apply { this.doneBySoulId = report.soul.id }
        }.map { it.into(*fieldsForInsert) }

        return this.dsl.insertInto(GOODNESS_EVIDENCES, *fieldsForInsert)
            .valuesOfRecords(recordsToInsert)
            .returning()
            .fetchAsync()
            .await()
            .map(goodnessEvidenceMapper)
    }

    private suspend fun deleteGoodnessEvidences(report: GoodnessReport): Unit =
        this.dsl.delete(GOODNESS_EVIDENCES)
            .where(GOODNESS_EVIDENCES.DONE_BY_SOUL_ID.eq(report.soul.id))
            .executeAsync()
            .await()
            .let { }
}
