package ru.ifmo.software_engineering.afterlife.quality_control.repositories

import kotlinx.coroutines.future.await
import org.jooq.*
import org.jooq.impl.DSL.count
import org.springframework.stereotype.Repository
import ru.ifmo.software_engineering.afterlife.core.models.PageRequest
import ru.ifmo.software_engineering.afterlife.core.models.PagedResult
import ru.ifmo.software_engineering.afterlife.database.Tables.MEASUREMENTS
import ru.ifmo.software_engineering.afterlife.database.Tables.THRESHOLDS
import ru.ifmo.software_engineering.afterlife.database.tables.records.MeasurementsRecord
import ru.ifmo.software_engineering.afterlife.database.tables.records.ThresholdsRecord
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Measurement
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Threshold
import ru.ifmo.software_engineering.afterlife.utils.jooq.paged

interface MeasurementsRepository {
    suspend fun findAll(pageRequest: PageRequest): PagedResult<Measurement>
    suspend fun findById(id: Long): Measurement?
    suspend fun save(measurement: Measurement): Measurement
    suspend fun update(measurement: Measurement): Measurement
}

@Repository
class MeasurementsRepositoryImpl(
    private val dsl: DSLContext,
    private val recordMapper: RecordMapper<Record, Measurement>,
    private val thresholdRecordMapper: RecordMapper<ThresholdsRecord, Threshold>,
    private val recordUnmapper: RecordUnmapper<Measurement, MeasurementsRecord>
) : MeasurementsRepository {
    override suspend fun findAll(pageRequest: PageRequest): PagedResult<Measurement> {
        val countField = count()
        val count = this.selectFromMeasurements(countField).fetchAsync().await().first().getValue(countField)
        val measurements = this.selectFromMeasurements()
            .paged(pageRequest)
            .fetchAsync()
            .await()
            .map { this.recordMapper.map(it) }

        return PagedResult(measurements, count, pageRequest.pageNumber)
    }

    override suspend fun findById(id: Long): Measurement? =
        this.selectFromMeasurements()
            .where(MEASUREMENTS.ID.eq(id))
            .fetchAsync()
            .await()
            .map { recordMapper.map(it) }
            .firstOrNull()

    override suspend fun save(measurement: Measurement): Measurement =
        this.dsl.insertInto(MEASUREMENTS)
            .set(this.recordUnmapper.unmap(measurement))
            .returning()
            .fetchAsync()
            .await()
            .map(this.recordMapper)
            .first()
            .thenSaveThreshold(measurement.threshold)

    override suspend fun update(measurement: Measurement): Measurement =
        this.dsl.update(MEASUREMENTS)
            .set(this.recordUnmapper.unmap(measurement))
            .where(MEASUREMENTS.ID.eq(measurement.id))
            .returning()
            .fetchAsync()
            .await()
            .map(this.recordMapper)
            .first()
            .thenSaveThreshold(measurement.threshold)

    private suspend fun Measurement.thenSaveThreshold(threshold: Threshold?): Measurement {
        val savedThreshold =
                if (threshold != null) saveOrUpdateThreshold(threshold, this)
                else deleteThreshold(this).let { null }
        return this.copy(threshold = savedThreshold)
    }

    private suspend fun saveOrUpdateThreshold(threshold: Threshold, measurement: Measurement): Threshold =
            this.dsl.insertInto(THRESHOLDS)
                    .set(ThresholdsRecord(measurement.id, threshold.value))
                    .onConflict(THRESHOLDS.MEASUREMENT_ID)
                    .doUpdate()
                    .set(ThresholdsRecord(measurement.id, threshold.value))
                    .returning()
                    .fetchAsync()
                    .await()
                    .map { thresholdRecordMapper.map(it) }
                    .first()

    private suspend fun deleteThreshold(measurement: Measurement) {
        this.dsl.delete(THRESHOLDS)
                .where(THRESHOLDS.MEASUREMENT_ID.eq(measurement.id))
                .executeAsync()
                .await()
    }

    private fun selectFromMeasurements(vararg f: SelectFieldOrAsterisk): SelectJoinStep<Record> =
        this.dsl.select(*f).from(MEASUREMENTS)
                .leftJoin(THRESHOLDS).on(THRESHOLDS.MEASUREMENT_ID.eq(MEASUREMENTS.ID))
}
