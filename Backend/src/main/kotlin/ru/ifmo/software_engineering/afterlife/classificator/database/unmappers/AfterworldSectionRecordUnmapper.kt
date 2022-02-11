package ru.ifmo.software_engineering.afterlife.classificator.database.unmappers

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.AfterworldKind
import ru.ifmo.software_engineering.afterlife.classificator.domain.AfterworldSection
import ru.ifmo.software_engineering.afterlife.database.tables.records.SoulAfterworldLocationRecord
import  ru.ifmo.software_engineering.afterlife.database.enums.AfterworldKind as AfterworldKindDb

@Component
class AfterworldSectionRecordUnmapper : RecordUnmapper<AfterworldSection, SoulAfterworldLocationRecord> {
    override fun unmap(afterworldSection: AfterworldSection?): SoulAfterworldLocationRecord {
        afterworldSection!!
        return SoulAfterworldLocationRecord().apply {
            kind = when (afterworldSection.afterwoldKind) {
                AfterworldKind.HELL -> AfterworldKindDb.HELL
                AfterworldKind.PARADISE -> AfterworldKindDb.PARADISE
            }

            sectionNumber = afterworldSection.sectionIndex
        }
    }
}