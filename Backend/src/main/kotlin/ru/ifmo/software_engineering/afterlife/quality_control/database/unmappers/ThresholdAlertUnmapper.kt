package ru.ifmo.software_engineering.afterlife.quality_control.database.unmappers

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.database.tables.records.ThresholdAlertsRecord
import ru.ifmo.software_engineering.afterlife.quality_control.domain.ThresholdAlert

@Component
class ThresholdAlertUnmapper : RecordUnmapper<ThresholdAlert, ThresholdAlertsRecord> {
    override fun unmap(alert: ThresholdAlert?): ThresholdAlertsRecord =
        alert!!.let {
            ThresholdAlertsRecord().apply {
                this.measurementId = it.measurementId
                this.text = it.text
            }
        }
}