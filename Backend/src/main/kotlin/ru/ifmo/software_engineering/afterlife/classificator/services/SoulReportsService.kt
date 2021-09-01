package ru.ifmo.software_engineering.afterlife.classificator.services

import arrow.core.Either
import arrow.core.Validated
import arrow.core.computations.either
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessEvidence
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessReport
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinEvidence
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinsReport
import ru.ifmo.software_engineering.afterlife.classificator.report_readers.csv.CsvParser
import ru.ifmo.software_engineering.afterlife.classificator.repositories.GoodnessReportRepository
import ru.ifmo.software_engineering.afterlife.classificator.repositories.SinsReportRepository
import ru.ifmo.software_engineering.afterlife.classificator.repositories.SoulRepository
import ru.ifmo.software_engineering.afterlife.core.exceptions.ApplicationException
import ru.ifmo.software_engineering.afterlife.core.exceptions.BadRequestException
import ru.ifmo.software_engineering.afterlife.core.exceptions.NotFoundException
import ru.ifmo.software_engineering.afterlife.users.domain.User
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.time.ZonedDateTime

interface SoulReportsService {
    suspend fun saveOrUpdateSinsReportForSoulFromCsv(soulId: Long, stream: InputStream): Either<ApplicationException, SinsReport>
    @Transactional
    suspend fun saveOrUpdateGoodnessReportForSoulFromCsv(
        soulId: Long,
        stream: InputStream
    ): Either<ApplicationException, GoodnessReport>
}

@Service
class SoulReportsServiceImpl(
    private val soulRepository: SoulRepository,
    private val sinsReportRepository: SinsReportRepository,
    private val goodnessReportRepository: GoodnessReportRepository,
    private val sinsEvidencesCsvParser: CsvParser<SinEvidence>,
    private val goodnessEvidencesCsvParser: CsvParser<GoodnessEvidence>
) : SoulReportsService {
    @Transactional
    override suspend fun saveOrUpdateSinsReportForSoulFromCsv(soulId: Long, stream: InputStream) =
        either<ApplicationException, SinsReport> {
            val evidences = InputStreamReader(stream, Charset.forName("UTF-8")).use {
                sinsEvidencesCsvParser.parseCsv(it)
            }.mapLeft { BadRequestException(it.message) }.bind()

            val soul = Validated.fromNullable(
                soulRepository.findById(soulId)
            ) { NotFoundException("Soul with $soulId not exist") }.bind()

            val currentReport = sinsReportRepository.findBySoul(soul)

            if (currentReport == null) {
                sinsReportRepository.save(
                    SinsReport(
                        0, soul, evidences, User.empty, ZonedDateTime.now()
                    )
                )
            } else {
                sinsReportRepository.update(currentReport.copy(sins = evidences, uploadedAt = ZonedDateTime.now()))
            }
        }

    @Transactional
    override suspend fun saveOrUpdateGoodnessReportForSoulFromCsv(soulId: Long, stream: InputStream) =
        either<ApplicationException, GoodnessReport> {
            val evidences = InputStreamReader(stream, Charset.forName("UTF-8")).use {
                goodnessEvidencesCsvParser.parseCsv(it)
            }.mapLeft { BadRequestException(it.message) }.bind()

            val soul = Validated.fromNullable(
                soulRepository.findById(soulId)
            ) { NotFoundException("Soul with $soulId not exist") }.bind()

            val currentReport = goodnessReportRepository.findBySoul(soul)

            if (currentReport == null) {
                goodnessReportRepository.save(
                    GoodnessReport(
                        0, soul, evidences, User.empty, ZonedDateTime.now()
                    )
                )
            } else {
                goodnessReportRepository.update(
                    currentReport.copy(goodnessEvidences = evidences, uploadedAt = ZonedDateTime.now())
                )
            }
        }
}
