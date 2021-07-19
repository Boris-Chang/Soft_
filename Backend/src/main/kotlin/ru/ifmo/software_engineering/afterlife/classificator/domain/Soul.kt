package ru.ifmo.software_engineering.afterlife.classificator.domain

import java.util.*

data class Soul(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val dateOfDeath: Date,
)