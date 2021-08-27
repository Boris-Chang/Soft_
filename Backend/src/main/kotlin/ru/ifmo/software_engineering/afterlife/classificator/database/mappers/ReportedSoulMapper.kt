package ru.ifmo.software_engineering.afterlife.classificator.database.mappers

import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessReport
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoul
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinsReport
import ru.ifmo.software_engineering.afterlife.database.tables.records.GoodnessReportsRecord
import ru.ifmo.software_engineering.afterlife.database.tables.records.SinsReportsRecord
import ru.ifmo.software_engineering.afterlife.database.tables.records.SoulsRecord
import ru.ifmo.software_engineering.afterlife.users.domain.User
import java.time.ZoneOffset
import java.util.*

@Component
class ReportedSoulMapper(
    private val soulMapper: SoulMapper
)  {
    fun map(records: Triple<SoulsRecord, SinsReportsRecord?, GoodnessReportsRecord?>): ReportedSoul {
        val soul = this.soulMapper.map(records.first)!!
        val sinsReportRecord = records.second
        val goodnessReportRecord = records.third
        return ReportedSoul(
            soul,
            if (sinsReportRecord != null)
                SinsReport(
                    sinsReportRecord.id,
                    soul,
                    emptyList(),
                    User(1, "Admin"),
                    Date.from(sinsReportRecord.uploadedAt.toInstant(ZoneOffset.UTC))
                )
            else null,
            if (goodnessReportRecord != null)
                GoodnessReport(
                    goodnessReportRecord.id,
                    soul,
                    emptyList(),
                    User(1, "Admin"),
                    Date.from(goodnessReportRecord.uploadedAt.toInstant(ZoneOffset.UTC))
                )
            else null,
        )
    }
}