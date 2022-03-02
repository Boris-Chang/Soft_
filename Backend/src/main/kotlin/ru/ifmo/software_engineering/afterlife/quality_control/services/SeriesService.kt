package ru.ifmo.software_engineering.afterlife.quality_control.services

import arrow.core.*
import arrow.core.computations.either
import org.springframework.stereotype.Service
import ru.ifmo.software_engineering.afterlife.core.exceptions.ApplicationException
import ru.ifmo.software_engineering.afterlife.core.exceptions.ForbiddenException
import ru.ifmo.software_engineering.afterlife.core.exceptions.NotFoundException
import ru.ifmo.software_engineering.afterlife.core.exceptions.UnauthorizedException
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Measurement
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Series
import ru.ifmo.software_engineering.afterlife.quality_control.domain.SeriesValue
import ru.ifmo.software_engineering.afterlife.quality_control.repositories.MeasurementsRepository
import ru.ifmo.software_engineering.afterlife.quality_control.repositories.SeriesRepository
import ru.ifmo.software_engineering.afterlife.quality_control.repositories.SeriesValueRepository
import ru.ifmo.software_engineering.afterlife.security.Principal
import ru.ifmo.software_engineering.afterlife.security.services.AuthorizationService
import ru.ifmo.software_engineering.afterlife.users.constants.RoleNames
import java.time.ZonedDateTime

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
    private val authorizationService: AuthorizationService
) : SeriesService {
    override suspend fun findSeriesById(id: Long): Series? {
        val validatedAccessToSeries = this.validateUserHasAccessToSeries()
        if (validatedAccessToSeries.isInvalid) {
            throw validatedAccessToSeries.swap().orNull()!!
        }

        return seriesRepository.findById(id)
    }

    override suspend fun findSeriesByMeasurementId(measurementId: Long): Validated<ApplicationException, List<Series>> =
            either<ApplicationException, List<Series>> {
                validateUserHasAccessToSeries().bind()
                val measurement = tryFindMeasurement(measurementId).bind()
                seriesRepository.findAllByMeasurement(measurement)
            }.toValidated()

    override suspend fun createSeries(measurementId: Long, series: Series): Validated<ApplicationException, Series> =
            either<ApplicationException, Series> {
                validateUserHasAccessToSeries().toEither().bind()
                val measurement = tryFindMeasurement(measurementId).toEither().bind()
                val createdSeries = seriesRepository.save(series.copy(measurement = measurement))
                findSeriesById(createdSeries.id)!!
            }.toValidated()

    override suspend fun updateSeries(series: Series): Validated<ApplicationException, Series> =
            either<ApplicationException, Series> {
                validateUserHasAccessToSeries().toEither().bind()
                val currentSeries = tryFindSeries(series.id).toEither().bind()
                val updatedSeries = seriesRepository.update(currentSeries.withUpdatedName(series.name))
                findSeriesById(updatedSeries.id)!!
            }.toValidated()

    override suspend fun addSeriesValue(seriesId: Long, value: SeriesValue): Validated<ApplicationException, Series> {
        //TODO: check role for user provider
        return this.tryFindSeries(seriesId).map { series ->
            this.seriesValueRepository.save(value.copy(timestamp = ZonedDateTime.now()), series)
            this.findSeriesById(seriesId)!!
        }
    }

    private suspend fun validateUserHasAccessToSeries(): Validated<ApplicationException, Principal> =
        this.authorizationService.getCurrentUser().toOption()
                .toEither { UnauthorizedException() }
                .flatMap {
                    if (it.isInRole(RoleNames.QUALITY_CONTROL))
                        it.right() else ForbiddenException().left()
                }.toValidated()

    private suspend fun tryFindMeasurement(measurementId: Long): Validated<NotFoundException, Measurement> =
        this.measurementsRepository.findById(measurementId)?.valid()
            ?: NotFoundException("Measurement with id $measurementId not exist").invalid()

    private suspend fun tryFindSeries(seriesId: Long): Validated<ApplicationException, Series> =
        this.seriesRepository.findById(seriesId)?.valid()
            ?: NotFoundException("Series with id $seriesId not found").invalid()
}
