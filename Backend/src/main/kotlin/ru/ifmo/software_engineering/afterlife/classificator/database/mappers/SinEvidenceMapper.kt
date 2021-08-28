package ru.ifmo.software_engineering.afterlife.classificator.database.mappers

import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinEvidence
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinKind
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.database.tables.SinEvidences.SIN_EVIDENCES
import ru.ifmo.software_engineering.afterlife.database.tables.Souls.SOULS
import ru.ifmo.software_engineering.afterlife.database.tables.records.SoulsRecord
import ru.ifmo.software_engineering.afterlife.utils.toDateUtc

@Component
class SinEvidenceMapper(
    private val soulMapper: RecordMapper<SoulsRecord, Soul>
) : RecordMapper<Record, SinEvidence> {
    override fun map(record: Record?): SinEvidence? {
        if (record == null || record[SIN_EVIDENCES.ID] == null) {
            return null
        }

        val sinRecord = record.into(SIN_EVIDENCES)
        val soul = this.soulMapper.map(record.into(SOULS))

        return SinEvidence(
            sinRecord.id,
            SinKind.valueOf(sinRecord.kind.literal),
            sinRecord.dateOfSin.toDateUtc(),
            soul!!,
            sinRecord.attonedAt?.toDateUtc()
        )
    }
}