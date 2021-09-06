package ru.ifmo.software_engineering.afterlife.quality_control.services

import arrow.core.Validated
import arrow.core.invalid
import arrow.core.valid
import org.springframework.stereotype.Service
import ru.ifmo.software_engineering.afterlife.core.exceptions.ApplicationException
import ru.ifmo.software_engineering.afterlife.core.exceptions.NotFoundException
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Measurement
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Series
import ru.ifmo.software_engineering.afterlife.quality_control.domain.SeriesValue
import ru.ifmo.software_engineering.afterlife.quality_control.repositories.MeasurementsRepository
import ru.ifmo.software_engineering.afterlife.quality_control.repositories.SeriesRepository
import ru.ifmo.software_engineering.afterlife.quality_control.repositories.SeriesValueRepository

interface SeriesService {
    suspend fun findSeriesById(id: Long): Series?
    suspend fun findSeriesByMeasurementId(measurementId: Long): Validated<ApplicationException, List<Series>>
    suspend fun createSeries(measurementId: Long, series: Series): Validated<ApplicationException, Series>
    suspend fun updateSeries(series: Series): Validated<ApplicationException, Series>
    suspend fun addSeriesValue(seriesId: Long, value: SeriesValue): Validated<ApplicationException, Series>
}

@Service
class SeriesServiceImpl(
    private val measurementsRepository: MeasurementsRepository,
    private val seriesRepository: SeriesRepository,
    private val seriesValueRepository: SeriesValueRepository,
) : SeriesService {
    override suspend fun findSeriesById(id: Long): Series? =
        this.seriesRepository.findById(id)

    override suspend fun findSeriesByMeasurementId(measurementId: Long): Validated<ApplicationException, List<Series>> =
        this.tryFindMeasurement(measurementId)
            .map { measurement ->
                this.seriesRepository.findAllByMeasurement(measurement)
            }

    override suspend fun createSeries(measurementId: Long, series: Series): Validated<ApplicationException, Series> =
        this.tryFindMeasurement(measurementId).map { measurement ->
            val createdSeries = this.seriesRepository.save(series.copy(measurement = measurement))
            this.findSeriesById(createdSeries.id)!!
        }

    override suspend fun updateSeries(series: Series): Validated<ApplicationException, Series> =
        this.tryFindSeries(series.id).map { currentSeries ->
            val updatedSeries = this.seriesRepository.update(currentSeries.copy(name = series.name))
            this.findSeriesById(updatedSeries.id)!!
        }

    override suspend fun addSeriesValue(seriesId: Long, value: SeriesValue): Validated<ApplicationException, Series> {
        return this.tryFindSeries(seriesId).map { series ->
            this.seriesValueRepository.save(value, series)
            this.findSeriesById(seriesId)!!
        }
    }

    private suspend fun tryFindMeasurement(measurementId: Long): Validated<NotFoundException, Measurement> =
        this.measurementsRepository.findById(measurementId)?.valid()
            ?: NotFoundException("Measurement with id $measurementId not exist").invalid()

    private suspend fun tryFindSeries(seriesId: Long): Validated<ApplicationException, Series> =
        this.findSeriesById(seriesId)?.valid()
            ?: NotFoundException("Series with id $seriesId not found").invalid()
}