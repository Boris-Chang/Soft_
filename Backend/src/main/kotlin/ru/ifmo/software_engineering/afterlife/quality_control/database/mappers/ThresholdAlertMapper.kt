package ru.ifmo.software_engineering.afterlife.quality_control.database.mappers

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.database.tables.records.ThresholdAlertsRecord
import ru.ifmo.software_engineering.afterlife.quality_control.domain.ThresholdAlert

@Component
class ThresholdAlertMapper : RecordMapper<ThresholdAlertsRecord, ThresholdAlert>{
    override fun map(record: ThresholdAlertsRecord?): ThresholdAlert? =
        record?.takeIf { it.id != null }?.let {
            ThresholdAlert(it.id, it.measurementId, it.text)
        }

}