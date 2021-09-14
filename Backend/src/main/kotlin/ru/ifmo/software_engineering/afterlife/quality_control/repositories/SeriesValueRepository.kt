package ru.ifmo.software_engineering.afterlife.quality_control.repositories

import kotlinx.coroutines.future.await
import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.jooq.RecordUnmapper
import org.springframework.stereotype.Repository
import ru.ifmo.software_engineering.afterlife.database.Tables.SERIES_VALUES
import ru.ifmo.software_engineering.afterlife.database.tables.records.SeriesValuesRecord
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Series
import ru.ifmo.software_engineering.afterlife.quality_control.domain.SeriesValue

interface SeriesValueRepository {
    suspend fun save(seriesValue: SeriesValue, series: Series): SeriesValue
}

@Repository
class SeriesValueRepositoryImpl(
    private val dsl: DSLContext,
    private val seriesValueMapper: RecordMapper<SeriesValuesRecord, SeriesValue>,
    private val seriesValueUnmapper: RecordUnmapper<SeriesValue, SeriesValuesRecord>,
) : SeriesValueRepository {
    override suspend fun save(seriesValue: SeriesValue, series: Series): SeriesValue =
        this.dsl.insertInto(SERIES_VALUES)
            .set(
                this.seriesValueUnmapper.unmap(seriesValue).apply {
                    this.seriesId = series.id
                }
            )
            .returning()
            .fetchAsync()
            .await()
            .map(this.seriesValueMapper)
            .first()
}
