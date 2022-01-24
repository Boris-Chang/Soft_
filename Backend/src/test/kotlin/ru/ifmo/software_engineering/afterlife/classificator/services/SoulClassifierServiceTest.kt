package ru.ifmo.software_engineering.afterlife.classificator.services

import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import ru.ifmo.software_engineering.afterlife.classificator.domain.*
import ru.ifmo.software_engineering.afterlife.classificator.repositories.AfterworldSectionRepository
import ru.ifmo.software_engineering.afterlife.classificator.repositories.GoodnessReportRepository
import ru.ifmo.software_engineering.afterlife.classificator.repositories.SinsReportRepository
import java.time.ZonedDateTime
import kotlin.test.assertEquals

object SoulClassifierServiceTest : Spek({
    val goodnessReportRepository: GoodnessReportRepository = mock()
    val sinsReportRepository: SinsReportRepository = mock()
    val afterworldSectionClassifier: AfterworldSectionClassifier = mock()
    val afterworldSectionRepository: AfterworldSectionRepository = mock()

    val service = SoulClassifierServiceImpl(
            goodnessReportRepository,
            sinsReportRepository,
            afterworldSectionClassifier,
            afterworldSectionRepository
    )

    Feature("SoulClassifierService.classifySoulIfRequired") {
        val soulToClassify = mock<Soul>()
        var classifyingResult: ReportedSoul? = null
        fun classifySoulIfRequired() {
            classifyingResult = runBlocking {
                service.classifySoulIfRequired(soulToClassify)
            }
        }

        listOf(
                Pair(null, null),
                Pair(mock<SinsReport>(), null),
                Pair(null, mock<GoodnessReport>()),
        ).forEach {
            val (sinsReport, goodnessReport) = it
            Scenario("Sins report is $sinsReport, but goodness report is $goodnessReport") {
                reset(afterworldSectionClassifier)
                reset(afterworldSectionRepository)
                Given("Sins Report is $sinsReport") {
                    sinsReportRepository.stub {
                        onBlocking {
                            findBySoul(soulToClassify)
                        } doReturn sinsReport
                    }
                }

                And("Goodness report is $goodnessReport") {
                    goodnessReportRepository.stub {
                        onBlocking {
                            findBySoul(soulToClassify)
                        } doReturn goodnessReport
                    }
                }

                When("Classifying soul") {
                    classifySoulIfRequired()
                }

                Then("Result should have provided soul and found reports") {
                    val expectedResult = ReportedSoul(soulToClassify, sinsReport, goodnessReport)
                    assertEquals(expectedResult, classifyingResult)
                }

                And("Classifier should not be called") {
                    verify(afterworldSectionClassifier, never())
                            .classifyAfterworldSection(any(), any())
                }

                And("Classifier repository should not be called") {
                    verifyBlocking(afterworldSectionRepository, never()) {
                        saveOrUpdateAfterworldSectionForSoul(any(), any())
                    }
                }
            }
        }

        Scenario("Both reports for soul exist") {
            val sinsReport = mock<SinsReport>().stub {
                on { it.uploadedAt } doReturn ZonedDateTime.now()
                on { it.sins } doReturn listOf()
            }
            val goodnessReport = mock<GoodnessReport>().stub {
                on { it.uploadedAt } doReturn ZonedDateTime.now()
                on { it.goodnessEvidences } doReturn listOf()
            }
            val classifiedSection = mock<AfterworldSection>()

            Given("Sins Report exist") {
                reset(afterworldSectionClassifier)
                reset(afterworldSectionRepository)
                sinsReportRepository.stub {
                    onBlocking {
                        findBySoul(soulToClassify)
                    } doReturn sinsReport
                }
            }

            And("Goodness report exist") {
                goodnessReportRepository.stub {
                    onBlocking {
                        findBySoul(soulToClassify)
                    } doReturn goodnessReport
                }
            }

            And("Classifier returned successful result") {
                afterworldSectionClassifier.stub {
                    on {
                        classifyAfterworldSection(sinsReport.sins, goodnessReport.goodnessEvidences)
                    } doReturn classifiedSection
                }
            }

            When("Classifying soul") {
                classifySoulIfRequired()
            }

            Then("Result should have provided soul and found reports") {
                val expectedResult = ReportedSoul(soulToClassify, sinsReport, goodnessReport)
                assertEquals(expectedResult, classifyingResult)
            }

            And("Classifier should not be called for reports") {
                verify(afterworldSectionClassifier)
                        .classifyAfterworldSection(sinsReport.sins, goodnessReport.goodnessEvidences)
            }

            And("Classifier repository should be called") {
                verifyBlocking(afterworldSectionRepository) {
                    saveOrUpdateAfterworldSectionForSoul(soulToClassify, classifiedSection)
                }
            }
        }
    }
})