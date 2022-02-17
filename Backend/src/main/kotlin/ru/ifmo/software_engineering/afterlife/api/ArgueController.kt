package ru.ifmo.software_engineering.afterlife.api

import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*
import ru.ifmo.software_engineering.afterlife.api.models.ChangeClassifierDecisionRequest
import ru.ifmo.software_engineering.afterlife.classificator.domain.ArguedClassifierDecision
import ru.ifmo.software_engineering.afterlife.classificator.domain.HellCircle
import ru.ifmo.software_engineering.afterlife.classificator.domain.ParadiseSphere
import ru.ifmo.software_engineering.afterlife.classificator.services.ArgueClassifierService
import ru.ifmo.software_engineering.afterlife.database.enums.AfterworldKind

@RestController
@RequestMapping("api/souls/{soulId}")
class ArgueController(
        private val argueClassifierService: ArgueClassifierService
) {
    @GetMapping("argue")
    @Operation(summary = "Get argue or classifier decision change for soul by id")
    suspend fun getArgueForSoul(@PathVariable("soulId") soulId: Long): ArguedClassifierDecision =
            argueClassifierService.getSoulArgue(soulId)

    @PostMapping("argue")
    @Operation(summary = "Mark classifier decision of soul as argued")
    suspend fun argueSoul(@PathVariable("soulId") soulId: Long): ArguedClassifierDecision =
            argueClassifierService.markClassifierDecisionArgued(soulId)

    @PostMapping("change-decision")
    @Operation(summary = "Changes classifier decision of soul")
    suspend fun changeClassifierDecision(
            @PathVariable("soulId") soulId: Long,
            @RequestBody req: ChangeClassifierDecisionRequest
    ) : ArguedClassifierDecision {
        val newSection = when (req.afterworldKind) {
            AfterworldKind.HELL -> HellCircle(req.sectionIndex)
            AfterworldKind.PARADISE -> ParadiseSphere(req.sectionIndex)
        }

        return argueClassifierService.changeClassifierDecision(soulId, newSection)
    }
}