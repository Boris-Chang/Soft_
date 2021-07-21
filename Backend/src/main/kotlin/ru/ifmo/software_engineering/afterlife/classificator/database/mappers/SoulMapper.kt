package ru.ifmo.software_engineering.afterlife.classificator.database.mappers

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.database.tables.Souls
import ru.ifmo.software_engineering.afterlife.database.tables.records.SoulsRecord
import java.time.ZoneOffset
import java.util.*

@Component
class SoulMapper : RecordMapper<SoulsRecord, Soul> {
    override fun map(record: SoulsRecord?): Soul? {
        return record?.map {
            Soul(
                it[Souls.SOULS.ID],
                it[Souls.SOULS.FIRST_NAME],
                it[Souls.SOULS.LAST_NAME],
                Date.from(it[Souls.SOULS.DATE_OF_DEATH].toInstant(ZoneOffset.UTC))
            )
        }
    }
}
