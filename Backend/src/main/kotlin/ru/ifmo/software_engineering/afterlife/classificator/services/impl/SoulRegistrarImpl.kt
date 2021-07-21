package ru.ifmo.software_engineering.afterlife.classificator.services.impl

import org.springframework.stereotype.Service
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.classificator.repositories.SoulRepository
import ru.ifmo.software_engineering.afterlife.classificator.services.SoulRegistrar

@Service
class SoulRegistrarImpl(
    private val soulRepository: SoulRepository
) : SoulRegistrar {
    override suspend fun registerNewSoul(soul: Soul): Soul =
        this.soulRepository.insertOne(soul)
}
