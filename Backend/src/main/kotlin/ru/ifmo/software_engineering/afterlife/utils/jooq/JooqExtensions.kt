package ru.ifmo.software_engineering.afterlife.utils.jooq

import org.jooq.Record
import org.jooq.SelectLimitStep
import ru.ifmo.software_engineering.afterlife.core.models.PageRequest

fun<R : Record> SelectLimitStep<R>.paged(pageRequest: PageRequest) =
    this.limit(pageRequest.pageSize)
        .offset(pageRequest.pageNumber * pageRequest.pageSize)