package ru.ifmo.software_engineering.afterlife.classificator.repositories

import kotlinx.coroutines.runBlocking
import org.apache.commons.lang3.RandomStringUtils
import org.jooq.DSLContext
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import org.springframework.boot.test.context.SpringBootTest
import ru.ifmo.software_engineering.afterlife.classificator.domain.ArguedClassifierDecision
import ru.ifmo.software_engineering.afterlife.classificator.domain.HellCircle
import ru.ifmo.software_engineering.afterlife.classificator.domain.ParadiseSphere
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.database.Tables.ARGUED_CLASSIFIER_DECISION
import ru.ifmo.software_engineering.afterlife.database.enums.AfterworldKind
import ru.ifmo.software_engineering.afterlife.database.enums.ArguedByKind
import ru.ifmo.software_engineering.afterlife.database.tables.Souls.SOULS
import ru.ifmo.software_engineering.afterlife.database.tables.records.ArguedClassifierDecisionRecord
import ru.ifmo.software_engineering.afterlife.utils.spek_spring.createContext
import java.time.ZonedDateTime
import kotlin.test.assertEquals

@SpringBootTest
object ArguedClassifierDecisionRepositoryTests : Spek({

    lateinit var soulRepository: SoulRepository
    lateinit var dsl: DSLContext
    lateinit var repository: ArguedClassifierDecisionRepository

    beforeEachGroup {
        val ctx = createContext(ArguedClassifierDecisionRepositoryTests::class)
        soulRepository = ctx.inject()
        dsl = ctx.inject()
        repository = ctx.inject()
    }

    lateinit var testSoul: Soul
    beforeEachGroup {
        testSoul = runBlocking {
            soulRepository.insertOne(
                    Soul(0, RandomStringUtils.random(10), RandomStringUtils.random(10), ZonedDateTime.now(),
                            null))
        }
    }
    afterEachGroup {
        dsl.truncate(SOULS).cascade()
    }

    Feature("Get Argue by soul") {
        lateinit var result: ArguedClassifierDecision
        fun findArgueForSoul() = runBlocking {
            result = repository.findArgueForSoul(testSoul)
        }

        Scenario("No Argues for soul") {
            When("getting argue by soul") {
                findArgueForSoul()
            }

            Then("Result should be for not argued") {
                val expectedResult = ArguedClassifierDecision(
                        null, null, false, false)

                assertEquals(expectedResult, result)
            }
        }

        Scenario("Only god Argue exist for soul") {
            Given("God argued classifier decision") {
                dsl.insertInto(ARGUED_CLASSIFIER_DECISION)
                        .set(ArguedClassifierDecisionRecord().apply {
                            this.arguedBy = ArguedByKind.GOD
                            this.soulId = testSoul.id
                            this.afterworldKind = AfterworldKind.HELL
                            this.sectionNumber = 5
                        })
                        .execute()
            }
            When("getting argue by soul") {
                findArgueForSoul()
            }

            Then("Result should be for not argued") {
                val expectedResult = ArguedClassifierDecision(
                        HellCircle(5), null, true, false)

                assertEquals(expectedResult, result)
            }
        }

        Scenario("Only devil Argue exist for soul") {
            Given("God argued classifier decision") {
                dsl.insertInto(ARGUED_CLASSIFIER_DECISION)
                        .set(ArguedClassifierDecisionRecord().apply {
                            this.arguedBy = ArguedByKind.DEVIL
                            this.soulId = testSoul.id
                            this.afterworldKind = AfterworldKind.HELL
                            this.sectionNumber = 5
                        })
                        .execute()
            }
            When("getting argue by soul") {
                findArgueForSoul()
            }

            Then("Result should be for not argued") {
                val expectedResult = ArguedClassifierDecision(
                        null, HellCircle(5), false, true)

                assertEquals(expectedResult, result)
            }
        }

        Scenario("Both Devil's and God's Argue exist for soul") {
            Given("both argued classifier decision") {
                dsl.batchInsert(listOf(
                        (ArguedClassifierDecisionRecord().apply {
                            this.arguedBy = ArguedByKind.DEVIL
                            this.soulId = testSoul.id
                            this.afterworldKind = AfterworldKind.HELL
                            this.sectionNumber = 5}),
                        ArguedClassifierDecisionRecord().apply {
                            this.arguedBy = ArguedByKind.GOD
                            this.soulId = testSoul.id
                            this.afterworldKind = AfterworldKind.PARADISE
                            this.sectionNumber = 4}))
                        .execute()
            }
            When("getting argue by soul") {
                findArgueForSoul()
            }

            Then("Result should be for not argued") {
                val expectedResult = ArguedClassifierDecision(
                        ParadiseSphere(4), HellCircle(5), true, true)

                assertEquals(expectedResult, result)
            }
        }
    }

    Feature("Update argue") {
        lateinit var result: ArguedClassifierDecision

        fun argueSoul(argue: ArguedClassifierDecision) = runBlocking {
            result = repository.updateSoulArgue(testSoul, argue)
        }

        fun findActualArgue() = runBlocking {
            repository.findArgueForSoul(testSoul)
        }

        val testNotExisted = listOf(
                ArguedClassifierDecision(HellCircle(1), ParadiseSphere(2), true, true),
                ArguedClassifierDecision(HellCircle(1), null, true, false),
                ArguedClassifierDecision(null, ParadiseSphere(2), false, true),
                ArguedClassifierDecision(null, ParadiseSphere(2), true, true),
                ArguedClassifierDecision(HellCircle(1), null, true, true),
                ArguedClassifierDecision(null, null, true, true),
                ArguedClassifierDecision(null, null, false, false),
        )

        testNotExisted.forEach {
                Scenario("Update argue that not existed, yet") {
                    When("updating argue that was not previously argued") {
                        argueSoul(it)
                    }

                    Then("Argue saved correctly") {
                        assertEquals(it, result)
                    }

                    And("Actual argue is same") {
                        val actualArgue = findActualArgue()
                        assertEquals(actualArgue, result)
                    }
                }
        }

        val testExisted = listOf(
                Pair(
                        ArguedClassifierDecision(null, null, true, true),
                        ArguedClassifierDecision(HellCircle(1), ParadiseSphere(2), true, true)),
                Pair(
                        ArguedClassifierDecision(HellCircle(1), null, true, true),
                        ArguedClassifierDecision(HellCircle(2), ParadiseSphere(2), true, true)),
                Pair(
                        ArguedClassifierDecision(null, ParadiseSphere(2), true, true),
                        ArguedClassifierDecision(HellCircle(2), ParadiseSphere(2), true, true)),
                Pair(
                        ArguedClassifierDecision(HellCircle(1), ParadiseSphere(2), true, true),
                        ArguedClassifierDecision(HellCircle(2), ParadiseSphere(2), true, true)),
                Pair(
                        ArguedClassifierDecision(null, null, true, false),
                        ArguedClassifierDecision(null, null, true, true)),
                Pair(
                        ArguedClassifierDecision(null, null, false, true),
                        ArguedClassifierDecision(null, null, true, true)),
        )

        testExisted.forEach {
            val (existed, updated) = it
            Scenario("Update argue that already existed") {
                Given("Soul was argued") {
                    argueSoul(existed)
                }

                When("updating argue that was previously argued") {
                    argueSoul(updated)
                }

                Then("Argue updated correctly") {
                    assertEquals(updated, result)
                }

                And("Actual argue is same") {
                    val actualArgue = findActualArgue()
                    assertEquals(actualArgue, result)
                }
            }
        }
    }
})