package ru.ifmo.software_engineering.afterlife.users.services

import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import ru.ifmo.software_engineering.afterlife.core.exceptions.UnauthorizedException
import ru.ifmo.software_engineering.afterlife.security.IdentityImpl
import ru.ifmo.software_engineering.afterlife.security.Principal
import ru.ifmo.software_engineering.afterlife.security.services.AuthorizationService
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class AuthorizedUserServiceTest: Spek({
  Feature("Get Current user")  {
      val authorizationService = mock<AuthorizationService>()
      val service = AuthorizedUserServiceImpl(authorizationService)

      Scenario("User is not authorized") {
          Given("No authorized user was found") {
              authorizationService.stub { on {
                  runBlocking { it.getCurrentUser() }
              }.doReturn(null) }
          }

          var exception: Exception? = null
          When("Getting current user") {

              try {
                  runBlocking {
                      service.getCurrentUser()
                  }
              } catch (e: Exception) {
                  exception = e
              }
          }

          Then("Should throw unauthorized exception") {
              assertNotNull(exception)
              assert(exception is UnauthorizedException)
          }
      }

      Scenario("User authorized") {
          val expectedUser = Principal(IdentityImpl("id", "username"), listOf("roles"))

          Given("Authorized user was found") {
              authorizationService.stub { on {
                  runBlocking { it.getCurrentUser() }
              }.doReturn(expectedUser) }
          }

          var actualUser: Principal? = null
          When("Getting current user") {
              actualUser = runBlocking {
                  service.getCurrentUser()
              }
          }

          Then("Should return authorized user"){
              assertSame(expectedUser, actualUser)
          }
      }
  }
})