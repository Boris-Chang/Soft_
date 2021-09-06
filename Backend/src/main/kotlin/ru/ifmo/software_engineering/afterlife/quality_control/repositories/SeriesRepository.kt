package ru.ifmo.software_engineering.afterlife.quality_control.repositories

import kotlinx.coroutines.future.await
import org.jooq.*
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.software_engineering.afterlife.database.Tables.*
import ru.ifmo.software_engineering.afterlife.database.tables.records.MeasurementsRecord
import ru.ifmo.software_engineering.afterlife.database.tables.records.SeriesRecord
import ru.ifmo.software_engineering.afterlife.database.tables.records.SeriesValuesRecord
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Measurement
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Series
import ru.ifmo.software_engineering.afterlife.quality_control.domain.SeriesValue

interface SeriesRepository {
    suspend fun findById(id: Long): Series?
    suspend fun findAllByMeasurement(measurement: Measurement): List<Series>
    suspend fun save(series: Series): Series
    @Transactional
    suspend fun update(series: Series): Series
}

@Repository
class SeriesRepositoryImpl(
    private val dsl: DSLContext,
    private val seriesMapper: RecordMapper<SeriesRecord, Series>,
    private val measurementMapper: RecordMapper<MeasurementsRecord, Measurement>,
    private val seriesValueMapper: RecordMapper<SeriesValuesRecord, SeriesValue>,
    private val seriesUnmapper: RecordUnmapper<Series, SeriesRecord>,
) : SeriesRepository {
    override suspend fun save(series: Series): Series =
        this.dsl.insertInto(SERIES)
            .set(this.seriesUnmapper.unmap(series))
            .returning()
            .fetchAsync()
            .await()
            .map(this.seriesMapper)
            .map { it.copy(measurement = series.measurement) }
            .first()

    @Transactional
    override suspend fun update(series: Series): Series =
        this.dsl.update(SERIES)
            .set(this.seriesUnmapper.unmap(series)
                .apply { this.changed(SERIES.MEASUREMENT_ID, false) })
            .where(SERIES.ID.eq(series.id))
            .returning()
            .fetchAsync()
            .await()
            .map(seriesMapper)
            .first()
            .let { this.findById(it.id) }!!

    override suspend fun findById(id: Long): Series? =
       this.selectFromSeries()
           .where(SERIES.ID.eq(id))
           .fetchAsync()
           .await()
           .mapToSeries()
           .firstOrNull()

    override suspend fun findAllByMeasurement(measurement: Measurement): List<Series> =
        this.selectFromSeries()
            .where(SERIES.MEASUREMENT_ID.eq(measurement.id))
            .fetchAsync()
            .await()
            .mapToSeries()

    private fun selectFromSeries() =
        this.dsl.select().from(SERIES)
            .join(MEASUREMENTS)
            .on(MEASUREMENTS.ID.eq(SERIES.MEASUREMENT_ID))
            .leftJoin(SERIES_VALUES)
            .on(SERIES_VALUES.SERIES_ID.eq(SERIES.ID))

    private fun Result<Record>.mapToSeries(): List<Series> =
        this.intoGroups({
            Pair(
                seriesMapper.map(it.into(SERIES)),
                measurementMapper.map(it.into(MEASUREMENTS)),
            )},
            { seriesValueMapper.map(it.into(SERIES_VALUES)) })
            .map {
                val (series, measurement) = it.key
                val values = it.value.filterNotNull()
                series!!.copy(measurement = measurement!!, values = values)
            }
}
