package ru.ifmo.software_engineering.afterlife.classificator.repositories

import kotlinx.coroutines.runBlocking
import org.jooq.DSLContext
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.ifmo.software_engineering.afterlife.classificator.domain.HellCircle
import ru.ifmo.software_engineering.afterlife.classificator.domain.ParadiseSphere
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.core.models.PageRequest
import ru.ifmo.software_engineering.afterlife.database.Tables.*
import ru.ifmo.software_engineering.afterlife.database.enums.AfterworldKind
import ru.ifmo.software_engineering.afterlife.database.enums.GoodnessKind
import ru.ifmo.software_engineering.afterlife.database.enums.SinKind
import ru.ifmo.software_engineering.afterlife.database.tables.Souls
import ru.ifmo.software_engineering.afterlife.database.tables.records.GoodnessEvidencesRecord
import ru.ifmo.software_engineering.afterlife.database.tables.records.SinEvidencesRecord
import ru.ifmo.software_engineering.afterlife.database.tables.records.SinsReportsRecord
import ru.ifmo.software_engineering.afterlife.database.tables.records.SoulAfterworldLocationRecord
import ru.ifmo.software_engineering.afterlife.utils.RandomGenerator
import ru.ifmo.software_engineering.afterlife.utils.inCurrentZone
import ru.ifmo.software_engineering.afterlife.utils.truncatedToSeconds
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest
class SoulRepositoryIntegrationTests {
    @Autowired
    lateinit var dsl: DSLContext
    @Autowired
    lateinit var soulRepository: SoulRepository

    @Test
    fun insertOne_soulPassed_createsNewSoul() = runBlocking {
        // Arrange
        val soulToInsert = Soul(
            0,
            RandomGenerator.generateRandomString(10),
            RandomGenerator.generateRandomString(10),
            ZonedDateTime.now(),
            null
        )

        // Act
        val result = soulRepository.insertOne(soulToInsert)

        // Assert
        assertSoulsAreEqual(soulToInsert, result)

        val count = dsl.selectFrom(Souls.SOULS)
            .where(Souls.SOULS.ID.eq(result.id))
            .count()
        assertEquals(1, count)
    }

    @Test
    fun getSoulById_soulWithoutReportsAndClassifiedSection_returnsRequestedSoul() = runBlocking {
        //Arrange
        val soulToInsert = Soul(
                0,
                RandomGenerator.generateRandomString(10),
                RandomGenerator.generateRandomString(10),
                ZonedDateTime.now(),
                null
        )
        val createdSoul = soulRepository.insertOne(soulToInsert)

        //Act
        val foundSoul = soulRepository.findById(createdSoul.id)

        //Assert
        assertNotNull(foundSoul)
        assertSoulsAreEqual(soulToInsert, foundSoul)
        assertSoulsAreEqual(createdSoul, foundSoul)
    }

    @Test
    fun getSoulById_soulWithClassifiedSection_returnsRequestedSoulWithClassifiedSection() = runBlocking {
        //Arrange
        val soulToInsert = Soul(
                0,
                RandomGenerator.generateRandomString(10),
                RandomGenerator.generateRandomString(10),
                ZonedDateTime.now(),
                HellCircle(4)
        )
        val createdSoul = soulRepository.insertOne(soulToInsert)
        val afterworldLocationRecord = SoulAfterworldLocationRecord().apply {
            this.soulId = createdSoul.id
            this.sectionNumber = soulToInsert.classifiedAfterworldSection?.sectionIndex
            this.kind = AfterworldKind.HELL
        }
        dsl.insertInto(SOUL_AFTERWORLD_LOCATION).set(afterworldLocationRecord).execute()

        //Act
        val foundSoul = soulRepository.findById(createdSoul.id)

        //Assert
        assertNotNull(foundSoul)
        assertSoulsAreEqual(soulToInsert, foundSoul)
        assertSoulsAreEqual(createdSoul.copy(classifiedAfterworldSection = soulToInsert.classifiedAfterworldSection), foundSoul)
    }

    @Test
    fun getReportedSouls_soulsWithoutClassifiedSectionAndReports_returnsRequestedSoulWithClassifiedSection() = runBlocking {
        //Arrange
        dsl.truncate(SOULS).cascade().execute()
        val soulToInsert1 = Soul(
                0,
                RandomGenerator.generateRandomString(10),
                RandomGenerator.generateRandomString(10),
                ZonedDateTime.now(),
                null
        )
        val soulToInsert2 = Soul(
                0,
                RandomGenerator.generateRandomString(10),
                RandomGenerator.generateRandomString(10),
                ZonedDateTime.now(),
                null
        )
        val createdSoul1 = soulRepository.insertOne(soulToInsert1)
        val createdSoul2 = soulRepository.insertOne(soulToInsert2)

        //Act
        val foundSoul1 = soulRepository.findById(createdSoul1.id)
        val foundSoul2 = soulRepository.findById(createdSoul2.id)
        val listOfReportedSouls = soulRepository.getReportedSouls(null, PageRequest.default())

        //Assert
        assertNotNull(foundSoul1)
        assertNotNull(foundSoul2)
        assertSoulsAreEqual(soulToInsert1, foundSoul1)
        assertSoulsAreEqual(soulToInsert2, foundSoul2)
        assertSoulsAreEqual(foundSoul1, listOfReportedSouls.results[1].soul)
        assertSoulsAreEqual(foundSoul2, listOfReportedSouls.results[0].soul)
        assert(listOfReportedSouls.results.all { it.goodnessReport == null && it.sinsReport == null })
    }

