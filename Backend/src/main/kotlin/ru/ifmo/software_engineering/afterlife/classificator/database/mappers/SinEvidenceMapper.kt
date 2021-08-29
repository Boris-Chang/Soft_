package ru.ifmo.software_engineering.afterlife.classificator.database.mappers

import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinEvidence
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinKind
import ru.ifmo.software_engineering.afterlife.database.tables.SinEvidences.SIN_EVIDENCES
import ru.ifmo.software_engineering.afterlife.utils.toDateUtc

@Component
class SinEvidenceMapper : RecordMapper<Record, SinEvidence> {
    override fun map(record: Record?): SinEvidence? {
        if (record == null || record[SIN_EVIDENCES.ID] == null) {
            return null
        }

        val sinRecord = record.into(SIN_EVIDENCES)

        return SinEvidence(
            sinRecord.id,
            SinKind.valueOf(sinRecord.kind.literal),
            sinRecord.dateOfSin.toDateUtc(),
            sinRecord.attonedAt?.toDateUtc()
        )
    }
}