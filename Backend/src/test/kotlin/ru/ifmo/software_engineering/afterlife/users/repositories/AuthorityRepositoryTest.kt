package ru.ifmo.software_engineering.afterlife.users.repositories

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import ru.ifmo.software_engineering.afterlife.users.domain.Authority
import ru.ifmo.software_engineering.afterlife.users.domain.User
import ru.ifmo.software_engineering.afterlife.utils.IntegrationTest
import kotlin.test.*

private const val TEST_USERNAME = "test_user"
private const val TEST_PASSWORD_HASH = "test_password_hash"

@IntegrationTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthorityRepositoryTest {
    @Autowired
    lateinit var dsl: DSLContext

    @Autowired
    lateinit var userRepository: AuthorityRepository

    @BeforeAll
    fun before() {
        val userId = dsl.fetch("INSERT INTO Users(username, password_hash) VALUES ('$TEST_USERNAME', '$TEST_PASSWORD_HASH') " +
                "returning ID as id")
                .map { it["id"].toString().toLong() }
                .first()
        dsl.execute("INSERT INTO USER_ROLES(user_id, role_id) VALUES (${userId}, 1), (${userId}, 2)")
    }

    @AfterAll
    fun after() {
        dsl.execute("DELETE FROM USER_ROLES")
        dsl.execute("DELETE FROM USERS")
    }

    @Test
    fun findUserByUsername_userExist_returnUserWithHisRoles(): Unit = runBlocking {
        //Act
        val user = userRepository.findUserByUsername(TEST_USERNAME)

        //Assert
        assertNotNull(user)
        assertEquals(TEST_USERNAME, user.user.username)
        assertEquals(TEST_PASSWORD_HASH, user.passwordHash)
        assertEquals(2, user.roles.size)
        assertThat(user.roles).contains("GOD", "ADMIN")
    }

    @Test
    fun findUserByUsername_userNotExist_returnsNull() = runBlocking {
        //Act
        val user = userRepository.findUserByUsername("NOT_EXIST")

        //Assert
        assertNull(user)
    }

    @Test
    fun findUserByUsername_userWithoutRolesExist_returnsUserEmptyRoles() = runBlocking {
        //Arrange
        val username = "no_roles_user"
        dsl.execute("INSERT INTO Users(username, password_hash) VALUES ('$username', '$TEST_PASSWORD_HASH') ")

        //Act
        val user = userRepository.findUserByUsername(username)

        //Assert
        assertNotNull(user)
        assertEquals(username, user.user.username)
        assertEquals(TEST_PASSWORD_HASH, user.passwordHash)
        assertThat(user.roles).isEmpty()
    }

    @Test
    fun createAuthority_authorityHasRoles_returnCreatedUsersWithRoles() = runBlocking {
        val authorityToCreate = Authority(
                User(0, "test_username"),
                "password_hash",
                listOf("GOD", "ADMIN"))

        val createdAuthority = userRepository.createAuthority(authorityToCreate)

        assertNotNull(createdAuthority)
        assertContentEquals(authorityToCreate.roles.sorted(), createdAuthority.roles.sorted())
        val expectedAuthority = authorityToCreate.copy(
                user = authorityToCreate.user.copy(id = createdAuthority.id),
                roles =createdAuthority.roles)
        assertEquals(expectedAuthority, createdAuthority)
        assert(createdAuthority.id != 0.toLong())
    }
}