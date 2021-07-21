package ru.ifmo.software_engineering.afterlife.classificator.repositories

import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul

interface SoulRepository {
    suspend fun insertOne(soul: Soul): Soul
}
