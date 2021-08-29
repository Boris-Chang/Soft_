package ru.ifmo.software_engineering.afterlife.classificator.services

import org.springframework.stereotype.Service
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.classificator.repositories.SoulRepository

interface SoulRegistrar {
    suspend fun registerNewSoul(soul: Soul): Soul
}

@Service
class SoulRegistrarImpl(
    private val soulRepository: SoulRepository
) : SoulRegistrar {
    override suspend fun registerNewSoul(soul: Soul): Soul =
        this.soulRepository.insertOne(soul)
}
