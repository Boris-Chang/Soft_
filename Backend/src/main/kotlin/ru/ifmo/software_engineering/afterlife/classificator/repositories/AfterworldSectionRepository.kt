package ru.ifmo.software_engineering.afterlife.classificator.repositories

import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.awaitFirst
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.RecordUnmapper
import org.jooq.impl.DSL.count
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.software_engineering.afterlife.classificator.domain.AfterworldSection
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.database.Tables.SOUL_AFTERWORLD_LOCATION
import ru.ifmo.software_engineering.afterlife.database.tables.records.SoulAfterworldLocationRecord

interface AfterworldSectionRepository {
    suspend fun saveOrUpdateAfterworldSectionForSoul(soul: Soul, afterworldSection: AfterworldSection)
}

@Repository
class AfterworldSectionRepositoryImpl(
        private val dsl: DSLContext,
        private val unmapper: RecordUnmapper<AfterworldSection, SoulAfterworldLocationRecord>
) : AfterworldSectionRepository {
    @Transactional
    override suspend fun saveOrUpdateAfterworldSectionForSoul(soul: Soul, afterworldSection: AfterworldSection) {
        val recordToSet = unmapper.unmap(afterworldSection)

        if (isSoulSectionKnown(soul)) {
            dsl.update(SOUL_AFTERWORLD_LOCATION)
                    .set(recordToSet)
                    .where(soulIdIsFor(soul))
                    .executeAsync().await()
        } else {
            dsl.insertInto(SOUL_AFTERWORLD_LOCATION)
                    .set(recordToSet)
                    .set(SOUL_AFTERWORLD_LOCATION.SOUL_ID, soul.id)
                    .executeAsync().await()
        }
    }

    private suspend fun isSoulSectionKnown(soul: Soul): Boolean {
        val countField = count()
        return dsl.select(countField)
                .from(SOUL_AFTERWORLD_LOCATION)
                .where(soulIdIsFor(soul))
                .awaitFirst()
                .getValue(countField) > 0
    }


    private fun soulIdIsFor(soul: Soul): Condition = SOUL_AFTERWORLD_LOCATION.SOUL_ID.eq(soul.id)
}
