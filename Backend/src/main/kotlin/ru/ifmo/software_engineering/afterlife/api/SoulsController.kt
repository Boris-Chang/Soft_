package ru.ifmo.software_engineering.afterlife.api

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoul
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoulsQueryFilter
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.classificator.services.ReportedSoulsQueryService
import ru.ifmo.software_engineering.afterlife.classificator.services.SoulRegistrar

@RestController
@RequestMapping("api/souls")
class SoulsController(
    private val soulsRegistrar: SoulRegistrar,
    private val soulsQueryService: ReportedSoulsQueryService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun registerNewSoul(@RequestBody soul: Soul): Soul {
        return this.soulsRegistrar.registerNewSoul(soul)
    }

    @GetMapping
    suspend fun getSouls(@RequestParam("report-filter") reportFilter: ReportedSoulsQueryFilter?): List<ReportedSoul> {
        return this.soulsQueryService.getAllReportedSouls(reportFilter)
    }
}
