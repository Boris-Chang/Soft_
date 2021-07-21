package ru.ifmo.software_engineering.afterlife.classificator.repositories

import kotlinx.coroutines.future.await
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import ru.ifmo.software_engineering.afterlife.classificator.database.mappers.SoulMapper
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.database.tables.Souls
import java.time.LocalDateTime
import java.time.ZoneId

@Repository
class SoulRepository(
    private val dsl: DSLContext,
    private val soulMapper: SoulMapper
) {
    suspend fun insertOne(soul: Soul): Soul {
        return this.dsl.insertInto(Souls.SOULS)
            .columns(Souls.SOULS.FIRST_NAME, Souls.SOULS.LAST_NAME, Souls.SOULS.DATE_OF_DEATH)
            .values(
                soul.firstName,
                soul.lastName,
                LocalDateTime.ofInstant(soul.dateOfDeath.toInstant(), ZoneId.of("UTC"))
            )
            .returning()
            .fetchAsync()
            .thenApply { it.map(this.soulMapper).first() }
            .await()
    }
}
