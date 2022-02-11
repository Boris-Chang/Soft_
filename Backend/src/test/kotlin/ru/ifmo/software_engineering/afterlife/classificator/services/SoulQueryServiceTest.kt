package ru.ifmo.software_engineering.afterlife.classificator.services

import arrow.core.Validated
import arrow.core.getOrElse
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.mockito.kotlin.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoul
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.classificator.repositories.SoulRepository
import ru.ifmo.software_engineering.afterlife.core.exceptions.ForbiddenException
import ru.ifmo.software_engineering.afterlife.core.exceptions.UnauthorizedException
import ru.ifmo.software_engineering.afterlife.core.models.PagedResult
import ru.ifmo.software_engineering.afterlife.security.IdentityImpl
import ru.ifmo.software_engineering.afterlife.security.Principal
import ru.ifmo.software_engineering.afterlife.security.services.AuthorizationService
import java.util.*
import kotlin.test.assertEquals

class SoulQueryServiceTest : Spek({
    val repository: SoulRepository = mock()
    val authorizationService: AuthorizationService = mock()

    val service = SoulsQueryServiceImpl(repository, authorizationService)

    fun currentUserIsNull() {
        authorizationService.stub {
            onBlocking { getCurrentUser() } doReturn null
        }
    }

    fun currentUserHasRole(role: String) {
        val user = Principal(IdentityImpl("test", "test"), listOf(role))
        authorizationService.stub {
            onBlocking { getCurrentUser() } doReturn user
        }
    }

    Feature("SoulsQueryService.getAllReportedSouls") {
        val allSouls = mock<PagedResult<ReportedSoul>>()
        fun repositoryGetAllSoulsReturnValidResult() {
            repository.stub {
                onBlocking { getReportedSouls(anyOrNull(), anyOrNull()) } doReturn allSouls
            }
        }

        var result: Validated<Throwable, PagedResult<ReportedSoul>>? = null
        fun getAllReportedSouls() {
            result = Validated.catch {
                runBlocking { service.getAllReportedSouls() }
            }
        }

        Scenario("User is not authorized") {
            Given("Current user is null") {
                currentUserIsNull()
            }

            When("get all reported souls") {
                getAllReportedSouls()
            }

            Then("UnauthorizedException should thrown") {
                assert(result!!.isInvalid)
                val err = result!!.swap().getOrElse { null }
                assertThat(err, instanceOf(UnauthorizedException::class.java))
            }
        }

        Scenario("User role is not allowed to view soul") {
            Given("Current user has role NOT_ALLOWED") {
                currentUserHasRole("NOT_ALLOWED")
            }

            When("get all reported souls") {
                getAllReportedSouls()
            }

            Then("UnauthorizedException should thrown") {
                assert(result!!.isInvalid)
                val err = result!!.swap().getOrElse { null }
                assertThat(err, instanceOf(ForbiddenException::class.java))
            }
        }

        listOf("GOD", "DEVIL", "HEAVEN_ADVOCATE", "HEAVEN_PROSECUTOR").forEach {
            Scenario("User role has role $it") {
                Given("Current user has role $it") {
                    currentUserHasRole(it)
                }

                And("Repository returns all souls") {
                    repositoryGetAllSoulsReturnValidResult()
                }

                When("get all reported souls") {
                    getAllReportedSouls()
                }

                Then("UnauthorizedException should thrown") {
                    assert(result!!.isValid)
                    val success = result!!.getOrElse { null }
                    assertEquals(allSouls, success)
                }
            }
        }
    }

    Feature("SoulsQueryService.getSoulById") {
        val soulId = Random().nextLong()
        val validSoul = mock<Soul>()

        fun repositoryGetSoulByIdReturnValidResult() {
            repository.stub {
                onBlocking { findById(soulId) } doReturn validSoul
            }
        }

        var result: Validated<Throwable, Soul?>? = null
        fun getSoulById() {
            result = Validated.catch {
                runBlocking { service.getSoulById(soulId) }
            }
        }

        Scenario("User is not authorized") {
            Given("Current user is null") {
                currentUserIsNull()
            }

            When("get all reported souls") {
                getSoulById()
            }

            Then("UnauthorizedException should thrown") {
                assert(result!!.isInvalid)
                val err = result!!.swap().getOrElse { null }
                assertThat(err, instanceOf(UnauthorizedException::class.java))
            }
        }

        Scenario("User role is not allowed to view soul") {
            Given("Current user has role NOT_ALLOWED") {
                currentUserHasRole("NOT_ALLOWED")
            }

            When("get all reported souls") {
                getSoulById()
            }

            Then("UnauthorizedException should thrown") {
                assert(result!!.isInvalid)
                val err = result!!.swap().getOrElse { null }
                assertThat(err, instanceOf(ForbiddenException::class.java))
            }
        }

        listOf("GOD", "DEVIL", "HEAVEN_ADVOCATE", "HEAVEN_PROSECUTOR").forEach {
            Scenario("User role has role $it") {
                Given("Current user has role $it") {
                    currentUserHasRole(it)
                }

                And("Repository returns all souls") {
                    repositoryGetSoulByIdReturnValidResult()
                }

                When("get all reported souls") {
                    getSoulById()
                }

                Then("UnauthorizedException should thrown") {
                    assert(result!!.isValid)
                    val success = result!!.getOrElse { null }
                    assertEquals(validSoul, success)
                }
            }
        }
    }
})