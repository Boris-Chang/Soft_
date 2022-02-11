package ru.ifmo.software_engineering.afterlife.classificator.database.jooq.domain

import org.jooq.Record
import org.jooq.SelectFromStep
import org.jooq.SelectOnConditionStep
import ru.ifmo.software_engineering.afterlife.database.Tables
import ru.ifmo.software_engineering.afterlife.database.tables.Souls

fun <T : Record> SelectFromStep<T>.fromSouls(): SelectOnConditionStep<T> =
    from(Souls.SOULS).leftJoin(Tables.SOUL_AFTERWORLD_LOCATION).on(Tables.SOUL_AFTERWORLD_LOCATION.SOUL_ID.eq(Souls.SOULS.ID))
