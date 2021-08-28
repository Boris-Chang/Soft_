package ru.ifmo.software_engineering.afterlife.classificator.services

import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoul
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoulsQueryFilter
import ru.ifmo.software_engineering.afterlife.core.models.PageRequest
import ru.ifmo.software_engineering.afterlife.core.models.PagedResult

interface ReportedSoulsQueryService {
    suspend fun getAllReportedSouls(reportFilter: ReportedSoulsQueryFilter? = null, pageRequest: PageRequest? = null): PagedResult<ReportedSoul>
}
