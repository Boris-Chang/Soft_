package ru.ifmo.software_engineering.afterlife.classificator.domain

enum class AfterworldKind {
    HELL,
    PARADISE
}
interface AfterworldSection {
    val afterwoldKind: AfterworldKind
}
