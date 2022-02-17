package ru.ifmo.software_engineering.afterlife.classificator.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class ArguedClassifierDecision(
        val godChangedDecision: AfterworldSection?,
        val devilChangedDecision: AfterworldSection?,
        val isArguedByGod: Boolean,
        val isArguedByDevil: Boolean) {

    @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val decisionText: String? get() {
        if (!isArguedByDevil && !isArguedByGod) return null
        if (changedDecision != null) {
            return "Решение классификатора изменено на ${formatSection(changedDecision)}"
        }

        return when {
            godDecisionText != null && devilDecisionText != null -> "$godDecisionText, $devilDecisionText"
            godDecisionText == null && devilDecisionText != null -> "$devilDecisionText"
            godDecisionText != null && devilDecisionText == null -> "$godDecisionText"
            else -> null
        }
    }

    private val changedDecision: AfterworldSection? =
            if (godChangedDecision != null && devilChangedDecision == godChangedDecision)
                godChangedDecision
            else null

    private val godDecisionText = when {
        isArguedByGod && godChangedDecision == null -> "Бог пометил решение как спорное"
        isArguedByGod && godChangedDecision != null ->
            "Бог изменил решение на ${formatSection(godChangedDecision)}"
        else -> null
    }

    private val devilDecisionText = when {
        isArguedByDevil && devilChangedDecision == null -> "Дьявол пометил решение как спорное"
        isArguedByDevil && devilChangedDecision != null ->
            "Дьявол изменил решение на ${formatSection(devilChangedDecision)}"
        else -> null
    }

    fun asChangedDecisionByGod(changedSection: AfterworldSection) =
            this.asArguedByGod().copy(godChangedDecision = changedSection)

    fun asChangedDecisionByDevil(changedSection: AfterworldSection) =
            this.asArguedByDevil().copy(devilChangedDecision = changedSection)

    fun asArguedByGod() =
            this.copy(isArguedByGod = true, godChangedDecision = null)

    fun asArguedByDevil() =
            this.copy(isArguedByDevil = true, devilChangedDecision = null)

    private fun formatSection(afterworldSection: AfterworldSection) =
            when (afterworldSection.afterwoldKind) {
                AfterworldKind.HELL -> "Круг ада №${afterworldSection.sectionIndex}"
                AfterworldKind.PARADISE -> "Небо рая №${afterworldSection.sectionIndex}"
            }
}