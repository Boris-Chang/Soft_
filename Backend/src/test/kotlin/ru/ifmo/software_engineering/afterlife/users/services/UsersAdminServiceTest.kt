package ru.ifmo.software_engineering.afterlife.users.services

import arrow.core.*
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import org.springframework.security.crypto.password.PasswordEncoder
import ru.ifmo.software_engineering.afterlife.core.exceptions.ConflictException
import ru.ifmo.software_engineering.afterlife.core.exceptions.ForbiddenException
import ru.ifmo.software_engineering.afterlife.core.exceptions.UnauthorizedException
import ru.ifmo.software_engineering.afterlife.security.IdentityImpl
import ru.ifmo.software_engineering.afterlife.security.Principal
import ru.ifmo.software_engineering.afterlife.security.services.AuthorizationService
import ru.ifmo.software_engineering.afterlife.security.toIdentity
import ru.ifmo.software_engineering.afterlife.users.domain.Authority
import ru.ifmo.software_engineering.afterlife.users.domain.CreateUserRequest
import ru.ifmo.software_engineering.afterlife.users.domain.User
import ru.ifmo.software_engineering.afterlife.users.repositories.AuthorityRepository
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UsersAdminServiceTest : Spek({
  Feature("Create user") {
      var result: Validated<Throwable, Principal>? = null

      val authorityRepository = mock<AuthorityRepository>()
      val authorizationService = mock<AuthorizationService>()
      val passwordEncoder = mock<PasswordEncoder>()
      val service = UserAdminServiceImpl(authorityRepository, authorizationService, passwordEncoder)

      val userToCreate = CreateUserRequest("user", "password", listOf("role"))
      Scenario("Current user is not authorized") {
          Given("current user is null") {
              authorizationService.stub {
                  onBlocking {
                      it.getCurrentUser()
                  }.doReturn (null)
              }
          }

          When("Creating user") {
              result = service.runCreateUser(userToCreate)
          }

          Then("Should throw unauthorized exception") {
              assertNotNull(result)
              assert(result!!.isInvalid)
              result!!.handleError {
                  assertThat(it, instanceOf(UnauthorizedException::class.java))
              }
          }
      }

      Scenario("Current user is not admin") {
          Given("current user is user without admin role") {
              authorizationService.stub {
                  onBlocking {
                      it.getCurrentUser()
                  }.doReturn (Principal(IdentityImpl("", ""), listOf("NOT_ADMIN")))
              }
          }

          When("Creating user") {
              result = service.runCreateUser(userToCreate)
          }

          Then("Should throw forbidden exception") {
              assertNotNull(result)
              assert(result!!.isInvalid)
              result!!.handleError {
                  assertThat(it, instanceOf(ForbiddenException::class.java))
              }
          }
      }

      Scenario("Current user is admin and user already exist") {
          Given("current user is user with admin role") {
              authorizationService.stub {
                  onBlocking {
                      it.getCurrentUser()
                  }.doReturn (Principal(IdentityImpl("", ""), listOf("ADMIN")))
              }
          }
          And("User with username already exist") {
              authorityRepository.stub {
                  onBlocking {
                      it.findUserByUsername(userToCreate.username)
                  }.doReturn (mock())
              }
          }

          When("Creating user") {
              result = service.runCreateUser(userToCreate)
          }

          Then("Should throw Conflict exception") {
              assertNotNull(result)
              assert(result!!.isInvalid)
              result!!.handleError {
                  assertThat(it, instanceOf(ConflictException::class.java))
              }
          }
      }

      Scenario("Current user is admin and user not exist") {
          Given("current user is user with admin role") {
              authorizationService.stub {
                  onBlocking {
                      it.getCurrentUser()
                  }.doReturn (Principal(IdentityImpl("", ""), listOf("ADMIN")))
              }
          }
          And("User with username not exist") {
              authorityRepository.stub {
                  onBlocking {
                      it.findUserByUsername(userToCreate.username)
                  } doReturn null
              }
          }
          val expectedHash = "TEST_HASH"
          And("Password was encoded to $expectedHash") {
              passwordEncoder.stub {
                  onBlocking { it.encode(userToCreate.password) } doReturn(expectedHash)
              }
          }

          val expectedAuthority = Authority(User(0, userToCreate.username), expectedHash, userToCreate.roles)
          And("Created user") {
              authorityRepository.stub {
                  onBlocking { it.createAuthority(expectedAuthority) } doReturn(expectedAuthority)
              }
          }

          When("Creating user") {
              result = service.runCreateUser(userToCreate)
          }

          Then("Should throw Conflict exception") {
              assertNotNull(result)
              assert(result!!.isValid)
              val createdUser = result!!.toOption().orNull()
              val expectedResult = Principal(expectedAuthority.toIdentity(), expectedAuthority.roles)
              assertEquals(createdUser, expectedResult)
          }
      }
  }
})

private fun UsersAdminService.runCreateUser(userToCreate: CreateUserRequest) = Validated.catch {
    val service = this
    runBlocking {
        service.createUser(userToCreate)
    }
}
