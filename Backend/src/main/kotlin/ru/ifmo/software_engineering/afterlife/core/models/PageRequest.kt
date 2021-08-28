package ru.ifmo.software_engineering.afterlife.core.models

data class PageRequest (
    val pageNumber: Int,
    val pageSize: Int = DEFAULT_PAGE_SIZE
) {
    companion object {
        const val DEFAULT_PAGE_SIZE = 100

        fun default(): PageRequest =
            PageRequest(0)
    }
}