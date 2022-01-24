package ru.ifmo.software_engineering.afterlife.classificator.domain

data class HellCircle(
    val circleNumber: Int,
) : AfterworldSection {
    override val afterwoldKind: AfterworldKind = AfterworldKind.HELL
    override val sectionIndex = circleNumber
}
