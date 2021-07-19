package ru.ifmo.software_engineering.afterlife.classificator.domain

import ru.ifmo.software_engineering.afterlife.users.domain.User
import java.util.*

data class GoodnessReport(
    val id: Long,
    val soul: Soul,
    val goodnessEvidences: List<GoodnessEvidence>,
    val uploadedBy: User,
    val uploadedAt: Date,
)
