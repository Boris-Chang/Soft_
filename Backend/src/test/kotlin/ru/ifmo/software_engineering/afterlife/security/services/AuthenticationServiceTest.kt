package ru.ifmo.software_engineering.afterlife.security.services

import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import ru.ifmo.software_engineering.afterlife.security.utils.JwtTokenUtil
import ru.ifmo.software_engineering.afterlife.users.domain.Authority
import ru.ifmo.software_engineering.afterlife.users.domain.User
import ru.ifmo.software_engineering.afterlife.users.repositories.AuthorityRepository
import kotlin.test.assertEquals
import kotlin.test.assertNull


private const val TEST_USERNAME = "test_username"
private const val TEST_PASSWORD = "test_password"
private const val TEST_TOKEN = "test_token"

object AuthenticationServiceTest : Spek({
    Feature("Get Authentication token for credentials $TEST_USERNAME:$TEST_PASSWORD") {
        val passwordEncoder = BCryptPasswordEncoder()
        val authorityRepository = mock<AuthorityRepository>()
        val jwtTokenUtil = mock<JwtTokenUtil>()
        jwtTokenUtil.stub {
            on { generateAccessToken(any())}.doReturn(TEST_TOKEN)
        }

        val service = AuthenticationServiceImpl(passwordEncoder, authorityRepository, jwtTokenUtil)
        var returnedResult: String? = null

        Scenario("authority with login $TEST_USERNAME not found") {
            Given("authority with login $TEST_USERNAME is not exist") {
                authorityRepository.stub {
                    on { runBlocking { findUserByUsername(TEST_USERNAME) } }
                            .doReturn(null)
                }
            }

            When("getting token") {
                returnedResult = runBlocking { service.authenticationToken(TEST_USERNAME, TEST_PASSWORD) }
            }

            Then("should return null") {
                assertNull(returnedResult)
            }
        }

        Scenario("authority with login $TEST_USERNAME found but has password not matching $TEST_PASSWORD") {
            val expectedAuthority = Authority(User(0, TEST_USERNAME), "another one", emptyList())

            Given("authority with login $TEST_USERNAME is exist") {
                authorityRepository.stub {
                    on { runBlocking { findUserByUsername(TEST_USERNAME) } }
                            .doReturn(expectedAuthority)
                }
            }

            When("getting token") {
                returnedResult = runBlocking { service.authenticationToken(TEST_USERNAME, TEST_PASSWORD) }
            }

            Then("should return null") {
                assertNull(returnedResult)
            }
        }

        Scenario("authority with login $TEST_USERNAME and password matches $TEST_PASSWORD") {
            val expectedAuthority = Authority(User(0, TEST_USERNAME), passwordEncoder.encode(TEST_PASSWORD), emptyList())

            Given("authority with login $TEST_USERNAME is exist and password is $TEST_PASSWORD") {
                authorityRepository.stub {
                    on { runBlocking { findUserByUsername(TEST_USERNAME) } }
                            .doReturn(expectedAuthority)
                }
            }

            When("getting token") {
                returnedResult = runBlocking { service.authenticationToken(TEST_USERNAME, TEST_PASSWORD) }
            }

            Then("should return token") {
                assertEquals(TEST_TOKEN, returnedResult)
            }
        }
    }
})