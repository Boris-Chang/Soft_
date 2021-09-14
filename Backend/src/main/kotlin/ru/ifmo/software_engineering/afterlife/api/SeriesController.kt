package ru.ifmo.software_engineering.afterlife.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.ifmo.software_engineering.afterlife.api.models.SeriesRequestData
import ru.ifmo.software_engineering.afterlife.api.models.asModel
import ru.ifmo.software_engineering.afterlife.core.exceptions.NotFoundException
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Series
import ru.ifmo.software_engineering.afterlife.quality_control.domain.SeriesValue
import ru.ifmo.software_engineering.afterlife.quality_control.services.SeriesService

@RestController
@RequestMapping("api/series")
class SeriesController(
    private val seriesService: SeriesService
) {
    @GetMapping("{id}")
    suspend fun getById(@PathVariable("id") id: Long): ResponseEntity<Series> =
        this.seriesService.findSeriesById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()

    @PutMapping("{id}")
    suspend fun updateSeries(@PathVariable("id") id: Long, @RequestBody series: SeriesRequestData): ResponseEntity<Series> =
        this.seriesService.updateSeries(series.asModel(id)).fold(
            { err ->
                when (err) {
                    is NotFoundException -> ResponseEntity.notFound().build()
                    else -> throw err
                }
            },
            {
                ResponseEntity.ok(it)
            }
        )

    @PostMapping("{id}/values")
    suspend fun addSeriesValue(@PathVariable("id") id: Long, @RequestBody value: SeriesValue): ResponseEntity<Series> =
        this.seriesService.addSeriesValue(id, value).fold(
            { err ->
                when (err) {
                    is NotFoundException -> ResponseEntity.notFound().build()
                    else -> throw err
                }
            },
            {
                ResponseEntity.ok(it)
            }
        )
}
