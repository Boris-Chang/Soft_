package ru.ifmo.software_engineering.afterlife.quality_control.services

import arrow.core.Validated
import arrow.core.invalid
import arrow.core.valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.software_engineering.afterlife.core.exceptions.ApplicationException
import ru.ifmo.software_engineering.afterlife.core.exceptions.NotFoundException
import ru.ifmo.software_engineering.afterlife.core.models.PageRequest
import ru.ifmo.software_engineering.afterlife.core.models.PagedResult
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Measurement
import ru.ifmo.software_engineering.afterlife.quality_control.repositories.MeasurementsRepository

interface MeasurementsService {
    suspend fun findAll(pageRequest: PageRequest): PagedResult<Measurement>
    suspend fun findMeasurementById(id: Long): Measurement?
    suspend fun createMeasurement(measurement: Measurement): Measurement
    suspend fun updateMeasurement(measurement: Measurement): Validated<ApplicationException, Measurement>
}

@Service
class MeasurementsServiceImpl(
    private val measurementsRepository: MeasurementsRepository,
) : MeasurementsService {
    override suspend fun findAll(pageRequest: PageRequest): PagedResult<Measurement> =
        this.measurementsRepository.findAll(pageRequest)

    override suspend fun findMeasurementById(id: Long): Measurement? =
        this.measurementsRepository.findById(id)

    override suspend fun createMeasurement(measurement: Measurement): Measurement =
        this.measurementsRepository.save(measurement)

    @Transactional
    override suspend fun updateMeasurement(measurement: Measurement): Validated<ApplicationException, Measurement> =
        this.measurementsRepository.findById(measurement.id)?.let {
            this.measurementsRepository.update(measurement).valid()
        } ?: NotFoundException("measurement not found").invalid()
}
