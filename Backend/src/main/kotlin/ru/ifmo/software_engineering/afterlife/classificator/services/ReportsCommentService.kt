package ru.ifmo.software_engineering.afterlife.classificator.services

import arrow.core.Validated
import arrow.core.invalid
import arrow.core.valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportComment
import ru.ifmo.software_engineering.afterlife.classificator.repositories.ReportCommentRepository
import ru.ifmo.software_engineering.afterlife.classificator.repositories.SoulRepository
import ru.ifmo.software_engineering.afterlife.core.exceptions.ApplicationException
import ru.ifmo.software_engineering.afterlife.core.exceptions.NotFoundException
import java.time.ZonedDateTime

interface ReportsCommentService {
    @Transactional(readOnly = true)
    suspend fun getCommentsBySoulId(soulId: Long): Validated<ApplicationException, List<ReportComment>>
    suspend fun postCommentForSoulById(
        soulId: Long,
        comment: ReportComment
    ): Validated<ApplicationException, ReportComment>
}

@Service
class ReportsCommentServiceImpl(
    private val soulRepository: SoulRepository,
    private val reportCommentRepository: ReportCommentRepository
) : ReportsCommentService {
    @Transactional(readOnly = true)
    override suspend fun getCommentsBySoulId(soulId: Long): Validated<ApplicationException, List<ReportComment>> {
        val soul = this.soulRepository.findById(soulId)
            ?: return NotFoundException("Soul with id=$soulId was not found").invalid()
        return this.reportCommentRepository.findCommentsBySoul(soul).valid()
    }

    override suspend fun postCommentForSoulById(soulId: Long, comment: ReportComment): Validated<ApplicationException, ReportComment> {
        val soul = this.soulRepository.findById(soulId)
            ?: return NotFoundException("Soul with id=$soulId was not found").invalid()
        val commentToCreate = comment.copy(createdAt = ZonedDateTime.now())
        return this.reportCommentRepository.save(soul, commentToCreate).valid()
    }
}
