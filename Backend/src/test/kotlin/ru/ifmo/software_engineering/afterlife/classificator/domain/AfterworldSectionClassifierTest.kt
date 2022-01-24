package ru.ifmo.software_engineering.afterlife.classificator.domain

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import java.time.ZonedDateTime
import kotlin.test.assertEquals

class AfterworldSectionClassifierTest : Spek({
    Feature("AfterworldSectionClassifierTest.classifyAfterworldSection") {
        data class TestInput(
                val goodnessEvidences: List<GoodnessEvidence>,
                val sinEvidences: List<SinEvidence>
        )

        fun generateSinEvidence(isAtoned: Boolean, vararg sinKind: SinKind): List<SinEvidence> {
            val atonedAt = if (isAtoned) ZonedDateTime.now() else null
            return sinKind.map {
                SinEvidence(0, it, ZonedDateTime.now(), atonedAt)
            }
        }

        fun generateGoodnessEvidences(vararg goodnessKinds: GoodnessKind): List<GoodnessEvidence> =
                goodnessKinds.map { GoodnessEvidence(0, it, ZonedDateTime.now()) }

        val testData = listOf(
                Pair(TestInput(listOf(), listOf()), ParadiseSphere(1)),
                Pair(
                        TestInput(listOf(), generateSinEvidence(true, SinKind.GLUTTONY)),
                        ParadiseSphere(1)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.REFORMISM, GoodnessKind.BREAKING_VOW_BY_ELSE),
                                generateSinEvidence( true, SinKind.GLUTTONY)),
                        ParadiseSphere(1)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.INNOCENT_VICTIM),
                                generateSinEvidence( true, SinKind.GLUTTONY)),
                        ParadiseSphere(2)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.REFORMISM),
                                generateSinEvidence( true, SinKind.GLUTTONY)),
                        ParadiseSphere(2)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.AMBITION),
                                generateSinEvidence( true, SinKind.GLUTTONY)),
                        ParadiseSphere(2)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.LOVE, GoodnessKind.AMBITION),
                                generateSinEvidence( true, SinKind.GLUTTONY)),
                        ParadiseSphere(3)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.WISDOM, GoodnessKind.LOVE),
                                generateSinEvidence( true, SinKind.GLUTTONY)),
                        ParadiseSphere(4)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.SCHOLARSHIP, GoodnessKind.LOVE),
                                generateSinEvidence( true, SinKind.GLUTTONY)),
                        ParadiseSphere(4)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.WAR_FOR_FAITH, GoodnessKind.SCHOLARSHIP),
                                generateSinEvidence( true, SinKind.GLUTTONY)),
                        ParadiseSphere(5)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.FAIR_GOVERNMENT, GoodnessKind.WAR_FOR_FAITH),
                                generateSinEvidence( true, SinKind.GLUTTONY)),
                        ParadiseSphere(6)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.THEOLOGY, GoodnessKind.FAIR_GOVERNMENT),
                                generateSinEvidence( true, SinKind.GLUTTONY)),
                        ParadiseSphere(7)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.MONASTICISM, GoodnessKind.FAIR_GOVERNMENT),
                                generateSinEvidence( true, SinKind.GLUTTONY)),
                        ParadiseSphere(7)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.TRIUMPH, GoodnessKind.MONASTICISM),
                                generateSinEvidence( true, SinKind.GLUTTONY)),
                        ParadiseSphere(8)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.HOLINESS, GoodnessKind.TRIUMPH),
                                generateSinEvidence( true, SinKind.GLUTTONY)),
                        ParadiseSphere(9)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.DIVINITY, GoodnessKind.HOLINESS),
                                generateSinEvidence( true, SinKind.GLUTTONY)),
                        ParadiseSphere(10)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.DIVINITY, GoodnessKind.HOLINESS),
                                generateSinEvidence( false, SinKind.UNBAPTIZED)),
                        HellCircle(1)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.DIVINITY, GoodnessKind.HOLINESS),
                                generateSinEvidence( false, SinKind.VOLUPTUOUSNESS, SinKind.UNBAPTIZED)),
                        HellCircle(2)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.DIVINITY, GoodnessKind.HOLINESS),
                                generateSinEvidence( false, SinKind.GLUTTONY, SinKind.VOLUPTUOUSNESS)),
                        HellCircle(3)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.DIVINITY, GoodnessKind.HOLINESS),
                                generateSinEvidence( false, SinKind.WASTEFULNESS, SinKind.GLUTTONY)),
                        HellCircle(4)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.DIVINITY, GoodnessKind.HOLINESS),
                                generateSinEvidence( false, SinKind.AVARICE, SinKind.WASTEFULNESS)),
                        HellCircle(4)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.DIVINITY, GoodnessKind.HOLINESS),
                                generateSinEvidence( false, SinKind.DESPONDENCY, SinKind.WASTEFULNESS)),
                        HellCircle(5)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.DIVINITY, GoodnessKind.HOLINESS),
                                generateSinEvidence( false, SinKind.PRIDE, SinKind.WASTEFULNESS)),
                        HellCircle(5)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.DIVINITY, GoodnessKind.HOLINESS),
                                generateSinEvidence( false, SinKind.HERETICNESS, SinKind.PRIDE)),
                        HellCircle(6)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.DIVINITY, GoodnessKind.HOLINESS),
                                generateSinEvidence( false, SinKind.FALSE_TEACHING, SinKind.PRIDE)),
                        HellCircle(6)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.DIVINITY, GoodnessKind.HOLINESS),
                                generateSinEvidence( false, SinKind.VIOLENCE, SinKind.FALSE_TEACHING)),
                        HellCircle(7)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.DIVINITY, GoodnessKind.HOLINESS),
                                generateSinEvidence( false, SinKind.DECEPTION_WHO_NOT_TRUST, SinKind.VIOLENCE)),
                        HellCircle(8)),
                Pair(
                        TestInput(
                                generateGoodnessEvidences(GoodnessKind.DIVINITY, GoodnessKind.HOLINESS),
                                generateSinEvidence( false, SinKind.DECEPTION_WHO_TRUST, SinKind.VIOLENCE)),
                        HellCircle(9)),
        )

        testData.forEach {
            Scenario("Soul has sins: ${it.first.sinEvidences} and goodness evidences: ${it.first.goodnessEvidences}") {
                val classifier = AfterworldSectionClassifierImpl()
                var result: AfterworldSection? = null
                When("Classifying afterworld section") {
                    result = classifier.classifyAfterworldSection(it.first.sinEvidences, it.first.goodnessEvidences)
                }

                Then("Result should be ${it.second}") {
                    assertEquals(it.second, result)
                }
            }
        }
    }
})