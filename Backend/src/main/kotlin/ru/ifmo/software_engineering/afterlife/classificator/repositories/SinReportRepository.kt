package ru.ifmo.software_engineering.afterlife.classificator.repositories

import kotlinx.coroutines.future.await
import org.jooq.*
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinEvidence
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinsReport
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.database.tables.SinEvidences.SIN_EVIDENCES
import ru.ifmo.software_engineering.afterlife.database.tables.SinsReports.SINS_REPORTS
import ru.ifmo.software_engineering.afterlife.database.tables.Souls.SOULS
import ru.ifmo.software_engineering.afterlife.database.tables.records.SinEvidencesRecord
import ru.ifmo.software_engineering.afterlife.database.tables.records.SinsReportsRecord
import ru.ifmo.software_engineering.afterlife.database.tables.records.SoulsRecord

interface SinsReportRepository {
    suspend fun save(report: SinsReport): SinsReport
    suspend fun update(report: SinsReport): SinsReport
    suspend fun findBySoul(soul: Soul): SinsReport?
    suspend fun findById(id: Long): SinsReport?
}

@Repository
class SinsReportRepositoryImpl(
    private val dsl: DSLContext,
    private val unmapper: RecordUnmapper<SinsReport, SinsReportsRecord>,
    private val evidenceUnmapper: RecordUnmapper<SinEvidence, SinEvidencesRecord>,
    private val mapper: RecordMapper<SinsReportsRecord, SinsReport>,
    private val evidenceMapper: RecordMapper<SinEvidencesRecord, SinEvidence>,
    private val soulMapper: RecordMapper<SoulsRecord, Soul>,
) : SinsReportRepository {
    @Transactional
    override suspend fun save(report: SinsReport): SinsReport {
        val savedReport = this.dsl
            .insertInto(SINS_REPORTS)
            .set(this.unmapper.unmap(report))
            .returning()
            .fetchAsync()
            .await()
            .map(this.mapper)
            .map { it.copy(soul = report.soul) }
            .first()

        val savedEvidences = this.saveSinEvidences(report.sins, savedReport)

        return savedReport.copy(sins = savedEvidences)
    }

    @Transactional
    override suspend fun update(report: SinsReport): SinsReport {
        val updatedReport = this.dsl
            .update(SINS_REPORTS)
            .set(this.unmapper.unmap(report))
            .where(SINS_REPORTS.ID.eq(report.id))
            .returning()
            .fetchAsync()
            .await()
            .map(this.mapper)
            .map { it.copy(soul = report.soul) }
            .first()

        this.deleteSinEvidencesForSoul(report.soul)
        val updatedEvidences = this.saveSinEvidences(report.sins, report)

        return updatedReport.copy(sins = updatedEvidences)
    }

    override suspend fun findBySoul(soul: Soul): SinsReport? =
        this.selectFromSinsReport()
            .where(SINS_REPORTS.SOUL_ID.eq(soul.id))
            .fetchAsync()
            .await()
            .mapToSinsReports()
            .firstOrNull()

    override suspend fun findById(id: Long): SinsReport? =
        this.selectFromSinsReport()
            .where(SINS_REPORTS.ID.eq(id))
            .fetchAsync()
            .await()
            .mapToSinsReports()
            .firstOrNull()

    private suspend fun saveSinEvidences(evidences: List<SinEvidence>, report: SinsReport): List<SinEvidence> {
        val fieldsForInsert = SIN_EVIDENCES.fields(SIN_EVIDENCES.SINNED_BY_SOUL_ID, SIN_EVIDENCES.DATE_OF_SIN, SIN_EVIDENCES.ATTONED_AT, SIN_EVIDENCES.KIND)

        val recordsToInsert = evidences.map {
            this.evidenceUnmapper.unmap(it)
                .apply { this.sinnedBySoulId = report.soul.id }
        }.map { it.into(*fieldsForInsert) }

        return this.dsl.insertInto(SIN_EVIDENCES, *fieldsForInsert)
            .valuesOfRecords(recordsToInsert)
            .returning()
            .fetchAsync()
            .await()
            .map(evidenceMapper)
    }

    private suspend fun deleteSinEvidencesForSoul(soul: Soul): Unit =
        this.dsl.delete(SIN_EVIDENCES)
            .where(SIN_EVIDENCES.SINNED_BY_SOUL_ID.eq(soul.id))
            .executeAsync()
            .await()
            .let { }

    private fun selectFromSinsReport(): SelectOnConditionStep<Record> =
        this.dsl.select().from(SINS_REPORTS)
            .join(SOULS)
            .on(SOULS.ID.eq(SINS_REPORTS.SOUL_ID))
            .leftJoin(SIN_EVIDENCES).on(SIN_EVIDENCES.SINNED_BY_SOUL_ID.eq(SINS_REPORTS.SOUL_ID))

    private fun <R : Record> Result<R>.mapToSinsReports(): List<SinsReport> =
        this.intoGroups(
            {
                Pair(
                    mapper.map(it.into(SINS_REPORTS))!!,
                    soulMapper.map(it.into(SOULS))!!
                )
            },
            { evidenceMapper.map(it.into(SIN_EVIDENCES)) }
        )
            .map {
                val (report, soul) = it.key
                val evidences = it.value
                report.copy(soul = soul, sins = evidences)
            }
}
