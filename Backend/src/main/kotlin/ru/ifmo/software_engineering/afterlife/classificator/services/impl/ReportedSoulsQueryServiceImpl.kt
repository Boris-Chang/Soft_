package ru.ifmo.software_engineering.afterlife.classificator.services.impl

import org.springframework.stereotype.Service
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoul
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoulsQueryFilter
import ru.ifmo.software_engineering.afterlife.classificator.repositories.SoulRepository
import ru.ifmo.software_engineering.afterlife.classificator.services.ReportedSoulsQueryService
import ru.ifmo.software_engineering.afterlife.core.models.PageRequest
import ru.ifmo.software_engineering.afterlife.core.models.PagedResult

@Service
class ReportedSoulsQueryServiceImpl(
    private val repository: SoulRepository
) : ReportedSoulsQueryService {
    override suspend fun getAllReportedSouls(reportFilter: ReportedSoulsQueryFilter?, pageRequest: PageRequest?): PagedResult<ReportedSoul> =
        repository.getReportedSouls(reportFilter, pageRequest ?: PageRequest.default())
}
