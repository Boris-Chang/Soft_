package ru.ifmo.software_engineering.afterlife.quality_control.repositories

import kotlinx.coroutines.runBlocking
import org.apache.commons.lang3.RandomStringUtils
import org.jooq.DSLContext
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import ru.ifmo.software_engineering.afterlife.classificator.repositories.ArguedClassifierDecisionRepositoryTests
import ru.ifmo.software_engineering.afterlife.database.Tables.MEASUREMENTS
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Measurement
import ru.ifmo.software_engineering.afterlife.quality_control.domain.ThresholdAlert
import ru.ifmo.software_engineering.afterlife.utils.spek_spring.createContext
import kotlin.test.assertContains
import kotlin.test.assertEquals

object ThresholdAlertsRepositoryTest : Spek({
    lateinit var repository: ThresholdAlertsRepository
    lateinit var measurementsRepository: MeasurementsRepository
    lateinit var dsl: DSLContext

    beforeEachGroup {
        val ctx = createContext(ArguedClassifierDecisionRepositoryTests::class)

        dsl = ctx.inject()
        repository = ctx.inject()
        measurementsRepository = ctx.inject()

        dsl.truncate(MEASUREMENTS).cascade().execute()
    }

    fun randomString() = RandomStringUtils.random(10)
    fun createMeasurement() = runBlocking {
        measurementsRepository.save(Measurement(0, randomString(), randomString(), randomString(), null))
    }

    Feature("save") {
        lateinit var result: ThresholdAlert
        fun saveAlert(thresholdAlert: ThresholdAlert) {
            result = runBlocking { repository.save(thresholdAlert) }
        }

        lateinit var measurement: Measurement
        beforeEachScenario {
            measurement = createMeasurement()
        }

        Scenario("Saving alert") {
            lateinit var alertToCreate: ThresholdAlert
            Given("Alert to create") {
                alertToCreate = ThresholdAlert(0, measurement.id, randomString())
            }

            When("Saving alert") {
                saveAlert(alertToCreate)
            }

            Then("Returned created alert") {
                val expectedAlert = alertToCreate.copy(id = result.id)
                assertEquals(expectedAlert, result)
            }
        }
    }

    Feature("findAllByMeasurement") {
        fun createAlertForMeasurement(measurement: Measurement) = runBlocking {
            repository.save(ThresholdAlert(0, measurement.id, randomString()))
        }

        Scenario("Getting alerts for few measurements with few threshold alerts") {
            lateinit var measurement1: Measurement
            lateinit var measurement2: Measurement
            Given("2 Measurements exist") {
                measurement1 = createMeasurement()
                measurement2 = createMeasurement()
            }

            lateinit var alertsOfMeasurement1: List<ThresholdAlert>
            lateinit var alertsOfMeasurement2: List<ThresholdAlert>
            And("first measurement has 2 alerts and second - 1") {
                alertsOfMeasurement1 = listOf(createAlertForMeasurement(measurement1), createAlertForMeasurement(measurement1))
                alertsOfMeasurement2 = listOf(createAlertForMeasurement(measurement2))
            }

            lateinit var foundAlertsOfMeasurement1: List<ThresholdAlert>
            lateinit var foundAlertsOfMeasurement2: List<ThresholdAlert>
            When("Finding alerts for both measurements") {
                runBlocking {
                    foundAlertsOfMeasurement1 = repository.findAllByMeasurement(measurement1)
                    foundAlertsOfMeasurement2 = repository.findAllByMeasurement(measurement2)
                }
            }

            Then("Found alerts should contain all existing alerts") {
                alertsOfMeasurement1.forEach {
                    assertContains(foundAlertsOfMeasurement1, it)
                }
                alertsOfMeasurement2.forEach {
                    assertContains(foundAlertsOfMeasurement2, it)
                }
            }
        }
    }
})