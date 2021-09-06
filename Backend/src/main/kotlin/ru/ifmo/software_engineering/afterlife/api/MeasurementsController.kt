package ru.ifmo.software_engineering.afterlife.api

import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.ifmo.software_engineering.afterlife.api.models.SeriesRequestData
import ru.ifmo.software_engineering.afterlife.api.models.asModel
import ru.ifmo.software_engineering.afterlife.core.exceptions.NotFoundException
import ru.ifmo.software_engineering.afterlife.core.models.PageRequest
import ru.ifmo.software_engineering.afterlife.core.models.PagedResult
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Measurement
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Series
import ru.ifmo.software_engineering.afterlife.quality_control.services.MeasurementsService
import ru.ifmo.software_engineering.afterlife.quality_control.services.SeriesService

@RestController
@RequestMapping("api/measurements")
class MeasurementsController(
    private val measurementsService: MeasurementsService,
    private val seriesService: SeriesService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates new measurement")
    suspend fun createMeasurement(@RequestBody measurement: Measurement): Measurement =
        this.measurementsService.createMeasurement(measurement)

    @GetMapping
    @Operation(summary = "Get paged result for all measurements")
    suspend fun getAll(
        @RequestParam("page-number", required = false, defaultValue = "0")
        pageNumber: Int = 0,

        @RequestParam(
            "page-size", required = false, defaultValue = PageRequest.DEFAULT_PAGE_SIZE.toString()
        )
        pageSize: Int?,
    ): PagedResult<Measurement> =
        this.measurementsService.findAll(PageRequest(pageNumber))

    @GetMapping("{id}")
    suspend fun getById(@PathVariable("id") id: Long): ResponseEntity<Measurement> =
        this.measurementsService.findMeasurementById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()

    @PutMapping("{id}")
    suspend fun update(@PathVariable("id") id: Long, @RequestBody measurement: Measurement): ResponseEntity<Measurement> =
        this.measurementsService.updateMeasurement(measurement.copy(id = id)).fold({
                when (it) {
                    is NotFoundException -> ResponseEntity.notFound().build()
                    else -> throw it
                }
            }, {
                ResponseEntity.ok(it)
            })

    @GetMapping("{measurementId}/series")
    suspend fun getSeriesByMeasurementId(@PathVariable("measurementId") measurementId: Long): ResponseEntity<List<Series>> =
        this.seriesService.findSeriesByMeasurementId(measurementId).fold({ err ->
            when (err) {
                is NotFoundException -> ResponseEntity.notFound().build()
                else -> throw err
            }
        }, {
            ResponseEntity.ok(it)
        })

    @PostMapping("{measurementId}/series")
    suspend fun createSeries(@PathVariable("measurementId") measurementId: Long, @RequestBody series: SeriesRequestData): ResponseEntity<Series> =
        this.seriesService.createSeries(measurementId, series.asModel(0)).fold({ err ->
            when (err) {
                is NotFoundException -> ResponseEntity.notFound().build()
                else -> throw err
            }
        }, {
            ResponseEntity.ok(it)
        })
}