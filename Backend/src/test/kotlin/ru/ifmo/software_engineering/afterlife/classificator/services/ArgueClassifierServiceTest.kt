package ru.ifmo.software_engineering.afterlife.classificator.services

import arrow.core.Validated
import arrow.core.getOrElse
import kotlinx.coroutines.runBlocking
import org.apache.commons.lang3.RandomStringUtils
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.mockito.kotlin.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import ru.ifmo.software_engineering.afterlife.classificator.domain.AfterworldSection
import ru.ifmo.software_engineering.afterlife.classificator.domain.ArguedClassifierDecision
import ru.ifmo.software_engineering.afterlife.classificator.domain.HellCircle
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.classificator.repositories.ArguedClassifierDecisionRepository
import ru.ifmo.software_engineering.afterlife.classificator.repositories.SoulRepository
import ru.ifmo.software_engineering.afterlife.core.exceptions.ConflictException
import ru.ifmo.software_engineering.afterlife.core.exceptions.ForbiddenException
import ru.ifmo.software_engineering.afterlife.core.exceptions.NotFoundException
import ru.ifmo.software_engineering.afterlife.core.exceptions.UnauthorizedException
import ru.ifmo.software_engineering.afterlife.security.IdentityImpl
import ru.ifmo.software_engineering.afterlife.security.Principal
import ru.ifmo.software_engineering.afterlife.security.services.AuthorizationService
import ru.ifmo.software_engineering.afterlife.users.constants.RoleNames
import java.util.*
import kotlin.test.assertEquals

