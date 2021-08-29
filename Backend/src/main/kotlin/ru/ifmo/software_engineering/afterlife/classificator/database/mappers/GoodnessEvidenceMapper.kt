package ru.ifmo.software_engineering.afterlife.classificator.database.mappers

import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessEvidence
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessKind
import ru.ifmo.software_engineering.afterlife.database.tables.GoodnessEvidences.GOODNESS_EVIDENCES
import ru.ifmo.software_engineering.afterlife.utils.toDateUtc

@Component
class GoodnessEvidenceMapper : RecordMapper<Record, GoodnessEvidence> {
    override fun map(record: Record?): GoodnessEvidence? {
        if (record == null || record[GOODNESS_EVIDENCES.ID] == null) {
            return null
        }

        val goodnessRecord = record.into(GOODNESS_EVIDENCES)

        return GoodnessEvidence(
            goodnessRecord.id,
            GoodnessKind.valueOf(goodnessRecord.kind.literal),
            goodnessRecord.dateOfGoodDeedEvidence.toDateUtc(),
        )
    }
}