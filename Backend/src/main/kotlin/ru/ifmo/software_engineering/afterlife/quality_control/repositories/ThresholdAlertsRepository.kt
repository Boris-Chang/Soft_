package ru.ifmo.software_engineering.afterlife.quality_control.repositories

import kotlinx.coroutines.future.await
import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.jooq.RecordUnmapper
import org.springframework.stereotype.Repository
import ru.ifmo.software_engineering.afterlife.database.Tables.THRESHOLD_ALERTS
import ru.ifmo.software_engineering.afterlife.database.tables.records.ThresholdAlertsRecord
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Measurement
import ru.ifmo.software_engineering.afterlife.quality_control.domain.ThresholdAlert

interface ThresholdAlertsRepository {
    suspend fun save(alert: ThresholdAlert): ThresholdAlert
    suspend fun findAllByMeasurement(measurement: Measurement): List<ThresholdAlert>
}

@Repository
class ThresholdAlertsRepositoryImpl(
        private val dsl: DSLContext,
        private val recordMapper: RecordMapper<ThresholdAlertsRecord, ThresholdAlert>,
        private val recordUnmapper: RecordUnmapper<ThresholdAlert, ThresholdAlertsRecord>
) : ThresholdAlertsRepository {
    override suspend fun save(alert: ThresholdAlert): ThresholdAlert =
        dsl.insertInto(THRESHOLD_ALERTS)
                .set(recordUnmapper.unmap(alert))
                .returning()
                .fetchAsync()
                .await()
                .map(recordMapper)
                .first()

    override suspend fun findAllByMeasurement(measurement: Measurement): List<ThresholdAlert> {
        return dsl.select().from(THRESHOLD_ALERTS)
                .where(THRESHOLD_ALERTS.MEASUREMENT_ID.eq(measurement.id))
                .fetchAsync()
                .await()
                .map { recordMapper.map(it.into(THRESHOLD_ALERTS)) }
    }
}
