package ru.ifmo.software_engineering.afterlife.api.models

import ru.ifmo.software_engineering.afterlife.database.enums.AfterworldKind

data class ChangeClassifierDecisionRequest(
        val sectionIndex: Int,
        val afterworldKind: AfterworldKind
)