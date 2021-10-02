package ru.ifmo.software_engineering.afterlife.api

import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportComment
import ru.ifmo.software_engineering.afterlife.classificator.services.ReportsCommentService
import ru.ifmo.software_engineering.afterlife.core.exceptions.NotFoundException

@RestController
@RequestMapping("api/souls/{soulId}/comments")
class ReportCommentsController(
    private val reportsCommentService: ReportsCommentService,
) {
    @GetMapping
    @Operation(summary = "Get all comments for soul by its id")
    suspend fun getSoulComments(@PathVariable("soulId") soulId: Long) =
        this.reportsCommentService.getCommentsBySoulId(soulId).fold(
            {
                when (it) {
                    is NotFoundException -> ResponseEntity.notFound().build()
                    else -> throw it
                }
            },
            {
                ResponseEntity.ok(it)
            }
        )

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates comment for soul")
    suspend fun postCommentForSoul(
        @PathVariable("soulId") soulId: Long,
        @RequestBody comment: ReportComment
    ) =
        this.reportsCommentService.postCommentForSoulById(soulId, comment).fold(
            {
                when (it) {
                    is NotFoundException -> ResponseEntity.notFound().build()
                    else -> throw it
                }
            },
            {
                ResponseEntity.status(HttpStatus.CREATED).body(it)
            }
        )
}
