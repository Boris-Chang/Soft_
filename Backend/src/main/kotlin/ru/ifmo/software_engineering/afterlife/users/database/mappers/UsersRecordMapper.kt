package ru.ifmo.software_engineering.afterlife.users.database.mappers

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.database.Tables.USERS
import ru.ifmo.software_engineering.afterlife.database.tables.records.UsersRecord
import ru.ifmo.software_engineering.afterlife.users.domain.Authority
import ru.ifmo.software_engineering.afterlife.users.domain.User

@Component
class UsersRecordMapper : RecordMapper<UsersRecord, Authority> {
    override fun map(record: UsersRecord?): Authority? =
            if (record == null) null
            else Authority(
                    user = User(username = record[USERS.USERNAME], id = record[USERS.ID]),
                    passwordHash = record[USERS.PASSWORD_HASH],
                    roles = emptyList())
}