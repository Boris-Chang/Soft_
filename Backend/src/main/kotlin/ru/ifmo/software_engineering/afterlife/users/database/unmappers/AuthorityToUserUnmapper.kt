package ru.ifmo.software_engineering.afterlife.users.database.unmappers

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.database.tables.records.UsersRecord
import ru.ifmo.software_engineering.afterlife.users.domain.Authority

@Component
class AuthorityToUserUnmapper : RecordUnmapper<Authority, UsersRecord> {
    override fun unmap(authority: Authority): UsersRecord {
        val usersRecord = UsersRecord()
        usersRecord.username = authority.user.username
        usersRecord.passwordHash = authority.passwordHash

        return usersRecord
    }
}