package ru.ifmo.software_engineering.afterlife.classificator.services

import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul

interface SoulRegistrar {
    suspend fun registerNewSoul(soul: Soul): Soul
}
