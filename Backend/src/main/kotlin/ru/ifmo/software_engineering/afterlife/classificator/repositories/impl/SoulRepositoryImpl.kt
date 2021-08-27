package ru.ifmo.software_engineering.afterlife.classificator.repositories.impl

import kotlinx.coroutines.future.await
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import ru.ifmo.software_engineering.afterlife.classificator.database.mappers.ReportedSoulMapper
import ru.ifmo.software_engineering.afterlife.classificator.database.mappers.SoulMapper
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoul
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.classificator.repositories.SoulRepository
import ru.ifmo.software_engineering.afterlife.database.tables.GoodnessReports
import ru.ifmo.software_engineering.afterlife.database.tables.SinsReports
import ru.ifmo.software_engineering.afterlife.database.tables.Souls
import java.time.LocalDateTime
import java.time.ZoneId

@Repository
class SoulRepositoryImpl(
    private val dsl: DSLContext,
    private val soulMapper: SoulMapper,
    private val reportedSoulMapper: ReportedSoulMapper
) : SoulRepository {
    override suspend fun insertOne(soul: Soul): Soul {
        return this.dsl.insertInto(Souls.SOULS)
            .columns(Souls.SOULS.FIRST_NAME, Souls.SOULS.LAST_NAME, Souls.SOULS.DATE_OF_DEATH)
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
        return this.dsl.select().from(Souls.SOULS)
            .leftJoin(SinsReports.SINS_REPORTS)
            .on(SinsReports.SINS_REPORTS.SOUL_ID.eq(Souls.SOULS.ID))
            .leftJoin(GoodnessReports.GOODNESS_REPORTS)
            .on(GoodnessReports.GOODNESS_REPORTS.SOUL_ID.eq(Souls.SOULS.ID))
            .fetchAsync()
            .await()
            .map {
                Triple(
                    it.into(Souls.SOULS),
                    if (it[SinsReports.SINS_REPORTS.ID] != null)
                        it.into(SinsReports.SINS_REPORTS)
                    else null,
                    if (it[GoodnessReports.GOODNESS_REPORTS.ID] != null)
                        it.into(GoodnessReports.GOODNESS_REPORTS)
                    else null
                )
            }
            .map { reportedSoulMapper.map(it) }
    }
}
