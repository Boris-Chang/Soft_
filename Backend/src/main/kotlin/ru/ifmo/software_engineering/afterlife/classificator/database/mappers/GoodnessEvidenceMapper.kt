package ru.ifmo.software_engineering.afterlife.classificator.database.mappers

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessEvidence
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessKind
import ru.ifmo.software_engineering.afterlife.database.tables.GoodnessEvidences.GOODNESS_EVIDENCES
import ru.ifmo.software_engineering.afterlife.database.tables.records.GoodnessEvidencesRecord

@Component
class GoodnessEvidenceMapper : RecordMapper<GoodnessEvidencesRecord, GoodnessEvidence> {
    override fun map(record: GoodnessEvidencesRecord?): GoodnessEvidence? {
        if (record == null || record[GOODNESS_EVIDENCES.ID] == null) {
            return null
        }
        return GoodnessEvidence(
            record.id,
            GoodnessKind.valueOf(record.kind.literal),
            record.dateOfGoodDeedEvidence.toZonedDateTime()
        )
    }
}
