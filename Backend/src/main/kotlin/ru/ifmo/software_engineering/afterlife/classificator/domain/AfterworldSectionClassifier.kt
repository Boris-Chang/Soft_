package ru.ifmo.software_engineering.afterlife.classificator.domain

import org.springframework.stereotype.Component

interface AfterworldSectionClassifier {
    fun classifyAfterworldSection(sinEvidences: List<SinEvidence>, goodnessEvidences: List<GoodnessEvidence>): AfterworldSection
}

@Component
class AfterworldSectionClassifierImpl : AfterworldSectionClassifier {
    override fun classifyAfterworldSection(sinEvidences: List<SinEvidence>, goodnessEvidences: List<GoodnessEvidence>): AfterworldSection {
        val hellCircle = classifyHellCircleIfSinner(sinEvidences)
        if (hellCircle != null) {
            return hellCircle
        }

        return classifyParadiseSphere(goodnessEvidences)
    }

    private fun classifyHellCircleIfSinner(sinEvidences: List<SinEvidence>): HellCircle? {
        val notAtoned = sinEvidences.filter { it.attonedAt == null }

        if (notAtoned.any{ it.kind == SinKind.DECEPTION_WHO_TRUST }) {
            return HellCircle(9)
        }
        if (notAtoned.any{ it.kind == SinKind.DECEPTION_WHO_NOT_TRUST }) {
            return HellCircle(8)
        }
        if (notAtoned.any{ it.kind == SinKind.VIOLENCE }) {
            return HellCircle(7)
        }
        if (notAtoned.any{ it.kind == SinKind.FALSE_TEACHING || it.kind == SinKind.HERETICNESS }) {
            return HellCircle(6)
        }
        if (notAtoned.any{ it.kind == SinKind.PRIDE || it.kind == SinKind.DESPONDENCY }) {
            return HellCircle(5)
        }
        if (notAtoned.any{ it.kind == SinKind.AVARICE || it.kind == SinKind.WASTEFULNESS }) {
            return HellCircle(4)
        }
        if (notAtoned.any{ it.kind == SinKind.GLUTTONY }) {
            return HellCircle(3)
        }
        if (notAtoned.any{ it.kind == SinKind.VOLUPTUOUSNESS }) {
            return HellCircle(2)
        }
        if (notAtoned.any{ it.kind == SinKind.UNBAPTIZED }) {
            return HellCircle(1)
        }

        return null
    }

    private fun classifyParadiseSphere(goodnessEvidences: List<GoodnessEvidence>): ParadiseSphere {
        if (goodnessEvidences.any { it.kind == GoodnessKind.BREAKING_VOW_BY_ELSE }) {
            return ParadiseSphere(1)
        }
        if (goodnessEvidences.any { it.kind == GoodnessKind.DIVINITY }) {
            return ParadiseSphere(10)
        }
        if (goodnessEvidences.any { it.kind == GoodnessKind.HOLINESS }) {
            return ParadiseSphere(9)
        }
        if (goodnessEvidences.any { it.kind == GoodnessKind.TRIUMPH }) {
            return ParadiseSphere(8)
        }
        if (goodnessEvidences.any { it.kind == GoodnessKind.THEOLOGY || it.kind == GoodnessKind.MONASTICISM }) {
            return ParadiseSphere(7)
        }
        if (goodnessEvidences.any { it.kind == GoodnessKind.FAIR_GOVERNMENT }) {
            return ParadiseSphere(6)
        }
        if (goodnessEvidences.any { it.kind == GoodnessKind.WAR_FOR_FAITH }) {
            return ParadiseSphere(5)
        }
        if (goodnessEvidences.any { it.kind == GoodnessKind.WISDOM || it.kind == GoodnessKind.SCHOLARSHIP }) {
            return ParadiseSphere(4)
        }
        if (goodnessEvidences.any { it.kind == GoodnessKind.LOVE }) {
            return ParadiseSphere(3)
        }
        if (goodnessEvidences.any { it.kind == GoodnessKind.REFORMISM || it.kind == GoodnessKind.AMBITION ||
                        it.kind ==  GoodnessKind.INNOCENT_VICTIM }) {
            return ParadiseSphere(2)
        }

        return ParadiseSphere(1)
    }
}