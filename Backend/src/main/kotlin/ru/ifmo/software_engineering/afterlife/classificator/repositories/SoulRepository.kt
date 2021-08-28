package ru.ifmo.software_engineering.afterlife.classificator.repositories

import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoul
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoulsQueryFilter
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul

interface SoulRepository {
    suspend fun insertOne(soul: Soul): Soul
    suspend fun getReportedSouls(filter: ReportedSoulsQueryFilter? = null): List<ReportedSoul>
}
