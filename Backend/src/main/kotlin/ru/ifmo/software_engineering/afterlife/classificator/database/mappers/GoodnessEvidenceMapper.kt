package ru.ifmo.software_engineering.afterlife.classificator.database.mappers

import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessEvidence
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessKind
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.database.tables.GoodnessEvidences.GOODNESS_EVIDENCES
import ru.ifmo.software_engineering.afterlife.database.tables.Souls
import ru.ifmo.software_engineering.afterlife.database.tables.records.SoulsRecord
import ru.ifmo.software_engineering.afterlife.utils.toDateUtc

@Component
class GoodnessEvidenceMapper(
    private val soulMapper: RecordMapper<SoulsRecord, Soul>
) : RecordMapper<Record, GoodnessEvidence> {
    override fun map(record: Record?): GoodnessEvidence? {
        if (record == null || record[GOODNESS_EVIDENCES.ID] == null) {
            return null
        }

        val goodnessRecord = record.into(GOODNESS_EVIDENCES)
        val soul = this.soulMapper.map(record.into(Souls.SOULS))

        return GoodnessEvidence(
            goodnessRecord.id,
            GoodnessKind.valueOf(goodnessRecord.kind.literal),
            goodnessRecord.dateOfGoodDeedEvidence.toDateUtc(),
            soul!!,
        )
    }
}