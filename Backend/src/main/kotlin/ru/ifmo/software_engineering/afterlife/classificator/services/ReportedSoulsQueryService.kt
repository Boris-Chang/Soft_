package ru.ifmo.software_engineering.afterlife.classificator.services

import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoul

interface ReportedSoulsQueryService {
    suspend fun getAllReportedSouls(): List<ReportedSoul>
}
