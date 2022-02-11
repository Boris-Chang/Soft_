package ru.ifmo.software_engineering.afterlife.classificator.database.mappers

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.AfterworldSection
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.database.Tables.SOUL_AFTERWORLD_LOCATION
import ru.ifmo.software_engineering.afterlife.database.tables.Souls
import ru.ifmo.software_engineering.afterlife.database.tables.records.SoulAfterworldLocationRecord

@Component
class SoulMapper(
        private val afterworldSectionRecordMapper: RecordMapper<SoulAfterworldLocationRecord, AfterworldSection>
) : RecordMapper<org.jooq.Record, Soul> {
    override fun map(record: org.jooq.Record?): Soul? {
        return record?.map {
            Soul(
                it[Souls.SOULS.ID],
                it[Souls.SOULS.FIRST_NAME],
                it[Souls.SOULS.LAST_NAME],
                it[Souls.SOULS.DATE_OF_DEATH].toZonedDateTime(),
                afterworldSectionRecordMapper.map(record.into(SOUL_AFTERWORLD_LOCATION))
            )
        }
    }
}
