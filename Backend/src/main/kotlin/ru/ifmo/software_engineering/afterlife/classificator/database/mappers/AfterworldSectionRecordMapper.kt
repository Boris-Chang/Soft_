package ru.ifmo.software_engineering.afterlife.classificator.database.mappers

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.AfterworldSection
import ru.ifmo.software_engineering.afterlife.classificator.domain.HellCircle
import ru.ifmo.software_engineering.afterlife.classificator.domain.ParadiseSphere
import ru.ifmo.software_engineering.afterlife.database.enums.AfterworldKind
import ru.ifmo.software_engineering.afterlife.database.tables.records.SoulAfterworldLocationRecord

@Component
class AfterworldSectionRecordMapper : RecordMapper<SoulAfterworldLocationRecord, AfterworldSection> {
    override fun map(p0: SoulAfterworldLocationRecord?): AfterworldSection? {
        return when (p0?.kind) {
            null -> null
            AfterworldKind.HELL -> HellCircle(p0.sectionNumber)
            AfterworldKind.PARADISE -> ParadiseSphere(p0.sectionNumber)
        }
    }
}