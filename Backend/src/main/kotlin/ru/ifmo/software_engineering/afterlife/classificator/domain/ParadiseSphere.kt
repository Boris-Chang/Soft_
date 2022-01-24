package ru.ifmo.software_engineering.afterlife.classificator.domain

data class ParadiseSphere(
    val sphereNumber: Int,
) : AfterworldSection {
    override val afterwoldKind: AfterworldKind =
        AfterworldKind.PARADISE
    override val sectionIndex = sphereNumber
}
