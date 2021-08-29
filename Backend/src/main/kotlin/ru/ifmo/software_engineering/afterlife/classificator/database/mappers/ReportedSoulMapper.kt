package ru.ifmo.software_engineering.afterlife.classificator.database.mappers

import org.jooq.Record
import org.jooq.Result
import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.*
import ru.ifmo.software_engineering.afterlife.database.tables.records.*
import ru.ifmo.software_engineering.afterlife.users.domain.User
import java.time.ZoneId

@Component
class ReportedSoulMapper(
    private val soulMapper: RecordMapper<SoulsRecord, Soul>,
    private val sinEvidenceMapper: RecordMapper<Record, SinEvidence>,
    private val goodnessEvidenceMapper: RecordMapper<Record, GoodnessEvidence>
) {
    fun map(
        records: Map.Entry<
                           Triple<SoulsRecord, SinsReportsRecord?, GoodnessReportsRecord?>,
                           Result<Record>>
    ): ReportedSoul {
        val soul = this.soulMapper.map(records.key.first)!!

        val sinEvidences = records.value
            .map{ this.sinEvidenceMapper.map(it) }
            .filterNotNull()
        val goodnessEvidences = records.value
            .map { this.goodnessEvidenceMapper.map(it) }
            .filterNotNull()

        val sinsReportRecord = records.key.second
        val goodnessReportRecord = records.key.third

        return ReportedSoul(
            soul,
            sinsReportRecord?.toModel(soul, sinEvidences),
            goodnessReportRecord?.toModel(soul, goodnessEvidences),
        )
    }

    private fun SinsReportsRecord.toModel(soul: Soul, sinEvidences: List<SinEvidence>): SinsReport {
        return SinsReport(
            this.id,
            soul,
            sinEvidences,
            User(1, "Admin"),
            this.uploadedAt.atZone(ZoneId.of("UTC")),
        )
    }

    private fun GoodnessReportsRecord.toModel(soul: Soul, goodnessEvidences: List<GoodnessEvidence>): GoodnessReport {
        return GoodnessReport(
            this.id,
            soul,
            goodnessEvidences,
            User(1, "Admin"),
            this.uploadedAt.atZone(ZoneId.of("UTC")),
        )

    }
}
