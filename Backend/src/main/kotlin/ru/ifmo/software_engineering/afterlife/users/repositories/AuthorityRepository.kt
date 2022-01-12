package ru.ifmo.software_engineering.afterlife.users.repositories

import kotlinx.coroutines.future.await
import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.jooq.RecordUnmapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.software_engineering.afterlife.database.Tables.*
import ru.ifmo.software_engineering.afterlife.database.tables.records.UserRolesRecord
import ru.ifmo.software_engineering.afterlife.database.tables.records.UsersRecord
import ru.ifmo.software_engineering.afterlife.users.domain.Authority

interface AuthorityRepository {
    suspend fun findUserByUsername(username: String): Authority?
    @Transactional
    suspend fun createAuthority(authority: Authority): Authority
}

@Repository
class AuthorityRepositoryImpl(
        private val dsl: DSLContext,
        private val authorizedUserMapper: RecordMapper<UsersRecord, Authority>,
        private val userUnmapper: RecordUnmapper<Authority, UsersRecord>
) : AuthorityRepository {
    override suspend fun findUserByUsername(username: String): Authority? {
        return dsl.select().from(USERS)
                .leftJoin(USER_ROLES)
                .on(USERS.ID.eq(USER_ROLES.USER_ID))
                .leftJoin(ROLES)
                .on(ROLES.ID.eq(USER_ROLES.ROLE_ID))
                .where(USERS.USERNAME.eq(username))
                .fetchAsync()
                .await()
                .intoGroups { authorizedUserMapper.map(it.into(USERS)) }
                .map {
                    it.key.copy(roles = it.value
                            .map { record -> record[ROLES.ROLE_NAME] }
                            .filterNotNull())
                }
                .firstOrNull()
    }

    @Transactional
    override suspend fun createAuthority(authority: Authority): Authority {
        val userId = dsl
                .insertInto(USERS)
                .set(userUnmapper.unmap(authority))
                .returning()
                .fetchAsync()
                .await()
                .map { it.id }
                .first()

        val userRoles = dsl.select()
                .from(ROLES)
                .where(ROLES.ROLE_NAME.`in`(authority.roles))
                .fetchAsync()
                .await()
                .map { it[ROLES.ID] }
                .map { UserRolesRecord(userId, it) }

        dsl.batchInsert(userRoles).executeAsync().await()

        return this.findUserByUsername(authority.user.username)!!
    }
}