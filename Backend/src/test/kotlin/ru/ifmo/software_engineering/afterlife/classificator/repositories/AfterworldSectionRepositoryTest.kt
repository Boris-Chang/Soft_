package ru.ifmo.software_engineering.afterlife.classificator.repositories

import kotlinx.coroutines.runBlocking
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.ifmo.software_engineering.afterlife.classificator.domain.HellCircle
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.database.Tables.SOULS
import ru.ifmo.software_engineering.afterlife.database.Tables.SOUL_AFTERWORLD_LOCATION
import ru.ifmo.software_engineering.afterlife.database.enums.AfterworldKind
import java.time.ZonedDateTime
import kotlin.test.assertEquals

@SpringBootTest
class AfterworldSectionRepositoryTest {
    @Autowired
    lateinit var dsl: DSLContext
    @Autowired
    lateinit var afterworldSectionRepository: AfterworldSectionRepository
    var testSoul = Soul(0, "Test", "Test", ZonedDateTime.now())

    @BeforeEach
    fun before() {
        dsl.delete(SOUL_AFTERWORLD_LOCATION).execute()
        dsl.delete(SOULS).execute()
        val soulId = dsl.fetch("INSERT INTO Souls(first_name, last_name, date_of_death) VALUES(" +
                "'${testSoul.firstName}', '${testSoul.lastName}', now()) returning id")
                .map { it["id"].toString().toLong() }
                .first()
        testSoul = testSoul.copy(id = soulId)
    }

    @Test
    fun saveOrUpdateAfterworldSectionForSoul_afterworldSectionWasNotSet_createsNewAfterworldSection() = runBlocking {
        val hellCircle = HellCircle(2)

        afterworldSectionRepository.saveOrUpdateAfterworldSectionForSoul(testSoul, hellCircle)

        val (afterworldKind, afterworldSection) = dsl.select()
                .from(SOUL_AFTERWORLD_LOCATION)
                .where(SOUL_AFTERWORLD_LOCATION.SOUL_ID.eq(testSoul.id))
                .fetch()
                .map { Pair(it[SOUL_AFTERWORLD_LOCATION.KIND], it[SOUL_AFTERWORLD_LOCATION.SECTION_NUMBER]) }
                .first()
        assertEquals(afterworldKind, AfterworldKind.HELL)
        assertEquals(afterworldSection, 2)
    }

    @Test
    fun saveOrUpdateAfterworldSectionForSoul_afterworldSectionWasAlreadySet_updatesAfterworldSection() = runBlocking {
        dsl.insertInto(SOUL_AFTERWORLD_LOCATION)
                .set(SOUL_AFTERWORLD_LOCATION.SOUL_ID, testSoul.id)
                .set(SOUL_AFTERWORLD_LOCATION.KIND, AfterworldKind.PARADISE)
                .set(SOUL_AFTERWORLD_LOCATION.SECTION_NUMBER, 3)
                .execute()
        val hellCircle = HellCircle(2)

        afterworldSectionRepository.saveOrUpdateAfterworldSectionForSoul(testSoul, hellCircle)

        val (afterworldKind, afterworldSection) = dsl.select()
                .from(SOUL_AFTERWORLD_LOCATION)
                .where(SOUL_AFTERWORLD_LOCATION.SOUL_ID.eq(testSoul.id))
                .fetch()
                .map { Pair(it[SOUL_AFTERWORLD_LOCATION.KIND], it[SOUL_AFTERWORLD_LOCATION.SECTION_NUMBER]) }
                .first()
        assertEquals(afterworldKind, AfterworldKind.HELL)
        assertEquals(afterworldSection, 2)
    }
}