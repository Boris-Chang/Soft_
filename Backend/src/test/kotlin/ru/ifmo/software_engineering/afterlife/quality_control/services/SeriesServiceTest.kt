package ru.ifmo.software_engineering.afterlife.quality_control.services

import arrow.core.Validated
import arrow.core.orNull
import kotlinx.coroutines.runBlocking
import org.apache.commons.lang3.RandomStringUtils
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import ru.ifmo.software_engineering.afterlife.core.exceptions.ApplicationException
import ru.ifmo.software_engineering.afterlife.core.exceptions.ForbiddenException
import ru.ifmo.software_engineering.afterlife.core.exceptions.NotFoundException
import ru.ifmo.software_engineering.afterlife.core.exceptions.UnauthorizedException
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Measurement
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Series
import ru.ifmo.software_engineering.afterlife.quality_control.repositories.MeasurementsRepository
import ru.ifmo.software_engineering.afterlife.quality_control.repositories.SeriesRepository
import ru.ifmo.software_engineering.afterlife.quality_control.repositories.SeriesValueRepository
import ru.ifmo.software_engineering.afterlife.security.Principal
import ru.ifmo.software_engineering.afterlife.security.services.AuthorizationService
import ru.ifmo.software_engineering.afterlife.users.constants.RoleNames
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

