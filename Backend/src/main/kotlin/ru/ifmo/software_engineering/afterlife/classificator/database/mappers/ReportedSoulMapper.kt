package ru.ifmo.software_engineering.afterlife.classificator.database.mappers

import org.jooq.Record
import org.jooq.RecordMapper
import org.jooq.Result
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.*
import ru.ifmo.software_engineering.afterlife.database.tables.GoodnessEvidences.GOODNESS_EVIDENCES
import ru.ifmo.software_engineering.afterlife.database.tables.SinEvidences.SIN_EVIDENCES
import ru.ifmo.software_engineering.afterlife.database.tables.records.*

@Component
class ReportedSoulMapper(
    private val soulMapper: RecordMapper<SoulsRecord, Soul>,
    private val sinEvidenceMapper: RecordMapper<SinEvidencesRecord, SinEvidence>,
    private val goodnessEvidenceMapper: RecordMapper<GoodnessEvidencesRecord, GoodnessEvidence>,
    private val sinsReportMapper: RecordMapper<SinsReportsRecord, SinsReport>,
    private val goodnessReportMapper: RecordMapper<GoodnessReportsRecord, GoodnessReport>
) {
    fun map(
        records: Map.Entry<
            Triple<SoulsRecord, SinsReportsRecord?, GoodnessReportsRecord?>,
            Result<Record>>
    ): ReportedSoul {
        val (soulRecord, sinsReportRecord, goodnessReportRecord) = records.key

        val soul = this.soulMapper.map(soulRecord)!!

        val sinEvidences = records.value
            .map { this.sinEvidenceMapper.map(it.into(SIN_EVIDENCES)) }
            .filterNotNull()
        val goodnessEvidences = records.value
            .map { this.goodnessEvidenceMapper.map(it.into(GOODNESS_EVIDENCES)) }
            .filterNotNull()

        val sinsReport = this.sinsReportMapper
            .map(sinsReportRecord)
            ?.copy(soul = soul, sins = sinEvidences)
        val goodnessReport = this.goodnessReportMapper
            .map(goodnessReportRecord)
            ?.copy(soul = soul, goodnessEvidences = goodnessEvidences)

        return ReportedSoul(soul, sinsReport, goodnessReport)
    }
}
