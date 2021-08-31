package ru.ifmo.software_engineering.afterlife.classificator.database.unmappers

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinEvidence
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinKind
import ru.ifmo.software_engineering.afterlife.database.tables.records.SinEvidencesRecord

private typealias SinKindDb = ru.ifmo.software_engineering.afterlife.database.enums.SinKind

@Component
class SinsEvidenceUnmapper : RecordUnmapper<SinEvidence, SinEvidencesRecord> {
    override fun unmap(model: SinEvidence?): SinEvidencesRecord =
        SinEvidencesRecord().apply {
            this.kind = unmapSinKind(model!!.kind)
            this.attonedAt = model.attonedAt?.toOffsetDateTime()
            this.dateOfSin = model.dateOfSin.toOffsetDateTime()
        }

    private fun unmapSinKind(sinKind: SinKind): SinKindDb =
        when(sinKind) {
            SinKind.UNBAPTIZED -> SinKindDb.UNBAPTIZED
            SinKind.VOLUPTUOUSNESS -> SinKindDb.VOLUPTUOUSNESS
            SinKind.GLUTTONY -> SinKindDb.GLUTTONY
            SinKind.WASTEFULNESS -> SinKindDb.WASTEFULNESS
            SinKind.AVARICE -> SinKindDb.AVARICE
            SinKind.PRIDE -> SinKindDb.PRIDE
            SinKind.HERETICNESS -> SinKindDb.HERETICNESS
            SinKind.FALSE_TEACHING -> SinKindDb.FALSE_TEACHING
            SinKind.VIOLENCE -> SinKindDb.VIOLENCE
            SinKind.DECEPTION_WHO_NOT_TRUST -> SinKindDb.DECEPTION_WHO_NOT_TRUST
            SinKind.DECEPTION_WHO_TRUST -> SinKindDb.DECEPTION_WHO_TRUST
        }
}