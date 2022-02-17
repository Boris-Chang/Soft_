package ru.ifmo.software_engineering.afterlife.classificator.database.mappers

import org.jooq.RecordMapper
import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.AfterworldKind
import ru.ifmo.software_engineering.afterlife.classificator.domain.AfterworldSection
import ru.ifmo.software_engineering.afterlife.classificator.domain.HellCircle
import ru.ifmo.software_engineering.afterlife.classificator.domain.ParadiseSphere
import ru.ifmo.software_engineering.afterlife.database.enums.AfterworldKind as AfterworldKindDb
import ru.ifmo.software_engineering.afterlife.database.tables.records.ArguedClassifierDecisionRecord

@Component
class ArguedClassifierDecisionMapper : RecordMapper<ArguedClassifierDecisionRecord, AfterworldSection>,
        RecordUnmapper<AfterworldSection, ArguedClassifierDecisionRecord> {

    override fun map(record: ArguedClassifierDecisionRecord?): AfterworldSection? {
        return when (record?.afterworldKind) {
            null -> null
            AfterworldKindDb.HELL -> HellCircle(record.sectionNumber)
            AfterworldKindDb.PARADISE -> ParadiseSphere(record.sectionNumber)
        }
    }

    override fun unmap(section: AfterworldSection?): ArguedClassifierDecisionRecord =
        ArguedClassifierDecisionRecord().apply {
            this.sectionNumber = section?.sectionIndex
            this.afterworldKind = when (section?.afterwoldKind) {
                AfterworldKind.HELL -> AfterworldKindDb.HELL
                AfterworldKind.PARADISE -> AfterworldKindDb.PARADISE
                null -> null
        }
    }
}