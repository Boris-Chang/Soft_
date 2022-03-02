package ru.ifmo.software_engineering.afterlife.quality_control.services

import arrow.core.Validated
import arrow.core.invalid
import arrow.core.valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.software_engineering.afterlife.core.exceptions.ApplicationException
import ru.ifmo.software_engineering.afterlife.core.exceptions.ForbiddenException
import ru.ifmo.software_engineering.afterlife.core.exceptions.NotFoundException
import ru.ifmo.software_engineering.afterlife.core.exceptions.UnauthorizedException
import ru.ifmo.software_engineering.afterlife.core.models.PageRequest
import ru.ifmo.software_engineering.afterlife.core.models.PagedResult
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Measurement
import ru.ifmo.software_engineering.afterlife.quality_control.repositories.MeasurementsRepository
import ru.ifmo.software_engineering.afterlife.security.Principal
import ru.ifmo.software_engineering.afterlife.security.services.AuthorizationService
import ru.ifmo.software_engineering.afterlife.users.constants.RoleNames

interface MeasurementsService {
    suspend fun findAll(pageRequest: PageRequest): PagedResult<Measurement>
    suspend fun findMeasurementById(id: Long): Measurement?
    suspend fun createMeasurement(measurement: Measurement): Measurement
    suspend fun updateMeasurement(measurement: Measurement): Validated<ApplicationException, Measurement>
}

@Service
class MeasurementsServiceImpl(
    private val measurementsRepository: MeasurementsRepository,
    private val authorizationService: AuthorizationService
) : MeasurementsService {
    override suspend fun findAll(pageRequest: PageRequest): PagedResult<Measurement>  {
        currentUser().apply { checkIsQualityControl() }
        return this.measurementsRepository.findAll(pageRequest)
    }

    override suspend fun findMeasurementById(id: Long): Measurement? {
        currentUser().apply { checkIsQualityControl() }
        return this.measurementsRepository.findById(id)
    }

    override suspend fun createMeasurement(measurement: Measurement): Measurement {
        currentUser().apply { checkIsQualityControl() }
        return this.measurementsRepository.save(measurement)
    }

    @Transactional
    override suspend fun updateMeasurement(measurement: Measurement): Validated<ApplicationException, Measurement> {
        try {
            currentUser().apply { checkIsQualityControl() }
        } catch (e: ApplicationException) {
            return e.invalid()
        }

        return this.measurementsRepository.findById(measurement.id)?.let {
            this.measurementsRepository.update(measurement).valid()
        } ?: NotFoundException("measurement not found").invalid()
    }

    private suspend fun currentUser() =
        authorizationService.getCurrentUser() ?: throw UnauthorizedException()

    private fun Principal.checkIsQualityControl() {
        if (!this.roles.contains(RoleNames.QUALITY_CONTROL)) {
            throw ForbiddenException()
        }
    }
}
