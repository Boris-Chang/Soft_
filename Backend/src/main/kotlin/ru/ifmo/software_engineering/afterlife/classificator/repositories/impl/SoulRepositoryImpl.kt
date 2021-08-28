package ru.ifmo.software_engineering.afterlife.classificator.repositories.impl

import kotlinx.coroutines.future.await
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import ru.ifmo.software_engineering.afterlife.classificator.database.mappers.ReportedSoulMapper
import ru.ifmo.software_engineering.afterlife.classificator.database.mappers.SoulMapper
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoul
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.classificator.repositories.SoulRepository
import ru.ifmo.software_engineering.afterlife.database.tables.GoodnessEvidences.GOODNESS_EVIDENCES
import ru.ifmo.software_engineering.afterlife.database.tables.GoodnessReports.GOODNESS_REPORTS
import ru.ifmo.software_engineering.afterlife.database.tables.SinsReports.SINS_REPORTS
import ru.ifmo.software_engineering.afterlife.database.tables.Souls.SOULS
import ru.ifmo.software_engineering.afterlife.database.tables.SinEvidences.SIN_EVIDENCES
import java.time.LocalDateTime
import java.time.ZoneId

@Repository
class SoulRepositoryImpl(
    private val dsl: DSLContext,
    private val soulMapper: SoulMapper,
    private val reportedSoulMapper: ReportedSoulMapper
) : SoulRepository {
    override suspend fun insertOne(soul: Soul): Soul {
        return this.dsl.insertInto(SOULS)
            .columns(SOULS.FIRST_NAME, SOULS.LAST_NAME, SOULS.DATE_OF_DEATH)
            .values(
                soul.firstName,
                soul.lastName,
                LocalDateTime.ofInstant(soul.dateOfDeath.toInstant(), ZoneId.of("UTC"))
            )
            .returning()
            .fetchAsync()
            .thenApply { it.map(this.soulMapper).first() }
            .await()
    }

    override suspend fun getReportedSouls(): List<ReportedSoul> {
        return this.dsl.select().from(SOULS)
            .leftJoin(SINS_REPORTS).on(SINS_REPORTS.SOUL_ID.eq(SOULS.ID))
            .leftJoin(GOODNESS_REPORTS).on(GOODNESS_REPORTS.SOUL_ID.eq(SOULS.ID))
            .leftJoin(SIN_EVIDENCES).on(SIN_EVIDENCES.SINNED_BY_SOUL_ID.eq(SOULS.ID))
            .leftJoin(GOODNESS_EVIDENCES).on(GOODNESS_EVIDENCES.DONE_BY_SOUL_ID.eq(SOULS.ID))
            .fetchAsync().await()
            .intoGroups{
                Triple(
                    it.into(SOULS),
                    if (it[SINS_REPORTS.ID] != null)
                        it.into(SINS_REPORTS)
                    else null,
                    if (it[GOODNESS_REPORTS.ID] != null)
                        it.into(GOODNESS_REPORTS)
                    else null
                )
            }
            .map { reportedSoulMapper.map(it) }
    }
}
