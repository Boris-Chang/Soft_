package ru.ifmo.software_engineering.afterlife.classificator.domain

import java.time.ZonedDateTime

enum class SinKind {
    //  Некрещенный
    UNBAPTIZED,
    //  Сладострастие
    VOLUPTUOUSNESS,
    //  Обжорство
    GLUTTONY,
    //  Расточительство
    WASTEFULNESS,
    //  Скупость
    AVARICE,
    //  Горделивость
    PRIDE,
    //  Еретичность
    HERETICNESS,
    //  Лжеучительство
    FALSE_TEACHING,
    //  Насилие
    VIOLENCE,
    //  Обман не доверившихся
    DECEPTION_WHO_NOT_TRUST,
    //  Обман доверившихся
    DECEPTION_WHO_TRUST,
}

data class SinEvidence(
    val id: Long,
    val kind: SinKind,
    val dateOfSin: ZonedDateTime,
    val attonedAt: ZonedDateTime? = null,
)
