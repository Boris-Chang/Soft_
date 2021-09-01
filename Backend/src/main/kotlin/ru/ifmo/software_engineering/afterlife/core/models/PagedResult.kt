package ru.ifmo.software_engineering.afterlife.core.models

data class PagedResult<T> (
    val results: List<T>,
    val totalCount: Int,
    val pageNumber: Int,
)
