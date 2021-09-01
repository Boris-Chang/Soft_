package ru.ifmo.software_engineering.afterlife.classificator.database.mappers

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinEvidence
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinKind
import ru.ifmo.software_engineering.afterlife.database.tables.records.SinEvidencesRecord

@Component
class SinEvidenceMapper : RecordMapper<SinEvidencesRecord, SinEvidence> {
    override fun map(record: SinEvidencesRecord?): SinEvidence? {
        if (record == null || record.id == null) {
            return null
        }

        return SinEvidence(
            record.id,
            SinKind.valueOf(record.kind.literal),
            record.dateOfSin.toZonedDateTime(),
            record.attonedAt?.toZonedDateTime(),
        )
    }
}
