package ru.ifmo.software_engineering.afterlife.classificator.database.unmappers

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessEvidence
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessKind
import ru.ifmo.software_engineering.afterlife.database.tables.records.GoodnessEvidencesRecord
typealias GoodnessKindDb = ru.ifmo.software_engineering.afterlife.database.enums.GoodnessKind

@Component
class GoodnessEvidenceUnmapper : RecordUnmapper<GoodnessEvidence, GoodnessEvidencesRecord> {
    override fun unmap(model: GoodnessEvidence?): GoodnessEvidencesRecord {
        model!!

        val record = GoodnessEvidencesRecord()
        record.dateOfGoodDeedEvidence = model.dateOfGoodDeedEvidence.toOffsetDateTime()
        record.kind = this.unmapKind(model.kind)

        return record
    }

    private fun unmapKind(goodnessKind: GoodnessKind): GoodnessKindDb {
        return when (goodnessKind) {
            GoodnessKind.BREAKING_VOW_BY_ELSE -> GoodnessKindDb.BREAKING_VOW_BY_ELSE
            GoodnessKind.REFORMISM -> GoodnessKindDb.REFORMISM
            GoodnessKind.AMBITION -> GoodnessKindDb.AMBITION
            GoodnessKind.LOVE -> GoodnessKindDb.LOVE
            GoodnessKind.WISDOM -> GoodnessKindDb.WISDOM
            GoodnessKind.SCHOLARSHIP -> GoodnessKindDb.SCHOLARSHIP
            GoodnessKind.WAR_FOR_FAITH -> GoodnessKindDb.WAR_FOR_FAITH
            GoodnessKind.FAIR_GOVERNMENT -> GoodnessKindDb.FAIR_GOVERNMENT
            GoodnessKind.THEOLOGY -> GoodnessKindDb.THEOLOGY
            GoodnessKind.MONASTICISM -> GoodnessKindDb.MONASTICISM
            GoodnessKind.TRIUMPH -> GoodnessKindDb.TRIUMPH
            GoodnessKind.HOLINESS -> GoodnessKindDb.HOLINESS
            GoodnessKind.DIVINITY -> GoodnessKindDb.DIVINITY
            GoodnessKind.INNOCENT_VICTIM -> GoodnessKindDb.INNOCENT_VICTIM
        }
    }
}
