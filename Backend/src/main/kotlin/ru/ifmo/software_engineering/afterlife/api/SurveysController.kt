package ru.ifmo.software_engineering.afterlife.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.ifmo.software_engineering.afterlife.core.exceptions.NotFoundException
import ru.ifmo.software_engineering.afterlife.core.models.PageRequest
import ru.ifmo.software_engineering.afterlife.core.models.PagedResult
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Survey
import ru.ifmo.software_engineering.afterlife.quality_control.services.SurveysService
import javax.validation.Valid

@RestController
@RequestMapping("api/surveys")
class SurveysController(
    private val surveysService: SurveysService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createSurvey(@Valid @RequestBody survey: Survey): Survey =
        this.surveysService.createSurvey(survey)

    @GetMapping
    suspend fun getAllSurveys(
        @RequestParam("page-number", required = false, defaultValue = "0")
        pageNumber: Int = 0,

        @RequestParam(
            "page-size", required = false, defaultValue = PageRequest.DEFAULT_PAGE_SIZE.toString()
        )
        pageSize: Int?,
    ): PagedResult<Survey> =
        this.surveysService.getAllSurveys(
            PageRequest(pageNumber, pageSize ?: PageRequest.DEFAULT_PAGE_SIZE)
        )

    @GetMapping("{surveyId}")
    suspend fun getSurveyById(@PathVariable("surveyId") surveyId: Long): ResponseEntity<Survey> =
        this.surveysService.getSurveyById(surveyId)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()

    @PutMapping("{surveyId}")
    suspend fun updateSurvey(@PathVariable("surveyId") surveyId: Long, @Valid @RequestBody survey: Survey): ResponseEntity<Survey> =
        this.surveysService.updateSurvey(surveyId, survey).fold(
            { err ->
                when (err) {
                    is NotFoundException -> ResponseEntity.notFound().build()
                    else -> throw err
                }
            },
            { ResponseEntity.ok(it) }
        )

    @DeleteMapping("{surveyId}")
    suspend fun deleteSurvey(@PathVariable("surveyId") surveyId: Long): ResponseEntity<Any> =
        this.surveysService.deleteSurvey(surveyId).fold(
            { err ->
                when (err) {
                    is NotFoundException -> ResponseEntity.notFound().build()
                    else -> throw err
                }
            },
            { ResponseEntity.noContent().build() }
        )
}
