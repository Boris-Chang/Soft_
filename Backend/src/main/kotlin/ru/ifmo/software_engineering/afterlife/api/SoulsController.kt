package ru.ifmo.software_engineering.afterlife.api

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoul
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoulsQueryFilter
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.classificator.services.ReportedSoulsQueryService
import ru.ifmo.software_engineering.afterlife.classificator.services.SoulRegistrar
import ru.ifmo.software_engineering.afterlife.core.models.PageRequest
import ru.ifmo.software_engineering.afterlife.core.models.PagedResult

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
    suspend fun getSouls(
        @RequestParam("report-filter", required = false)
        reportFilter: ReportedSoulsQueryFilter?,

        @RequestParam("page-number", required = false, defaultValue = "0")
        pageNumber: Int = 0,

        @RequestParam(
            "page-size", required = false, defaultValue = PageRequest.DEFAULT_PAGE_SIZE.toString()
        )
        pageSize: Int?,

    ): PagedResult<ReportedSoul> {
        val pageRequest = pageSize?.let {
            PageRequest(pageNumber, it)
        }

        return this.soulsQueryService.getAllReportedSouls(
            reportFilter,
            pageRequest
        )
    }
}