object ArgueClassifierServiceTest : Spek({
    lateinit var authorizationService: AuthorizationService
    lateinit var soulsRepository: SoulRepository
    lateinit var arguedClassifierDecisionRepository: ArguedClassifierDecisionRepository

    lateinit var service: ArgueClassifierService

    beforeEachGroup {
        authorizationService = mock()
        soulsRepository = mock()
        arguedClassifierDecisionRepository = mock()

        service = ArgueClassifierServiceImpl(authorizationService, soulsRepository, arguedClassifierDecisionRepository)
    }

    fun setUnauthorized() = authorizationService.stub {
        onBlocking { it.getCurrentUser() } doReturn null
    }

    fun setAuthorizedWithRoles(roles: List<String>) = authorizationService.stub {
        val userToReturn = Principal(IdentityImpl(RandomStringUtils.random(10), RandomStringUtils.random(10)), roles)
        onBlocking {
            it.getCurrentUser()
        } doReturn(userToReturn)
    }

    val testSoulId = Random().nextLong()

    fun soulWithNoSessionClassifiedExists() {
        val soul = mock<Soul>()
        soulsRepository.stub {
            onBlocking { it.findById(testSoulId) } doReturn soul
        }
    }

    Feature("markClassifierDecisionArgued") {
        lateinit var result: Validated<Throwable, ArguedClassifierDecision>
        fun markClassifierDecisionArgued() = runBlocking {
            result = Validated.catch { service.markClassifierDecisionArgued(testSoulId) }
        }

        fun soulWithSessionClassifiedExists(): Soul {
            val soul = mock<Soul>()
            soul.stub {
                on {it.classifiedAfterworldSection} doReturn HellCircle(1)
            }
            soulsRepository.stub {
                onBlocking { it.findById(testSoulId) } doReturn soul
            }
            return soul
        }

        Scenario("User is unauthorized") {
            Given("user is not authorized") {
                setUnauthorized()
            }

            When("Marking classifier Decision as argued") {
                markClassifierDecisionArgued()
            }

            Then("UnauthorizedException should be thrown") {
                val exception = result.swap().getOrElse { null }
                assertThat(exception, instanceOf(UnauthorizedException::class.java))
            }
        }

        val forbiddenRolesTestData = listOf(
                listOf(RoleNames.HEAVEN_ADVOCATE),
                listOf(RoleNames.HEAVEN_PROSECUTOR),
                listOf(RoleNames.ADMIN),
                listOf(RoleNames.ADMIN, RoleNames.HEAVEN_ADVOCATE, RoleNames.HEAVEN_PROSECUTOR),
                listOf(RoleNames.HEAVEN_ADVOCATE, RoleNames.HEAVEN_PROSECUTOR),
                listOf(RoleNames.ADMIN, RoleNames.HEAVEN_PROSECUTOR),
                listOf(RoleNames.ADMIN, RoleNames.HEAVEN_ADVOCATE))
        forbiddenRolesTestData.forEach { roles ->
            Scenario("User has no rights to user") {
                Given("User has roles $roles") {
                    setAuthorizedWithRoles(roles)
                }

                When("Marking classifier Decision as argued") {
                    markClassifierDecisionArgued()
                }

                Then("${ForbiddenException::class.simpleName} should be thrown") {
                    val exception = result.swap().getOrElse { null }
                    assertThat(exception, instanceOf(ForbiddenException::class.java))
                }
            }
        }

        Scenario("User is GOD but soul not found") {
            Given("User is ${RoleNames.GOD}") {
                setAuthorizedWithRoles(listOf(RoleNames.GOD))
            }

            When("Marking classifier Decision as argued") {
                markClassifierDecisionArgued()
            }

            Then("${NotFoundException::class.simpleName} thrown") {
                val exception = result.swap().getOrElse { null }
                assertThat(exception, instanceOf(NotFoundException::class.java))
            }
        }

        Scenario("User is GOD Soul found but classifier did not made decision") {
            Given("User is ${RoleNames.GOD}") {
                setAuthorizedWithRoles(listOf(RoleNames.GOD))
            }

            And("soul was classified") {
                soulWithNoSessionClassifiedExists()
            }

            When("Marking classifier Decision as argued") {
                markClassifierDecisionArgued()
            }

            Then("${ConflictException::class.simpleName} thrown") {
                val exception = result.swap().getOrElse { null }
                assertThat(exception, instanceOf(ConflictException::class.java))
            }
        }

        Scenario("User is GOD Soul found but classifier made decision") {
            Given("User is ${RoleNames.GOD}") {
                setAuthorizedWithRoles(listOf(RoleNames.GOD))
            }

            lateinit var soul: Soul
            And("soul was classified") {
                soul = soulWithSessionClassifiedExists()
            }

            val soulArgue = mock<ArguedClassifierDecision>()
            And("Argue repository returns result") {
                arguedClassifierDecisionRepository.stub { onBlocking { it.findArgueForSoul(soul) } doReturn soulArgue }
            }

            val updatedArgue = mock<ArguedClassifierDecision>()
            And("Result updated for god") {
                soulArgue.stub { on { it.asArguedByGod() } doReturn updatedArgue }
            }

            And("Result saved for god") {
                arguedClassifierDecisionRepository.stub {
                    onBlocking { it.updateSoulArgue(soul, updatedArgue) } doReturn updatedArgue
                }
            }

            When("Marking classifier Decision as argued") {
                markClassifierDecisionArgued()
            }

            Then("Result returned updated argue") {
                val actualResult = result.getOrElse { null }
                assertEquals(updatedArgue, actualResult)
            }

            And("Result was updated in repository") {
                verifyBlocking(arguedClassifierDecisionRepository) {
                    updateSoulArgue(soul, updatedArgue)
                }
            }
        }

        Scenario("User is DEVIL Soul found but classifier made decision") {
            Given("User is ${RoleNames.DEVIL}") {
                setAuthorizedWithRoles(listOf(RoleNames.DEVIL))
            }

            lateinit var soul: Soul
            And("soul was classified") {
                soul = soulWithSessionClassifiedExists()
            }

            val soulArgue = mock<ArguedClassifierDecision>()
            And("Argue repository returns result") {
                arguedClassifierDecisionRepository.stub { onBlocking { it.findArgueForSoul(soul) } doReturn soulArgue }
            }

            val updatedArgue = mock<ArguedClassifierDecision>()
            And("Result updated for devil") {
                soulArgue.stub { on { it.asArguedByDevil() } doReturn updatedArgue }
            }

            And("Result saved for devil") {
                arguedClassifierDecisionRepository.stub {
                    onBlocking { it.updateSoulArgue(soul, updatedArgue) } doReturn updatedArgue
                }
            }

            When("Marking classifier Decision as argued") {
                markClassifierDecisionArgued()
            }

            Then("Result returned updated argue") {
                val actualResult = result.getOrElse { null }
                assertEquals(updatedArgue, actualResult)
            }

            And("Result was updated in repository") {
                verifyBlocking(arguedClassifierDecisionRepository) {
                    updateSoulArgue(soul, updatedArgue)
                }
            }
        }
    }

    Feature("changeClassifierDecision") {
        val sectionToChange = mock<AfterworldSection>()

        lateinit var result: Validated<Throwable, ArguedClassifierDecision>
        fun changeClassifierDecisionArgued() = runBlocking {
            result = Validated.catch { service.changeClassifierDecision(testSoulId, sectionToChange) }
        }

        fun changeClassifierDecisionArgued(section: AfterworldSection) = runBlocking {
            result = Validated.catch { service.changeClassifierDecision(testSoulId, section) }
        }

        fun soulWithSessionClassifiedExists(): Soul {
            val soul = mock<Soul>()
            soul.stub {
                on {it.classifiedAfterworldSection} doReturn HellCircle(1)
            }
            soulsRepository.stub {
                onBlocking { it.findById(testSoulId) } doReturn soul
            }
            return soul
        }

        Scenario("User is unauthorized") {
            Given("user is not authorized") {
                setUnauthorized()
            }

            When("Marking classifier Decision as argued") {
                changeClassifierDecisionArgued()
            }

            Then("UnauthorizedException should be thrown") {
                val exception = result.swap().getOrElse { null }
                assertThat(exception, instanceOf(UnauthorizedException::class.java))
            }
        }

        val forbiddenRolesTestData = listOf(
                listOf(RoleNames.HEAVEN_ADVOCATE),
                listOf(RoleNames.HEAVEN_PROSECUTOR),
                listOf(RoleNames.ADMIN),
                listOf(RoleNames.ADMIN, RoleNames.HEAVEN_ADVOCATE, RoleNames.HEAVEN_PROSECUTOR),
                listOf(RoleNames.HEAVEN_ADVOCATE, RoleNames.HEAVEN_PROSECUTOR),
                listOf(RoleNames.ADMIN, RoleNames.HEAVEN_PROSECUTOR),
                listOf(RoleNames.ADMIN, RoleNames.HEAVEN_ADVOCATE))
        forbiddenRolesTestData.forEach { roles ->
            Scenario("User has no rights to user") {
                Given("User has roles $roles") {
                    setAuthorizedWithRoles(roles)
                }

                When("Marking classifier Decision as argued") {
                    changeClassifierDecisionArgued()
                }

                Then("${ForbiddenException::class.simpleName} should be thrown") {
                    val exception = result.swap().getOrElse { null }
                    assertThat(exception, instanceOf(ForbiddenException::class.java))
                }
            }
        }

        Scenario("User is GOD but soul not found") {
            Given("User is ${RoleNames.GOD}") {
                setAuthorizedWithRoles(listOf(RoleNames.GOD))
            }

            When("Marking classifier Decision as argued") {
                changeClassifierDecisionArgued()
            }

            Then("${NotFoundException::class.simpleName} thrown") {
                val exception = result.swap().getOrElse { null }
                assertThat(exception, instanceOf(NotFoundException::class.java))
            }
        }

        Scenario("User is GOD Soul found but classifier did not made decision") {
            Given("User is ${RoleNames.GOD}") {
                setAuthorizedWithRoles(listOf(RoleNames.GOD))
            }

            And("soul was classified") {
                soulWithNoSessionClassifiedExists()
            }

            When("Marking classifier Decision as argued") {
                changeClassifierDecisionArgued()
            }

            Then("${ConflictException::class.simpleName} thrown") {
                val exception = result.swap().getOrElse { null }
                assertThat(exception, instanceOf(ConflictException::class.java))
            }
        }

        Scenario("User is GOD, Soul found but classifier decision same as passed to change") {
            Given("User is ${RoleNames.GOD}") {
                setAuthorizedWithRoles(listOf(RoleNames.GOD))
            }

            lateinit var soul: Soul
            And("soul was classified") {
                soul = soulWithSessionClassifiedExists()
            }

            val soulArgue = mock<ArguedClassifierDecision>()
            And("Argue repository returns result") {
                arguedClassifierDecisionRepository.stub { onBlocking { it.findArgueForSoul(soul) } doReturn soulArgue }
            }

            When("Marking classifier Decision as argued") {
                changeClassifierDecisionArgued(soul.classifiedAfterworldSection!!)
            }

            Then("Returns existing soul argue") {
                val actualArgue = result.getOrElse { null }
                assertEquals(soulArgue, actualArgue)
            }

            And("updating argue was not called") {
                verifyBlocking(arguedClassifierDecisionRepository, never()) {
                    updateSoulArgue(any(), any())
                }
            }
        }

        Scenario("User is GOD Soul found but classifier made decision") {
            Given("User is ${RoleNames.GOD}") {
                setAuthorizedWithRoles(listOf(RoleNames.GOD))
            }

            lateinit var soul: Soul
            And("soul was classified") {
                soul = soulWithSessionClassifiedExists()
            }

            val soulArgue = mock<ArguedClassifierDecision>()
            And("Argue repository returns result") {
                arguedClassifierDecisionRepository.stub { onBlocking { it.findArgueForSoul(soul) } doReturn soulArgue }
            }

            val updatedArgue = mock<ArguedClassifierDecision>()
            And("Result updated for god") {
                soulArgue.stub { on { it.asChangedDecisionByGod(sectionToChange) } doReturn updatedArgue }
            }

            And("Result saved for god") {
                arguedClassifierDecisionRepository.stub {
                    onBlocking { it.updateSoulArgue(soul, updatedArgue) } doReturn updatedArgue
                }
            }

            When("Marking classifier Decision as argued") {
                changeClassifierDecisionArgued()
            }

            Then("Result returned updated argue") {
                val actualResult = result.getOrElse { null }
                assertEquals(updatedArgue, actualResult)
            }

            And("Result was updated in repository") {
                verifyBlocking(arguedClassifierDecisionRepository) {
                    updateSoulArgue(soul, updatedArgue)
                }
            }
        }

        Scenario("User is DEVIL Soul found but classifier made decision") {
            Given("User is ${RoleNames.DEVIL}") {
                setAuthorizedWithRoles(listOf(RoleNames.DEVIL))
            }

            lateinit var soul: Soul
            And("soul was classified") {
                soul = soulWithSessionClassifiedExists()
            }

            val soulArgue = mock<ArguedClassifierDecision>()
            And("Argue repository returns result") {
                arguedClassifierDecisionRepository.stub { onBlocking { it.findArgueForSoul(soul) } doReturn soulArgue }
            }

            val updatedArgue = mock<ArguedClassifierDecision>()
            And("Result updated for devil") {
                soulArgue.stub { on { it.asChangedDecisionByDevil(sectionToChange) } doReturn updatedArgue }
            }

            And("Result saved for devil") {
                arguedClassifierDecisionRepository.stub {
                    onBlocking { it.updateSoulArgue(soul, updatedArgue) } doReturn updatedArgue
                }
            }

            When("Marking classifier Decision as argued") {
                changeClassifierDecisionArgued()
            }

            Then("Result returned updated argue") {
                val actualResult = result.getOrElse { null }
                assertEquals(updatedArgue, actualResult)
            }

            And("Result was updated in repository") {
                verifyBlocking(arguedClassifierDecisionRepository) {
                    updateSoulArgue(soul, updatedArgue)
                }
            }
        }
    }

    Feature("getSoulArgue") {
        lateinit var result: Validated<Throwable, ArguedClassifierDecision>
        fun getSoulArgue() = runBlocking {
            result = Validated.catch { service.getSoulArgue(testSoulId) }
        }

        fun soulWithSessionClassifiedExists(): Soul {
            val soul = mock<Soul>()
            soul.stub {
                on {it.classifiedAfterworldSection} doReturn HellCircle(1)
            }
            soulsRepository.stub {
                onBlocking { it.findById(testSoulId) } doReturn soul
            }
            return soul
        }

        Scenario("User is unauthorized") {
            Given("user is not authorized") {
                setUnauthorized()
            }

            When("Marking classifier Decision as argued") {
                getSoulArgue()
            }

            Then("UnauthorizedException should be thrown") {
                val exception = result.swap().getOrElse { null }
                assertThat(exception, instanceOf(UnauthorizedException::class.java))
            }
        }

        Scenario("User has no rights to get roles") {
            val roles = listOf(RoleNames.ADMIN)
            Given("User has roles $roles") {
                setAuthorizedWithRoles(roles)
            }

            When("getting soul argue") {
                getSoulArgue()
            }

            Then("${ForbiddenException::class.simpleName} should be thrown") {
                val exception = result.swap().getOrElse { null }
                assertThat(exception, instanceOf(ForbiddenException::class.java))
            }
        }

        val allowedRolesTestData = listOf(
                listOf(RoleNames.HEAVEN_ADVOCATE),
                listOf(RoleNames.HEAVEN_PROSECUTOR),
                listOf(RoleNames.HEAVEN_PROSECUTOR, RoleNames.HEAVEN_ADVOCATE),
                listOf(RoleNames.ADMIN, RoleNames.HEAVEN_ADVOCATE, RoleNames.HEAVEN_PROSECUTOR))
        allowedRolesTestData.forEach { roles ->
            Scenario("User has roles $roles and soul was found") {
                Given("User has roles $roles") {
                    setAuthorizedWithRoles(roles)
                }

                lateinit var soul: Soul
                And("soul was classified") {
                    soul = soulWithSessionClassifiedExists()
                }

                val expectedArgue = mock<ArguedClassifierDecision>()
                And("repository Returns soul argue") {
                    arguedClassifierDecisionRepository.stub {
                        onBlocking { it.findArgueForSoul(soul) } doReturn expectedArgue
                    }
                }

                When("Marking classifier Decision as argued") {
                    getSoulArgue()
                }

                Then("${ForbiddenException::class.simpleName} should be thrown") {
                    val actualArgue = result.getOrElse { null }
                    assertEquals(expectedArgue, actualArgue)
                }
            }
        }
    }
})