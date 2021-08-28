package ru.ifmo.software_engineering.afterlife.classificator.services.impl

import org.springframework.stereotype.Service
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoul
import ru.ifmo.software_engineering.afterlife.classificator.repositories.SoulRepository
import ru.ifmo.software_engineering.afterlife.classificator.services.ReportedSoulsQueryService

@Service
class ReportedSoulsQueryServiceImpl(
    private val repository: SoulRepository
) : ReportedSoulsQueryService {
    override suspend fun getAllReportedSouls(): List<ReportedSoul> =
        repository.getReportedSouls()
            .sortedByDescending { it.lastUpdate }
}
