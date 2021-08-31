package ru.ifmo.software_engineering.afterlife.classificator.services

import org.springframework.stereotype.Service
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoul
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoulsQueryFilter
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.classificator.repositories.SoulRepository
import ru.ifmo.software_engineering.afterlife.core.models.PageRequest
import ru.ifmo.software_engineering.afterlife.core.models.PagedResult

interface SoulsQueryService {
    suspend fun getAllReportedSouls(reportFilter: ReportedSoulsQueryFilter? = null, pageRequest: PageRequest? = null): PagedResult<ReportedSoul>
    suspend fun getSoulById(id: Long): Soul?
}

@Service
class SoulsQueryServiceImpl(
    private val repository: SoulRepository
) : SoulsQueryService {
    override suspend fun getAllReportedSouls(reportFilter: ReportedSoulsQueryFilter?, pageRequest: PageRequest?): PagedResult<ReportedSoul> =
        repository.getReportedSouls(reportFilter, pageRequest ?: PageRequest.default())

    override suspend fun getSoulById(id: Long): Soul? =
        repository.findById(id)
}
