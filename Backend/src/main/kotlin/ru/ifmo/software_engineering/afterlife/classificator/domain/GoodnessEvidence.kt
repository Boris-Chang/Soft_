package ru.ifmo.software_engineering.afterlife.classificator.domain

import java.time.ZonedDateTime

enum class GoodnessKind {
    //  Нарушения обета по чужой вине
    BREAKING_VOW_BY_ELSE,
    // Невинно пострадавший
    INNOCENT_VICTIM,
    //  Реформаторство
    REFORMISM,
    //  Честолюбивая деятельность
    AMBITION,
    //  Влюбленность
    LOVE,
    //  Мудрость
    WISDOM,
    //  Ученность
    SCHOLARSHIP,
    //  Война за веру
    WAR_FOR_FAITH,
    //  Справедливое правление
    FAIR_GOVERNMENT,
    //  Богословие
    THEOLOGY,
    //  Монашество
    MONASTICISM,
    //  Торжествование
    TRIUMPH,
    //  Святость
    HOLINESS,
    //  Божественность, блаженность
    DIVINITY,
}

data class GoodnessEvidence(
    val id: Long,
    val kind: GoodnessKind,
    val dateOfGoodDeedEvidence: ZonedDateTime,
)
