package ru.ifmo.software_engineering.afterlife.classificator.services

import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoul
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoulsQueryFilter

interface ReportedSoulsQueryService {
    suspend fun getAllReportedSouls(reportFilter: ReportedSoulsQueryFilter? = null): List<ReportedSoul>
}
