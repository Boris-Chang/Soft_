package ru.ifmo.software_engineering.afterlife.classificator.domain

data class ReportedSoul(
    val soul: Soul,
    val sinsReport: SinsReport?,
    val goodnessReport: GoodnessReport?,
) {
    val lastUpdate =
        when {
            this.sinsReport == null && this.goodnessReport == null ->
                soul.dateOfDeath
            this.sinsReport == null ->
                this.goodnessReport!!.uploadedAt
            this.goodnessReport == null ->
                this.sinsReport.uploadedAt
            else ->
                minOf(this.goodnessReport.uploadedAt, this.sinsReport.uploadedAt)
        }
}
