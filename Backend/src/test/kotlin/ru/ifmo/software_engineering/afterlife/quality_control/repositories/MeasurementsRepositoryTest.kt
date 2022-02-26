package ru.ifmo.software_engineering.afterlife.quality_control.repositories

import kotlinx.coroutines.runBlocking
import org.apache.commons.lang3.RandomStringUtils
import org.jooq.DSLContext
import org.jooq.impl.DSL.count
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import org.springframework.boot.test.context.SpringBootTest
import ru.ifmo.software_engineering.afterlife.classificator.repositories.ArguedClassifierDecisionRepositoryTests
import ru.ifmo.software_engineering.afterlife.core.models.PageRequest
import ru.ifmo.software_engineering.afterlife.core.models.PagedResult
import ru.ifmo.software_engineering.afterlife.database.Tables.MEASUREMENTS
import ru.ifmo.software_engineering.afterlife.database.Tables.THRESHOLDS
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Measurement
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Threshold
import ru.ifmo.software_engineering.afterlife.utils.spek_spring.createContext
import java.util.*
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest
object MeasurementsRepositoryTest : Spek({
    lateinit var repository: MeasurementsRepository
    lateinit var dsl: DSLContext

    beforeEachGroup {
        val ctx = createContext(ArguedClassifierDecisionRepositoryTests::class)
        dsl = ctx.inject()
        repository = ctx.inject()

        dsl.truncate(MEASUREMENTS).cascade().execute()
        dsl.truncate(THRESHOLDS).cascade().execute()
    }

    fun randomString() = RandomStringUtils.random(10)

    Feature("save") {
        lateinit var result: Measurement

        fun saveMeasurement(measurement: Measurement) {
            result = runBlocking {
                repository.save(measurement)
            }
        }

        Scenario("Creating measurement without threshold") {
            lateinit var measurementToInsert: Measurement
            Given("Measurement without threshold") {
                fun randomString() = RandomStringUtils.random(10)
                measurementToInsert = Measurement(0, randomString(), randomString(), randomString(), null)
            }

            When("saving measurement") {
                saveMeasurement(measurementToInsert)
            }

            Then("New measurement with same fields should be returned with generated id") {
                val expected = measurementToInsert.copy(id = result.id)
                assertEquals(expected, result)
            }

            And("New measurement should be created in database and should be same as result") {
                val createdMeasurementRecords = dsl.select().from(MEASUREMENTS)
                        .where(MEASUREMENTS.ID.eq(result.id))
                        .fetch().map { it.into(MEASUREMENTS) }
                assertEquals(1, createdMeasurementRecords.size)
                val createdMeasurement = createdMeasurementRecords.first().let {
                    Measurement(it.id, it.title, it.xCaption, it.yCaption, null)
                }

                assertEquals(createdMeasurement, result)
            }

            And("Threshold should not be created") {
                val thresholdsCount = dsl.select(count()).from(THRESHOLDS).fetch().first().value1()
                assertEquals(thresholdsCount, 0)
            }
        }

        Scenario("Creating measurement with threshold") {
            lateinit var measurementToInsert: Measurement
            Given("Measurement with threshold") {
                measurementToInsert = Measurement(0, randomString(), randomString(), randomString(),
                        Threshold(Random().nextDouble()))
            }

            When("saving measurement") {
                saveMeasurement(measurementToInsert)
            }

            Then("New measurement with same fields should be returned with generated id") {
                val expected = measurementToInsert.copy(id = result.id)
                assertEquals(expected, result)
            }

            And("New measurement should be created in database and should be same as result") {
                val createdMeasurementRecords = dsl.select().from(MEASUREMENTS)
                        .where(MEASUREMENTS.ID.eq(result.id))
                        .fetch().map { it.into(MEASUREMENTS) }
                assertEquals(1, createdMeasurementRecords.size)
                val createdMeasurement = createdMeasurementRecords.first().let {
                    Measurement(it.id, it.title, it.xCaption, it.yCaption, measurementToInsert.threshold)
                }

                assertEquals(createdMeasurement, result)
            }

            And("Threshold should be created") {
                val createdThresholds = dsl.select().from(THRESHOLDS)
                        .where(THRESHOLDS.MEASUREMENT_ID.eq(result.id))
                        .fetch()
                assertEquals(createdThresholds.size, 1)

                val createdThreshold = createdThresholds.first().let {
                    Threshold(it.into(THRESHOLDS).value)
                }

                assertEquals(createdThreshold, result.threshold)
                assertEquals(createdThreshold, measurementToInsert.threshold)
            }
        }
    }

    fun createMeasurementWithoutThreshold(): Measurement = runBlocking {
        repository.save(Measurement(0, randomString(), randomString(), randomString(), null))
    }
    fun createMeasurementWithThreshold(): Measurement = runBlocking {
        repository.save(Measurement(0, randomString(), randomString(), randomString(),
                Threshold(Random().nextDouble())))
    }

    Feature("update") {
        lateinit var measurementWithoutThreshold: Measurement
        lateinit var measurementWithThreshold: Measurement
        beforeEachScenario {
            measurementWithoutThreshold = createMeasurementWithoutThreshold()
            measurementWithThreshold = createMeasurementWithThreshold()
        }

        lateinit var result: Measurement
        fun updateMeasurement(measurement: Measurement) {
            result = runBlocking { repository.update(measurement) }
        }

        listOf<(Measurement) -> Measurement>(
                { Measurement(it.id, randomString(), randomString(), randomString(), null) },
                { Measurement(it.id, randomString(), randomString(), randomString(),
                        Threshold(Random().nextDouble())) },
        ).forEach { measurementUpdateFn ->
            Scenario("Updating existing measurement with threshold") {
                Given("measurement with threshold exists") { }

                lateinit var measurementToUpdate: Measurement
                When("Updating measurement") {
                    measurementToUpdate = measurementUpdateFn(measurementWithThreshold)
                    updateMeasurement(measurementToUpdate)
                }

                Then("updated measurement should be returned which is same as passed param") {
                    assertEquals(measurementToUpdate, result)
                }

                And("Measurement should have threshold if provided or not have it if was not provided") {
                    val expectedThresholdsCount = if (measurementToUpdate.threshold == null) 0 else 1
                    val actualThresholdsCount = dsl.select(count()).from(THRESHOLDS)
                            .where(THRESHOLDS.MEASUREMENT_ID.eq(measurementToUpdate.id))
                            .first().value1()

                    assertEquals(expectedThresholdsCount, actualThresholdsCount)
                }
            }

            Scenario("Updating existing measurement without threshold") {
                Given("measurement without threshold exists") { }

                lateinit var measurementToUpdate: Measurement
                When("Updating measurement") {
                    measurementToUpdate = measurementUpdateFn(measurementWithoutThreshold)
                    updateMeasurement(measurementToUpdate)
                }

                Then("updated measurement should be returned which is same as passed param") {
                    assertEquals(measurementToUpdate, result)
                }

                And("Measurement should have threshold if provided or not have it if was not provided") {
                    val expectedThresholdsCount = if (measurementToUpdate.threshold == null) 0 else 1
                    val actualThresholdsCount = dsl.select(count()).from(THRESHOLDS)
                            .where(THRESHOLDS.MEASUREMENT_ID.eq(measurementToUpdate.id))
                            .first().value1()

                    assertEquals(expectedThresholdsCount, actualThresholdsCount)
                }
            }
        }
    }

    Feature("findAll") {
        lateinit var result: PagedResult<Measurement>
        fun findAllMeasurement() {
            result = runBlocking { repository.findAll(PageRequest.default()) }
        }

        Scenario("Few different measurements exist") {
            lateinit var existingMeasurements: List<Measurement>
            Given("Exist 2 measurements with threshold and 2 without") {
                existingMeasurements = listOf(
                        createMeasurementWithThreshold(), createMeasurementWithThreshold(),
                        createMeasurementWithoutThreshold(), createMeasurementWithoutThreshold()
                )
            }

            When("Getting all measurements") {
                findAllMeasurement()
            }

            Then("All existing measurements should be included") {
                existingMeasurements.forEach {
                        assertContains(result.results, it)
                }
            }
        }
    }

    Feature("findById") {
        var result: Measurement? = null

        fun findById(id: Long) {
            result = runBlocking { repository.findById(id) }
        }

        Scenario("Getting measurement with threshold") {
            Given("Measurement does not exist") {}

            When("Getting measurement by id") {
                findById(0)
            }

            Then("Should return null") {
                assertNull(result)
            }
        }

        Scenario("Getting measurement with threshold") {
            lateinit var existingMeasurement: Measurement
            Given("Exists measurement with threshold") {
                existingMeasurement = createMeasurementWithThreshold()
            }

            When("Getting measurement by id") {
                findById(existingMeasurement.id)
            }

            Then("All existing measurements should be included") {
                assertEquals(result, existingMeasurement)
            }

            And("Threshold should not be null") {
                assertNotNull(result!!.threshold)
            }
        }

        Scenario("Getting measurement without threshold") {
            lateinit var existingMeasurement: Measurement
            Given("Exists measurement without threshold") {
                existingMeasurement = createMeasurementWithoutThreshold()
            }

            When("Getting measurement by id") {
                findById(existingMeasurement.id)
            }

            Then("All existing measurements should be included") {
                assertEquals(result, existingMeasurement)
            }

            And("Threshold should be null") {
                assertNull(result!!.threshold)
            }
        }
    }
})