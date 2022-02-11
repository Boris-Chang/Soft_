package ru.ifmo.software_engineering.afterlife.classificator.services

import arrow.core.*
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.mockito.kotlin.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import ru.ifmo.software_engineering.afterlife.classificator.domain.*
import ru.ifmo.software_engineering.afterlife.classificator.report_readers.csv.CsvParseException
import ru.ifmo.software_engineering.afterlife.classificator.report_readers.csv.CsvParser
import ru.ifmo.software_engineering.afterlife.classificator.repositories.GoodnessReportRepository
import ru.ifmo.software_engineering.afterlife.classificator.repositories.SinsReportRepository
import ru.ifmo.software_engineering.afterlife.classificator.repositories.SoulRepository
import ru.ifmo.software_engineering.afterlife.core.exceptions.*
import ru.ifmo.software_engineering.afterlife.security.IdentityImpl
import ru.ifmo.software_engineering.afterlife.security.Principal
import ru.ifmo.software_engineering.afterlife.security.services.AuthorizationService
import java.io.InputStream
import java.time.ZonedDateTime
import java.util.*
import kotlin.test.assertEquals

class SoulReportsServiceTest : Spek({
    val soulRepository: SoulRepository = mock()
    val sinsReportRepository: SinsReportRepository = mock()
    val goodnessReportRepository: GoodnessReportRepository = mock()
    val sinsEvidencesCsvParser: CsvParser<SinEvidence> = mock()
    val goodnessEvidencesCsvParser: CsvParser<GoodnessEvidence> = mock()
    val authorizationService: AuthorizationService = mock()
    val soulClassifierService: SoulClassifierService = mock()

    val service = SoulReportsServiceImpl(
            soulRepository,
            sinsReportRepository,
            goodnessReportRepository,
            sinsEvidencesCsvParser,
            goodnessEvidencesCsvParser,
            authorizationService,
            soulClassifierService)

    val soulId = Random(ZonedDateTime.now().toEpochSecond()).nextLong()
    val streamToUpload = mock<InputStream>()

    Feature("SoulReportsService.saveOrUpdateSinsReportForSoulFromCsv()") {
        var actualResult: Either<ApplicationException, SinsReport>? = null

        Scenario("user is not authorized") {
            Given("Current user is null") {
                authorizationService.stub {
                    onBlocking { getCurrentUser() } doReturn null
                }
            }

            When("Uploading sins report") {
                actualResult = runBlocking {
                    service.saveOrUpdateSinsReportForSoulFromCsv(soulId, streamToUpload)
                }
            }

            Then("Result should be invalid"){
                assert(actualResult!!.isLeft())
            }

            And("Result should has unauthorized error") {
                val error = actualResult!!.swap().getOrElse { null }
                assertThat(error, instanceOf(UnauthorizedException::class.java))
            }
        }

        Scenario("user is not Heaven prosecutor") {
            Given("Current user is not Heaven prosecutor") {
                authorizationService.stub {
                    onBlocking { getCurrentUser() } doReturn Principal(
                            IdentityImpl("", ""), listOf("HEAVEN_ADVOCATE"))
                }
            }

            When("Uploading sins report") {
                actualResult = runBlocking {
                    service.saveOrUpdateSinsReportForSoulFromCsv(soulId, streamToUpload)
                }
            }

            Then("Result should be invalid"){
                assert(actualResult!!.isLeft())
            }

            And("Result should has forbidden error") {
                val error = actualResult!!.swap().getOrElse { null }
                assertThat(error, instanceOf(ForbiddenException::class.java))
            }
        }

        Scenario("User is Heaven Prosecutor but soul not exist") {
            Given("Current user is Heaven prosecutor") {
                authorizationService.stub {
                    onBlocking { getCurrentUser() } doReturn Principal(
                            IdentityImpl("", ""), listOf("HEAVEN_PROSECUTOR"))
                }
            }

            When("Uploading sins report") {
                actualResult = runBlocking {
                    service.saveOrUpdateSinsReportForSoulFromCsv(soulId, streamToUpload)
                }
            }

            Then("Result should be invalid"){
                assert(actualResult!!.isLeft())
            }

            And("Result should has NotFound error") {
                val error = actualResult!!.swap().getOrElse { null }
                assertThat(error, instanceOf(NotFoundException::class.java))
            }
        }

        val existingSoul = Soul(soulId, "Test user", "Test user", ZonedDateTime.now(), null)
        Scenario("User is Heaven Prosecutor, soul exist, but report is not valid") {
            Given("Current user is Heaven prosecutor") {
                authorizationService.stub {
                    onBlocking { getCurrentUser() } doReturn Principal(
                            IdentityImpl("", ""), listOf("HEAVEN_PROSECUTOR"))
                }
            }

            And("Soul exist") {
                soulRepository.stub {
                    onBlocking { findById(existingSoul.id) } doReturn existingSoul
                }
            }

            And("Report parser returned error") {
                sinsEvidencesCsvParser.stub {
                    on { parseCsv(isA()) } doReturn CsvParseException("", 1, 1).invalid()
                }
            }

            When("Uploading sins report") {
                actualResult = runBlocking {
                    service.saveOrUpdateSinsReportForSoulFromCsv(soulId, streamToUpload)
                }
            }

            Then("Result should be invalid"){
                assert(actualResult!!.isLeft())
            }

            And("Result should has BadRequest error") {
                val error = actualResult!!.swap().getOrElse { null }
                assertThat(error, instanceOf(BadRequestException::class.java))
            }
        }

        val validParsedSins = mock<List<SinEvidence>>()
        Scenario("User is Heaven Prosecutor, soul exist, report is valid and was already loaded") {
            Given("Current user is Heaven prosecutor") {
                authorizationService.stub {
                    onBlocking { getCurrentUser() } doReturn Principal(
                            IdentityImpl("", ""), listOf("HEAVEN_PROSECUTOR"))
                }
            }

            And("Soul exist") {
                soulRepository.stub {
                    onBlocking { findById(existingSoul.id) } doReturn existingSoul
                }
            }

            And("Report parser did not return error") {
                sinsEvidencesCsvParser.stub {
                    on { parseCsv(isA()) } doReturn validParsedSins.valid()
                }
            }

            And("Sins report for soul already exist") {
                sinsReportRepository.stub {
                    onBlocking { findBySoul(existingSoul) } doReturn mock()
                }
            }

            val expectedResult = mock<SinsReport>()
            And("Update report returned successful result") {
                sinsReportRepository.stub {
                    onBlocking { update(anyOrNull()) }.doReturn(expectedResult)
                }
            }

            When("Uploading sins report") {
                actualResult = runBlocking {
                    service.saveOrUpdateSinsReportForSoulFromCsv(soulId, streamToUpload)
                }
            }

            Then("Result should be valid"){
                assert(actualResult!!.isRight())
            }

            And("Result should have updated result") {
                val returnedReport = actualResult!!.getOrElse { null }
                assertEquals(expectedResult, returnedReport)
            }

            And("Soul should be classified if required") {
                verifyBlocking(soulClassifierService) {
                    this.classifySoulIfRequired(existingSoul)
                }
            }
        }

        Scenario("User is Heaven Prosecutor, soul exist, report is valid and was never loaded") {
            Given("Current user is Heaven prosecutor") {
                clearInvocations(soulClassifierService)
                authorizationService.stub {
                    onBlocking { getCurrentUser() } doReturn Principal(
                            IdentityImpl("", ""), listOf("HEAVEN_PROSECUTOR"))
                }
            }

            And("Soul exist") {
                soulRepository.stub {
                    onBlocking { findById(existingSoul.id) } doReturn existingSoul
                }
            }

            And("Report parser did not return error") {
                sinsEvidencesCsvParser.stub {
                    on { parseCsv(isA()) } doReturn validParsedSins.valid()
                }
            }

            And("Sins report for soul not exist") {
                sinsReportRepository.stub {
                    onBlocking { findBySoul(existingSoul) } doReturn null
                }
            }

            val expectedResult = mock<SinsReport>()
            And("Update report returned successful result") {
                sinsReportRepository.stub {
                    onBlocking { save(anyOrNull()) }.doReturn(expectedResult)
                }
            }

            When("Uploading sins report") {
                actualResult = runBlocking {
                    service.saveOrUpdateSinsReportForSoulFromCsv(soulId, streamToUpload)
                }
            }

            Then("Result should be valid"){
                assert(actualResult!!.isRight())
            }

            And("Result should have updated result") {
                val returnedReport = actualResult!!.getOrElse { null }
                assertEquals(expectedResult, returnedReport)
            }

            And("Soul should be classified if required") {
                verifyBlocking(soulClassifierService) {
                    this.classifySoulIfRequired(existingSoul)
                }
            }
        }
    }

    Feature("SoulReportsService.saveOrUpdateGoodnessReportForSoulFromCsv()") {
        var actualResult: Either<ApplicationException, GoodnessReport>? = null

        Scenario("user is not authorized") {
            Given("Current user is null") {
                authorizationService.stub {
                    onBlocking { getCurrentUser() } doReturn null
                }
            }

            When("Uploading goodness report") {
                actualResult = runBlocking {
                    service.saveOrUpdateGoodnessReportForSoulFromCsv(soulId, streamToUpload)
                }
            }

            Then("Result should be invalid"){
                assert(actualResult!!.isLeft())
            }

            And("Result should has unauthorized error") {
                val error = actualResult!!.swap().getOrElse { null }
                assertThat(error, instanceOf(UnauthorizedException::class.java))
            }
        }

        Scenario("user is not Heaven prosecutor") {
            Given("Current user is not Heaven advocate") {
                authorizationService.stub {
                    onBlocking { getCurrentUser() } doReturn Principal(
                            IdentityImpl("", ""), listOf("HEAVEN_PROSECUTOR"))
                }
            }

            When("Uploading goodness report") {
                actualResult = runBlocking {
                    service.saveOrUpdateGoodnessReportForSoulFromCsv(soulId, streamToUpload)
                }
            }

            Then("Result should be invalid"){
                assert(actualResult!!.isLeft())
            }

            And("Result should has forbidden error") {
                val error = actualResult!!.swap().getOrElse { null }
                assertThat(error, instanceOf(ForbiddenException::class.java))
            }
        }

        Scenario("User is Heaven Prosecutor but soul not exist") {
            Given("Current user is Heaven advocate") {
                authorizationService.stub {
                    onBlocking { getCurrentUser() } doReturn Principal(
                            IdentityImpl("", ""), listOf("HEAVEN_ADVOCATE"))
                }
            }

            And("Soul not exist") {
                soulRepository.stub {
                    onBlocking { findById(soulId) } doReturn null
                }
            }

            When("Uploading goodness report") {
                actualResult = runBlocking {
                    service.saveOrUpdateGoodnessReportForSoulFromCsv(soulId, streamToUpload)
                }
            }

            Then("Result should be invalid"){
                assert(actualResult!!.isLeft())
            }

            And("Result should has NotFound error") {
                val error = actualResult!!.swap().getOrElse { null }
                assertThat(error, instanceOf(NotFoundException::class.java))
            }
        }

        val existingSoul = Soul(soulId, "Test user", "Test user", ZonedDateTime.now(), null)
        Scenario("User is Heaven Prosecutor, soul exist, but report is not valid") {
            Given("Current user is Heaven advocate") {
                authorizationService.stub {
                    onBlocking { getCurrentUser() } doReturn Principal(
                            IdentityImpl("", ""), listOf("HEAVEN_ADVOCATE"))
                }
            }

            And("Soul exist") {
                soulRepository.stub {
                    onBlocking { findById(existingSoul.id) } doReturn existingSoul
                }
            }

            And("Report parser returned error") {
                goodnessEvidencesCsvParser.stub {
                    on { parseCsv(isA()) } doReturn CsvParseException("", 1, 1).invalid()
                }
            }

            When("Uploading goodness report") {
                actualResult = runBlocking {
                    service.saveOrUpdateGoodnessReportForSoulFromCsv(soulId, streamToUpload)
                }
            }

            Then("Result should be invalid"){
                assert(actualResult!!.isLeft())
            }

            And("Result should has BadRequest error") {
                val error = actualResult!!.swap().getOrElse { null }
                assertThat(error, instanceOf(BadRequestException::class.java))
            }
        }

        val validParsedGoodness = mock<List<GoodnessEvidence>>()
        Scenario("User is Heaven Prosecutor, soul exist, report is valid and was already loaded") {
            Given("Current user is Heaven advocate") {
                authorizationService.stub {
                    onBlocking { getCurrentUser() } doReturn Principal(
                            IdentityImpl("", ""), listOf("HEAVEN_ADVOCATE"))
                }
            }

            And("Soul exist") {
                soulRepository.stub {
                    onBlocking { findById(existingSoul.id) } doReturn existingSoul
                }
            }

            And("Report parser did not returned error") {
                goodnessEvidencesCsvParser.stub {
                    on { parseCsv(isA()) } doReturn validParsedGoodness.valid()
                }
            }

            And("Goodness report for soul already exist") {
                goodnessReportRepository.stub {
                    onBlocking { findBySoul(existingSoul) } doReturn mock()
                }
            }

            val expectedResult = mock<GoodnessReport>()
            And("Update report returned successful result") {
                goodnessReportRepository.stub {
                    onBlocking { update(anyOrNull()) }.doReturn(expectedResult)
                }
            }

            When("Uploading goodness report") {
                actualResult = runBlocking {
                    service.saveOrUpdateGoodnessReportForSoulFromCsv(soulId, streamToUpload)
                }
            }

            Then("Result should be valid"){
                assert(actualResult!!.isRight())
            }

            And("Result should have updated result") {
                val returnedReport = actualResult!!.getOrElse { null }
                assertEquals(expectedResult, returnedReport)
            }

            And("Soul should be classified if required") {
                verifyBlocking(soulClassifierService) {
                    this.classifySoulIfRequired(existingSoul)
                }
            }
        }

        Scenario("User is Heaven Prosecutor, soul exist, report is valid and was never loaded") {
            Given("Current user is Heaven advocate") {
                clearInvocations(soulClassifierService)
                authorizationService.stub {
                    onBlocking { getCurrentUser() } doReturn Principal(
                            IdentityImpl("", ""), listOf("HEAVEN_ADVOCATE"))
                }
            }

            And("Soul exist") {
                soulRepository.stub {
                    onBlocking { findById(existingSoul.id) } doReturn existingSoul
                }
            }

            And("Report parser did not returned error") {
                goodnessEvidencesCsvParser.stub {
                    on { parseCsv(isA()) } doReturn validParsedGoodness.valid()
                }
            }

            And("Goodness report for soul not exist") {
                goodnessReportRepository.stub {
                    onBlocking { findBySoul(existingSoul) } doReturn null
                }
            }

            val expectedResult = mock<GoodnessReport>()
            And("Update report returned successful result") {
                goodnessReportRepository.stub {
                    onBlocking { save(anyOrNull()) }.doReturn(expectedResult)
                }
            }

            When("Uploading sins report") {
                actualResult = runBlocking {
                    service.saveOrUpdateGoodnessReportForSoulFromCsv(soulId, streamToUpload)
                }
            }

            Then("Result should be valid"){
                assert(actualResult!!.isRight())
            }

            And("Result should have updated result") {
                val returnedReport = actualResult!!.getOrElse { null }
                assertEquals(expectedResult, returnedReport)
            }

            And("Soul should be classified if required") {
                verifyBlocking(soulClassifierService) {
                    classifySoulIfRequired(existingSoul)
                }
            }
        }
    }
})