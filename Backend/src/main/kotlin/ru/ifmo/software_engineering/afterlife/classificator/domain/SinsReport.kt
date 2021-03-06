package ru.ifmo.software_engineering.afterlife.classificator.domain

import ru.ifmo.software_engineering.afterlife.users.domain.User
import java.time.ZonedDateTime

data class SinsReport(
    val id: Long,
    val soul: Soul,
    val sins: List<SinEvidence>,
    val uploadedBy: User,
    val uploadedAt: ZonedDateTime,
)
