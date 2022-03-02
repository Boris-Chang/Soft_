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
import ru.ifmo.software_engineering.afterlife.core.exceptions.ForbiddenException
import ru.ifmo.software_engineering.afterlife.core.exceptions.UnauthorizedException
import ru.ifmo.software_engineering.afterlife.core.models.PageRequest
import ru.ifmo.software_engineering.afterlife.core.models.PagedResult
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Measurement
import ru.ifmo.software_engineering.afterlife.quality_control.repositories.MeasurementsRepository
import ru.ifmo.software_engineering.afterlife.security.IdentityImpl
import ru.ifmo.software_engineering.afterlife.security.Principal
import ru.ifmo.software_engineering.afterlife.security.services.AuthorizationService
import ru.ifmo.software_engineering.afterlife.users.constants.RoleNames
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

object MeasurementsServiceTest : Spek({
    val measurementsRepository: MeasurementsRepository = mock()
    val authorizationService: AuthorizationService = mock()

    val service = MeasurementsServiceImpl(measurementsRepository, authorizationService)

    fun randomString() = RandomStringUtils.random(10)
    fun setUserUnauthorized() {
        authorizationService.stub {
            onBlocking { it.getCurrentUser() } doReturn null
        }
    }

    fun setUserAuthorizedWithRoles(roles: List<String>) {
        authorizationService.stub {
            onBlocking {
                it.getCurrentUser()
            } doReturn Principal(IdentityImpl(randomString(), randomString()), roles)
        }
    }

    Feature("findAll") {
        lateinit var result: Validated<Throwable, PagedResult<Measurement>>
        fun findAllMeasurements() {
            result = Validated.catch {
                runBlocking { service.findAll(PageRequest.default()) }
            }
        }


        fun assertExceptionThrown(type: Class<out Any>) {
            assert(result.isInvalid)
            assertThat(result.swap().orNull(), instanceOf(type))
        }

        Scenario("Find all measurement as not authorized user") {
            Given("User is unauthorized") {
                setUserUnauthorized()
            }

            When("finding all measurements") {
                findAllMeasurements()
            }

            Then("Throw unauthorized exception") {
                assertExceptionThrown(UnauthorizedException::class.java)
            }
        }

        listOf(
                listOf(RoleNames.GOD),
                listOf(RoleNames.DEVIL),
                listOf(RoleNames.ADMIN),
                listOf(RoleNames.HEAVEN_ADVOCATE),
                listOf(RoleNames.HEAVEN_PROSECUTOR),
                listOf(RoleNames.HEAVEN_PROSECUTOR, RoleNames.GOD, RoleNames.DEVIL, RoleNames.HEAVEN_ADVOCATE),
        ).forEach { forbiddenRoles ->
            Scenario("finding all measurements as user with roles $forbiddenRoles") {
                Given("User has roles $forbiddenRoles") {
                    setUserAuthorizedWithRoles(forbiddenRoles)
                }

                When("Finding all measurements") {
                    findAllMeasurements()
                }

                Then("ForbiddenException should be thrown") {
                    assertExceptionThrown(ForbiddenException::class.java)
                }
            }
        }
        listOf(
                listOf(RoleNames.QUALITY_CONTROL),
                listOf(RoleNames.HEAVEN_PROSECUTOR, RoleNames.GOD, RoleNames.DEVIL, RoleNames.HEAVEN_ADVOCATE,
                        RoleNames.QUALITY_CONTROL),
        ).forEach { allowedRoles ->
            Scenario("finding all measurements as user with roles $allowedRoles") {
                Given("User has roles $allowedRoles") {
                    setUserAuthorizedWithRoles(allowedRoles)
                }

                val expectedMeasurements = mock<PagedResult<Measurement>>()
                And("Repository has measurements") {
                    measurementsRepository.stub {
                        onBlocking {
                            it.findAll(PageRequest.default())
                        } doReturn expectedMeasurements
                    }
                }

                When("Finding all measurements") {
                    findAllMeasurements()
                }

                Then("Returned all measurements") {
                    assert(result.isValid)
                    assertEquals(expectedMeasurements, result.orNull())
                }
            }
        }
    }

    val testMeasurementId = Random().nextLong()
    fun setTestMeasurementNotExist() {
        measurementsRepository.stub {
            onBlocking { it.findById(testMeasurementId) } doReturn null
        }
    }

    fun setTestMeasurementExist(measurement: Measurement) {
        measurementsRepository.stub {
            onBlocking { it.findById(testMeasurementId) } doReturn measurement
        }
    }

    Feature("findMeasurementById") {
        lateinit var result: Validated<Throwable, Measurement?>
        fun findMeasurement() {
            result = Validated.catch {
                runBlocking { service.findMeasurementById(testMeasurementId) }
            }
        }

        fun assertExceptionThrown(type: Class<out Any>) {
            assert(result.isInvalid)
            assertThat(result.swap().orNull(), instanceOf(type))
        }

        Scenario("Find measurement as not authorized user") {
            Given("User is unauthorized") {
                setUserUnauthorized()
            }

            When("finding measurement") {
                findMeasurement()
            }

            Then("Throw unauthorized exception") {
                assertExceptionThrown(UnauthorizedException::class.java)
            }
        }

        listOf(
                listOf(RoleNames.GOD),
                listOf(RoleNames.DEVIL),
                listOf(RoleNames.ADMIN),
                listOf(RoleNames.HEAVEN_ADVOCATE),
                listOf(RoleNames.HEAVEN_PROSECUTOR),
                listOf(RoleNames.HEAVEN_PROSECUTOR, RoleNames.GOD, RoleNames.DEVIL, RoleNames.HEAVEN_ADVOCATE),
        ).forEach { forbiddenRoles ->
            Scenario("Finding measurement as user with roles $forbiddenRoles") {
                Given("User has roles $forbiddenRoles") {
                    setUserAuthorizedWithRoles(forbiddenRoles)
                }

                When("Finding measurement by id") {
                    findMeasurement()
                }

                Then("ForbiddenException should be thrown") {
                    assertExceptionThrown(ForbiddenException::class.java)
                }
            }
        }

        listOf(
                listOf(RoleNames.QUALITY_CONTROL),
                listOf(RoleNames.HEAVEN_PROSECUTOR, RoleNames.GOD, RoleNames.DEVIL, RoleNames.HEAVEN_ADVOCATE,
                        RoleNames.QUALITY_CONTROL),
        ).forEach { allowedRoles ->
            Scenario("finding measurements as user with roles $allowedRoles") {
                Given("User has roles $allowedRoles") {
                    setUserAuthorizedWithRoles(allowedRoles)
                }

                And("Test measurement does not exists") {
                    setTestMeasurementNotExist()
                }

                When("Finding all measurements") {
                    findMeasurement()
                }

                Then("Returned null") {
                    assert(result.isValid)
                    assertNull(result.orNull())
                }
            }

            Scenario("finding measurements as user with roles $allowedRoles") {
                Given("User has roles $allowedRoles") {
                    setUserAuthorizedWithRoles(allowedRoles)
                }

                val expectedMeasurement = mock<Measurement>()
                And("Test measurement exists") {
                    setTestMeasurementExist(expectedMeasurement)
                }

                When("Finding all measurements") {
                    findMeasurement()
                }

                Then("Returned all measurements") {
                    assert(result.isValid)
                    assertEquals(expectedMeasurement, result.orNull())
                }
            }
        }
    }

    Feature("createMeasurement") {
        lateinit var result: Validated<Throwable, Measurement>
        val measurementToCreate = mock<Measurement>()

        fun saveMeasurement() {
            result = Validated.catch {
                runBlocking { service.createMeasurement(measurementToCreate) }
            }
        }


        fun assertExceptionThrown(type: Class<out Any>) {
            assert(result.isInvalid)
            assertThat(result.swap().orNull(), instanceOf(type))
        }

        Scenario("Save measurement as not authorized user") {
            Given("User is unauthorized") {
                setUserUnauthorized()
            }

            When("saving measurement") {
                saveMeasurement()
            }

            Then("Throw unauthorized exception") {
                assertExceptionThrown(UnauthorizedException::class.java)
            }
        }

        listOf(
                listOf(RoleNames.GOD),
                listOf(RoleNames.DEVIL),
                listOf(RoleNames.ADMIN),
                listOf(RoleNames.HEAVEN_ADVOCATE),
                listOf(RoleNames.HEAVEN_PROSECUTOR),
                listOf(RoleNames.HEAVEN_PROSECUTOR, RoleNames.GOD, RoleNames.DEVIL, RoleNames.HEAVEN_ADVOCATE),
        ).forEach { forbiddenRoles ->
            Scenario("saving measurement as user with roles $forbiddenRoles") {
                Given("User has roles $forbiddenRoles") {
                    setUserAuthorizedWithRoles(forbiddenRoles)
                }

                When("Saving measurement") {
                    saveMeasurement()
                }

                Then("ForbiddenException should be thrown") {
                    assertExceptionThrown(ForbiddenException::class.java)
                }
            }
        }
        listOf(
                listOf(RoleNames.QUALITY_CONTROL),
                listOf(RoleNames.HEAVEN_PROSECUTOR, RoleNames.GOD, RoleNames.DEVIL, RoleNames.HEAVEN_ADVOCATE,
                        RoleNames.QUALITY_CONTROL),
        ).forEach { allowedRoles ->
            Scenario("Saving measurement as user with roles $allowedRoles") {
                Given("User has roles $allowedRoles") {
                    setUserAuthorizedWithRoles(allowedRoles)
                }

                val expectedMeasurement = mock<Measurement>()
                And("Repository save returns measurement") {
                    measurementsRepository.stub {
                        onBlocking {
                            it.save(measurementToCreate)
                        } doReturn expectedMeasurement
                    }
                }

                When("Saving measurement") {
                    saveMeasurement()
                }

                Then("Returned all measurements") {
                    assert(result.isValid)
                    assertEquals(expectedMeasurement, result.orNull())
                }
            }
        }
    }
})