object SeriesServiceTest : Spek({
    val measurementsRepository: MeasurementsRepository = mock()
    val seriesRepository: SeriesRepository = mock()
    val seriesValueRepository: SeriesValueRepository = mock()
    val authorizationService: AuthorizationService = mock()

    val service = SeriesServiceImpl(measurementsRepository, seriesRepository, seriesValueRepository, authorizationService)

    fun setUserUnauthorized() {
        authorizationService.stub {
            onBlocking { it.getCurrentUser() } doReturn null
        }
    }

    fun setUserIsNotQualityControl() {
        val principal = mock<Principal>().stub {
            on { isInRole(RoleNames.QUALITY_CONTROL) } doReturn false
        }
        authorizationService.stub {
            onBlocking { it.getCurrentUser() } doReturn principal
        }
    }

    fun setUserIsQualityControl() {
        val principal = mock<Principal>().stub {
            on { isInRole(RoleNames.QUALITY_CONTROL) } doReturn true
        }
        authorizationService.stub {
            onBlocking { it.getCurrentUser() } doReturn principal
        }
    }

    fun setSeriesByIdNotExist(seriesId: Long) {
        seriesRepository.stub {
            onBlocking { it.findById(seriesId) } doReturn null
        }
    }

    fun setSeriesByIdExists(seriesId: Long, series: Series) {
        seriesRepository.stub {
            onBlocking { it.findById(seriesId) } doReturn series
        }
    }

    fun setMeasurementNotExist(measurementId: Long) {
        measurementsRepository.stub {
            onBlocking { it.findById(measurementId) } doReturn null
        }
    }

    fun setMeasurementExist(measurementId: Long, measurement: Measurement) {
        measurementsRepository.stub {
            onBlocking { it.findById(measurementId) } doReturn measurement
        }
    }

    Feature("findSeriesById") {
        val testSeriesId = Random().nextLong()

        lateinit var result: Validated<Throwable, Series?>
        fun findSeriesById() = runBlocking {
            result = Validated.catch { service.findSeriesById(testSeriesId) }
        }


        fun exceptionThrown(type: Class<out Throwable>) {
            assert(result.isInvalid)
            assertThat(result.swap().orNull(), instanceOf(type))
        }

        Scenario("Finding series by id as unauthorized user") {
            Given("User is not authorized") {
                setUserUnauthorized()
            }

            When("finding series by id") {
                findSeriesById()
            }

            Then("Unauthorized exception should be thrown") {
                exceptionThrown(UnauthorizedException::class.java)
            }
        }

        Scenario("Finding series by id as not QUALITY_CONTROL") {
            Given("User is not in role ${RoleNames.QUALITY_CONTROL}") {
                setUserIsNotQualityControl()
            }

            When("finding series by id") {
                findSeriesById()
            }

            Then("Forbidden exception should be thrown") {
                exceptionThrown(ForbiddenException::class.java)
            }
        }

        Scenario("Finding series by id as QUALITY_CONTROL and series not exist") {
            Given("User is in role ${RoleNames.QUALITY_CONTROL}") {
                setUserIsQualityControl()
            }

            And("Repository does not have test series") {
                setSeriesByIdNotExist(testSeriesId)
            }

            When("finding series by id") {
                findSeriesById()
            }

            Then("Should return null") {
                assert(result.isValid)
                assertNull(result.orNull())
            }
        }

        Scenario("Finding series by id as QUALITY_CONTROL and series exists") {
            Given("User is in role ${RoleNames.QUALITY_CONTROL}") {
                setUserIsQualityControl()
            }

            val expectedSeries = mock<Series>()
            And("Repository does not have test series") {
                setSeriesByIdExists(testSeriesId, expectedSeries)
            }

            When("finding series by id") {
                findSeriesById()
            }

            Then("Not null sereis should returned") {
                assert(result.isValid)
                assertEquals(expectedSeries, result.orNull())
            }
        }
    }

    Feature("findSeriesByMeasurementId") {
        val testMeasurementId = Random().nextLong()

        lateinit var result: Validated<ApplicationException, List<Series>>
        fun findSeriesByTestMeasurement() {
            result = runBlocking { service.findSeriesByMeasurementId(testMeasurementId) }
        }

        fun exceptionThrown(type: Class<out Throwable>) {
            assert(result.isInvalid)
            assertThat(result.swap().orNull(), instanceOf(type))
        }

        Scenario("Finding series by measurement as unauthorized user") {
            Given("User is not authorized") {
                setUserUnauthorized()
            }

            When("finding series by test measurement") {
                findSeriesByTestMeasurement()
            }

            Then("Unauthorized exception should be thrown") {
                exceptionThrown(UnauthorizedException::class.java)
            }
        }

        Scenario("Finding series by measurement as not QUALITY_CONTROL") {
            Given("User is not in role ${RoleNames.QUALITY_CONTROL}") {
                setUserIsNotQualityControl()
            }

            When("finding series by measurement") {
                findSeriesByTestMeasurement()
            }

            Then("Forbidden exception should be thrown") {
                exceptionThrown(ForbiddenException::class.java)
            }
        }

        Scenario("Finding series by measurement as QUALITY_CONTROL and measurement not exist") {
            Given("User is in role ${RoleNames.QUALITY_CONTROL}") {
                setUserIsQualityControl()
            }

            And("Measurement not exist") {
                setMeasurementNotExist(testMeasurementId)
            }

            When("finding series by measurement") {
                findSeriesByTestMeasurement()
            }

            Then("Should throw NotFoundException") {
                exceptionThrown(NotFoundException::class.java)
            }
        }

        Scenario("Finding series by measurement as QUALITY_CONTROL and measurement exists") {
            Given("User is in role ${RoleNames.QUALITY_CONTROL}") {
                setUserIsQualityControl()
            }

            val expectedMeasurement = mock<Measurement>()
            And("Measurement exists") {
                setMeasurementExist(testMeasurementId, expectedMeasurement)
            }

            val expectedSeries = mock<List<Series>>()
            And("Repository has series for found measurement") {
                seriesRepository.stub {
                    onBlocking { it.findAllByMeasurement(expectedMeasurement) } doReturn expectedSeries
                }
            }

            When("finding series by measurement") {
                findSeriesByTestMeasurement()
            }

            Then("Should return expected series for this measurement") {
                assertEquals(expectedSeries, result.orNull())
            }
        }
    }

    Feature("createSeries") {
        val testMeasurementId = Random().nextLong()
        lateinit var result: Validated<ApplicationException, Series>
        val seriesToCreate = Series(0, mock(), RandomStringUtils.random(10), listOf())
        fun createSeries() {
            result = runBlocking { service.createSeries(testMeasurementId, seriesToCreate) }
        }

        fun exceptionThrown(type: Class<out Throwable>) {
            assert(result.isInvalid)
            assertThat(result.swap().orNull(), instanceOf(type))
        }

        Scenario("Creating series as not authorized user") {
            Given("User not authorized") {
                setUserUnauthorized()
            }

            When("Creating series") {
                createSeries()
            }

            Then("Unauthorized exception should be thrown") {
                exceptionThrown(UnauthorizedException::class.java)
            }
        }

        Scenario("Creating series as authorized user who not is quality control") {
            Given("User is not quality control") {
                setUserIsNotQualityControl()
            }

            When("Creating series") {
                createSeries()
            }

            Then("Forbidden exception should be thrown") {
                exceptionThrown(ForbiddenException::class.java)
            }
        }

        Scenario("Creating series for not existing measurement as authorized user who is quality control") {
            Given("User is quality control") {
                setUserIsQualityControl()
            }

            And("Measurement not exist") {
                setMeasurementNotExist(testMeasurementId)
            }

            When("Creating series") {
                createSeries()
            }

            Then("NotFound exception should be thrown") {
                exceptionThrown(NotFoundException::class.java)
            }
        }

        Scenario("Creating series for existing measurement as authorized user who is quality control") {
            Given("User is quality control") {
                setUserIsQualityControl()
            }

            val expectedMeasurement = mock<Measurement>().stub { on { it.id } doReturn testMeasurementId }
            And("Measurement exists") {
                setMeasurementExist(testMeasurementId, expectedMeasurement)
            }

            val createdSeries = mock<Series>().stub {
                on { it.id } doReturn Random().nextLong()
            }
            And("Creation of series succeed") {
                seriesRepository.stub {
                    onBlocking {
                        save(seriesToCreate.copy(measurement = expectedMeasurement))
                    } doReturn createdSeries
                }
            }

            val expectedSeries = mock<Series>()
            And("series for created series exists") {
                seriesRepository.stub {
                    onBlocking { it.findById(createdSeries.id) } doReturn expectedSeries
                }
            }

            When("Creating series") {
                createSeries()
            }

            Then("created series should be returned") {
                assertEquals(expectedSeries, result.orNull())
            }
        }
    }
})