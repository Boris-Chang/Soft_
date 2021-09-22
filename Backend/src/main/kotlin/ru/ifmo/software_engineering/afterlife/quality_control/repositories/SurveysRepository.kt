package ru.ifmo.software_engineering.afterlife.quality_control.repositories

import kotlinx.coroutines.future.await
import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.jooq.RecordUnmapper
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.software_engineering.afterlife.core.models.PageRequest
import ru.ifmo.software_engineering.afterlife.core.models.PagedResult
import ru.ifmo.software_engineering.afterlife.database.Tables.SURVEYS
import ru.ifmo.software_engineering.afterlife.database.tables.records.SurveysRecord
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Survey
import ru.ifmo.software_engineering.afterlife.utils.jooq.paged

interface SurveysRepository {
    suspend fun findById(id: Long): Survey?
    suspend fun findAll(pageRequest: PageRequest): PagedResult<Survey>
    suspend fun save(survey: Survey): Survey
    suspend fun update(survey: Survey): Survey
    suspend fun delete(survey: Survey)
}

@Repository
class SurveysRepositoryImpl(
    private val dsl: DSLContext,
    private val mapper: RecordMapper<SurveysRecord, Survey>,
    private val unmapper: RecordUnmapper<Survey, SurveysRecord>,
) : SurveysRepository {
    override suspend fun findById(id: Long): Survey? =
        this.dsl.select().from(SURVEYS)
            .where(SURVEYS.ID.eq(id))
            .fetchAsync()
            .await()
            .map { mapper.map(it.into(SURVEYS)) }
            .firstOrNull()

    @Transactional
    override suspend fun findAll(pageRequest: PageRequest): PagedResult<Survey> {
        val countField = DSL.count()
        val totalCount = this.dsl
            .select(countField).from(SURVEYS)
            .fetchAsync().await()
            .first().getValue(countField)

        val results = this.dsl.select().from(SURVEYS)
            .paged(pageRequest)
            .fetchAsync()
            .await()
            .map { this.mapper.map(it.into(SURVEYS)) }

        return PagedResult(results, totalCount, pageRequest.pageNumber)
    }

    override suspend fun save(survey: Survey): Survey =
        this.dsl.insertInto(SURVEYS)
            .set(this.unmapper.unmap(survey))
            .returning()
            .fetchAsync()
            .await()
            .map(this.mapper)
            .first()

    override suspend fun update(survey: Survey): Survey =
        this.dsl.update(SURVEYS)
            .set(this.unmapper.unmap(survey))
            .where(SURVEYS.ID.eq(survey.id))
            .returning()
            .fetchAsync()
            .await()
            .map(this.mapper)
            .first()

    override suspend fun delete(survey: Survey): Unit =
        this.dsl.delete(SURVEYS)
            .where(SURVEYS.ID.eq(survey.id))
            .executeAsync()
            .await()
            .let { }
}
