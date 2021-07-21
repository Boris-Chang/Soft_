package ru.ifmo.software_engineering.afterlife.classificator.repositories

import kotlinx.coroutines.runBlocking
import org.jooq.DSLContext
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.database.tables.Souls
import ru.ifmo.software_engineering.afterlife.utils.RandomGenerator
import java.util.*
import kotlin.test.assertEquals

@SpringBootTest
class SoulRepositoryIntegrationTests {
    @Autowired
    lateinit var dsl: DSLContext;
    @Autowired
    lateinit var soulRepository: SoulRepository

    @Test
    fun insertOne_soulPassed_createsNewSoul() = runBlocking {
        //Arrange
        val soulToInsert = Soul(
            0,
            RandomGenerator.generateRandomString(10),
            RandomGenerator.generateRandomString(10),
            Date()
        )

        //Act
        val result = soulRepository.insertOne(soulToInsert)

        //Assert
        assertEquals(soulToInsert.copy(id = result.id), result)

        val count = dsl.selectFrom(Souls.SOULS)
            .where(Souls.SOULS.ID.eq(result.id))
            .count()
        assertEquals(1, count)
    }
}