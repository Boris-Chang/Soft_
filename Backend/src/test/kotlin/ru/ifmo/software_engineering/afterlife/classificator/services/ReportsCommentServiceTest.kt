package ru.ifmo.software_engineering.afterlife.classificator.services

import arrow.core.Validated
import arrow.core.orNull
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportComment
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.classificator.repositories.ReportCommentRepository
import ru.ifmo.software_engineering.afterlife.classificator.repositories.SoulRepository
import ru.ifmo.software_engineering.afterlife.core.exceptions.ApplicationException
import ru.ifmo.software_engineering.afterlife.core.exceptions.ForbiddenException
import ru.ifmo.software_engineering.afterlife.core.exceptions.NotFoundException
import ru.ifmo.software_engineering.afterlife.core.exceptions.UnauthorizedException
import ru.ifmo.software_engineering.afterlife.security.Principal
import ru.ifmo.software_engineering.afterlife.security.services.AuthorizationService
import ru.ifmo.software_engineering.afterlife.users.constants.RoleNames
import java.util.*
import kotlin.test.assertEquals

object ReportsCommentServiceTest : Spek({
    lateinit var authorizationService: AuthorizationService
    lateinit var soulRepository: SoulRepository
    lateinit var reportCommentRepository: ReportCommentRepository

    lateinit var service: ReportsCommentService

    val testSoulId = Random().nextLong()

    beforeEachGroup {
        authorizationService = mock()
        soulRepository = mock()
        reportCommentRepository = mock()

        service = ReportsCommentServiceImpl(authorizationService, soulRepository, reportCommentRepository)
    }

    fun setUserUnauthorized() {
        authorizationService.stub {
            onBlocking { it.getCurrentUser() } doReturn null
        }
    }

    fun setUserAuthorizedWithRoles(roles: List<String>) {
        authorizationService.stub {
            onBlocking { it.getCurrentUser() } doReturn Principal(mock(), roles)
        }
    }

    fun setSoulNotFound() {
        soulRepository.stub {
            onBlocking { it.findById(testSoulId) } doReturn null
        }
    }

    fun setSoulFound(soul: Soul) {
        soulRepository.stub {
            onBlocking { it.findById(testSoulId) } doReturn soul
        }
    }

    Feature("getCommentsBySoulId") {
        lateinit var result: Validated<ApplicationException, List<ReportComment>>

        fun getCommentsBySoulId() {
            result = runBlocking { service.getCommentsBySoulId(testSoulId) }
        }

        Scenario("User is not authorized") {
            Given("user is unauthorized") {
                setUserUnauthorized()
            }

            When("getting comment by soul id") {
                getCommentsBySoulId()
            }

            Then("Unauthorized exception should be thrown") {
                val exception = result.swap().orNull()
                assertThat(exception, instanceOf(UnauthorizedException::class.java))
            }
        }

        val forbiddenRoles = listOf(listOf(RoleNames.ADMIN), listOf())
        forbiddenRoles.forEach { roles ->
            Scenario("User is authorized but does not have required roles") {
                Given("user is authorized with roles $roles") {
                    setUserAuthorizedWithRoles(roles)
                }

                When("getting comment by soul id") {
                    getCommentsBySoulId()
                }

                Then("Forbidden exception should be thrown") {
                    val exception = result.swap().orNull()
                    assertThat(exception, instanceOf(ForbiddenException::class.java))
                }
            }
        }

        val allowedRoles = listOf(
                listOf(RoleNames.GOD),
                listOf(RoleNames.DEVIL),
                listOf(RoleNames.HEAVEN_PROSECUTOR),
                listOf(RoleNames.HEAVEN_ADVOCATE),
                listOf(RoleNames.GOD, RoleNames.DEVIL, RoleNames.HEAVEN_PROSECUTOR, RoleNames.HEAVEN_ADVOCATE),
                listOf(RoleNames.GOD, RoleNames.DEVIL, RoleNames.HEAVEN_PROSECUTOR, RoleNames.HEAVEN_ADVOCATE,
                        RoleNames.ADMIN)
        )

        allowedRoles.forEach { roles ->
            Scenario("User is authorized and has required roles $roles but soul not found") {
                Given("user is authorized with roles $roles") {
                    setUserAuthorizedWithRoles(roles)
                }

                And("Soul was not found") {
                    setSoulNotFound()
                }

                When("getting comment by soul id") {
                    getCommentsBySoulId()
                }

                Then("Not found exception should be thrown") {
                    val exception = result.swap().orNull()
                    assertThat(exception, instanceOf(NotFoundException::class.java))
                }
            }

            Scenario("User is authorized, has required roles $roles and soul exists") {
                Given("User is authorized with roles $roles") {
                    setUserAuthorizedWithRoles(roles)
                }
                val existingSoul: Soul = mock()

                And("Soul is exist") {
                    setSoulFound(existingSoul)
                }

                val expectedComments: List<ReportComment> = mock()
                And("Existing soul has some comments") {
                    reportCommentRepository.stub {
                        onBlocking { it.findCommentsBySoul(existingSoul) } doReturn expectedComments
                    }
                }

                When("getting comment by soul id") {
                    getCommentsBySoulId()
                }

                Then("Should return comments for this soul") {
                    assertEquals(expectedComments, result.orNull())
                }
            }
        }
    }

    Feature("postCommentForSoulById") {
        lateinit var result: Validated<ApplicationException, ReportComment>
        fun postCommentForTestSoul(comment: ReportComment) {
            result = runBlocking { service.postCommentForSoulById(testSoulId, comment) }
        }

        Scenario("user is not authorized") {
            Given("User is not authorized") {
                setUserUnauthorized()
            }

            When("Post comment for test soul") {
                postCommentForTestSoul(mock())
            }

            Then("Result should have UnauthorizedException") {
                assertThat(result.swap().orNull(), instanceOf(UnauthorizedException::class.java))
            }
        }

        val forbiddenRoles = listOf(
                listOf(RoleNames.ADMIN),
                listOf(RoleNames.DEVIL),
                listOf(RoleNames.GOD),
                listOf(RoleNames.GOD, RoleNames.ADMIN, RoleNames.GOD),
                listOf())
        forbiddenRoles.forEach { roles ->
            Scenario("User is authorized but does not have required roles") {
                Given("user is authorized with roles $roles") {
                    setUserAuthorizedWithRoles(roles)
                }

                When("Post comment for test soul") {
                    postCommentForTestSoul(mock())
                }

                Then("Forbidden exception should be thrown") {
                    val exception = result.swap().orNull()
                    assertThat(exception, instanceOf(ForbiddenException::class.java))
                }
            }
        }

        val allowedRoles = listOf(
                listOf(RoleNames.HEAVEN_PROSECUTOR),
                listOf(RoleNames.HEAVEN_ADVOCATE),
                listOf(RoleNames.HEAVEN_PROSECUTOR, RoleNames.HEAVEN_ADVOCATE),
                listOf(RoleNames.GOD, RoleNames.DEVIL, RoleNames.HEAVEN_PROSECUTOR, RoleNames.HEAVEN_ADVOCATE,
                        RoleNames.ADMIN)
        )

        allowedRoles.forEach { roles ->
            Scenario("User is authorized and has required roles $roles but soul not found") {
                Given("user is authorized with roles $roles") {
                    setUserAuthorizedWithRoles(roles)
                }

                And("Soul was not found") {
                    setSoulNotFound()
                }

                When("Post comment for test soul") {
                    postCommentForTestSoul(mock())
                }

                Then("Not found exception should be thrown") {
                    val exception = result.swap().orNull()
                    assertThat(exception, instanceOf(NotFoundException::class.java))
                }
            }

            Scenario("User is authorized, has required roles $roles and soul exists") {
                val commentToInsert: ReportComment = mock<ReportComment>().stub {
                    on{ it.withJustCreatedAt() } doReturn it
                }
                val existingSoul: Soul = mock()
                val expectedCreatedComment: ReportComment = mock()

                Given("User is authorized with roles $roles") {
                    setUserAuthorizedWithRoles(roles)
                }

                And("Soul is exist") {
                    setSoulFound(existingSoul)
                }

                And("Saving comment in repository returns saved comment") {
                    reportCommentRepository.stub {
                        onBlocking { it.save(existingSoul, commentToInsert) } doReturn expectedCreatedComment
                    }
                }

                When("Post comment for test soul") {
                    postCommentForTestSoul(commentToInsert)
                }

                Then("Should return save comment") {
                    assertEquals(expectedCreatedComment, result.orNull())
                }
            }
        }
    }
})