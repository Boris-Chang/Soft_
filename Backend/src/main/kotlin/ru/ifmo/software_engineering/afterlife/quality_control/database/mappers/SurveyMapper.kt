package ru.ifmo.software_engineering.afterlife.quality_control.database.mappers

import arrow.core.computations.nullable
import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.database.enums.SurveysAddresses
import ru.ifmo.software_engineering.afterlife.database.tables.records.SurveysRecord
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Survey
import ru.ifmo.software_engineering.afterlife.quality_control.domain.SurveyAddressee

@Component
class SurveyMapper : RecordMapper<SurveysRecord, Survey> {
    override fun map(nullableRecord: SurveysRecord?): Survey? = nullable.eager {
        val record = nullableRecord.bind()
        record.id.bind()

        Survey(
            record.id,
            record.title,
            record.url,
            mapAddressee(record.addressee)
        )
    }

    private fun mapAddressee(addressee: SurveysAddresses): SurveyAddressee =
        when (addressee) {
            SurveysAddresses.SINNERS -> SurveyAddressee.SINNERS
            SurveysAddresses.TORTURERS -> SurveyAddressee.TORTURERS
        }
}
