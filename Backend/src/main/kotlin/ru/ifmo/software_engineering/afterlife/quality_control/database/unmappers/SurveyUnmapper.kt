package ru.ifmo.software_engineering.afterlife.quality_control.database.unmappers

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.database.enums.SurveysAddresses
import ru.ifmo.software_engineering.afterlife.database.tables.records.SurveysRecord
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Survey
import ru.ifmo.software_engineering.afterlife.quality_control.domain.SurveyAddressee

@Component
class SurveyUnmapper : RecordUnmapper<Survey, SurveysRecord> {
    override fun unmap(model: Survey?): SurveysRecord =
        SurveysRecord().apply {
            this.title = model!!.title
            this.url = model.url
            this.addressee = mapAddressee(model.addressee)
        }

    private fun mapAddressee(addressee: SurveyAddressee): SurveysAddresses = when (addressee) {
        SurveyAddressee.TORTURERS -> SurveysAddresses.TORTURERS
        SurveyAddressee.SINNERS -> SurveysAddresses.SINNERS
    }
}