    @Test
    fun getReportedSouls_soulsWithClassifiedSectionAndReports_returnsRequestedSoulWithClassifiedSection() = runBlocking {
        //Arrange
        dsl.truncate(SOULS).cascade().execute()
        val soulToInsert1 = Soul(
                0,
                RandomGenerator.generateRandomString(10),
                RandomGenerator.generateRandomString(10),
                ZonedDateTime.now(),
                ParadiseSphere(3)
        )
        val soulToInsert2 = Soul(
                0,
                RandomGenerator.generateRandomString(10),
                RandomGenerator.generateRandomString(10),
                ZonedDateTime.now(),
                null
        )
        val createdSoul1 = soulRepository.insertOne(soulToInsert1)
        val createdSoul2 = soulRepository.insertOne(soulToInsert2)

        val afterworldLocationRecord = SoulAfterworldLocationRecord().apply {
            this.soulId = createdSoul1.id
            this.sectionNumber = soulToInsert1.classifiedAfterworldSection?.sectionIndex
            this.kind = AfterworldKind.PARADISE
        }
        dsl.insertInto(SOUL_AFTERWORLD_LOCATION).set(afterworldLocationRecord).execute()

        val sinEvidenceRecords = (1..2).map {
            SinEvidencesRecord().apply {
                this.kind = SinKind.DESPONDENCY
                this.sinnedBySoulId = createdSoul2.id
                this.dateOfSin = ZonedDateTime.now().toOffsetDateTime()
                this.attonedAt = null
            }
        }
        dsl.insertInto(SINS_REPORTS).set(
                SinsReportsRecord().apply {
                    this.soulId = createdSoul2.id
                    this.uploadedAt = ZonedDateTime.now().toOffsetDateTime()
                }).execute()
        dsl.insertInto(GOODNESS_REPORTS).set(
                SinsReportsRecord().apply {
                    this.soulId = createdSoul1.id
                    this.uploadedAt = ZonedDateTime.now().toOffsetDateTime()
                }).execute()
        dsl.insertInto(GOODNESS_REPORTS).set(
                SinsReportsRecord().apply {
                    this.soulId = createdSoul2.id
                    this.uploadedAt = ZonedDateTime.now().toOffsetDateTime()
                }).execute()
        dsl.batchInsert(sinEvidenceRecords).execute()

        val goodnessEvidenceRecord1 = GoodnessEvidencesRecord().apply {
            this.kind = GoodnessKind.INNOCENT_VICTIM
            this.doneBySoulId = createdSoul1.id
            this.dateOfGoodDeedEvidence = ZonedDateTime.now().toOffsetDateTime()
        }
        val goodnessEvidenceRecord2 = GoodnessEvidencesRecord().apply {
            this.kind = GoodnessKind.INNOCENT_VICTIM
            this.doneBySoulId = createdSoul2.id
            this.dateOfGoodDeedEvidence = ZonedDateTime.now().toOffsetDateTime()
        }
        dsl.batchInsert(listOf(goodnessEvidenceRecord1, goodnessEvidenceRecord2)).execute()

        //Act
        val foundSoul1 = soulRepository.findById(createdSoul1.id)
        val foundSoul2 = soulRepository.findById(createdSoul2.id)
        val listOfReportedSouls = soulRepository.getReportedSouls(null, PageRequest.default())

        //Assert
        assertNotNull(foundSoul1)
        assertNotNull(foundSoul2)
        assertSoulsAreEqual(soulToInsert1, foundSoul1)
        assertSoulsAreEqual(soulToInsert2, foundSoul2)
        assertSoulsAreEqual(foundSoul1, listOfReportedSouls.results[1].soul)
        assertSoulsAreEqual(foundSoul2, listOfReportedSouls.results[0].soul)
        assert(listOfReportedSouls.results[0].sinsReport?.sins?.size == 2)
        assert(listOfReportedSouls.results[0].goodnessReport?.goodnessEvidences?.size == 1)
        assert(listOfReportedSouls.results[1].goodnessReport?.goodnessEvidences?.size == 1)
        assertNull(listOfReportedSouls.results[1].sinsReport)
    }

    private fun assertSoulsAreEqual(expected: Soul, actual: Soul) {
        val actualWithFixedDate = actual.copy(dateOfDeath = expected.dateOfDeath.truncatedToSeconds().inCurrentZone())
        val expectedWithFixedFields = expected.copy(
                dateOfDeath = expected.dateOfDeath.truncatedToSeconds().inCurrentZone(),
                id = if (expected.id == 0.toLong()) actual.id else expected.id
        )
        assertEquals(expectedWithFixedFields, actualWithFixedDate)
    }
}
