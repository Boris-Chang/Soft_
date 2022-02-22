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
import ru.ifmo.software_engineering.afterlife.core.exceptions.ForbiddenException
import ru.ifmo.software_engineering.afterlife.core.exceptions.NotFoundException
import ru.ifmo.software_engineering.afterlife.core.exceptions.UnauthorizedException
import ru.ifmo.software_engineering.afterlife.security.services.AuthorizationService
import ru.ifmo.software_engineering.afterlife.users.constants.RoleNames

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
        private val authorizationService: AuthorizationService,
        private val soulRepository: SoulRepository,
        private val reportCommentRepository: ReportCommentRepository
) : ReportsCommentService {
    @Transactional(readOnly = true)
    override suspend fun getCommentsBySoulId(soulId: Long): Validated<ApplicationException, List<ReportComment>> {
        val accessToComments = checkUserCanViewComments()
        if (accessToComments != null) return accessToComments.invalid()

        val soul = this.soulRepository.findById(soulId)
            ?: return NotFoundException("Soul with id=$soulId was not found").invalid()
        return this.reportCommentRepository.findCommentsBySoul(soul).valid()
    }

    override suspend fun postCommentForSoulById(soulId: Long, comment: ReportComment): Validated<ApplicationException, ReportComment> {
        val accessToComments = checkUserCanAddComments()
        if (accessToComments != null) return accessToComments.invalid()

        val soul = this.soulRepository.findById(soulId)
            ?: return NotFoundException("Soul with id=$soulId was not found").invalid()
        val commentToCreate = comment.withJustCreatedAt()
        return this.reportCommentRepository.save(soul, commentToCreate).valid()
    }

    private suspend fun checkUserCanViewComments(): ApplicationException? {
        val allowedRoles = listOf(RoleNames.GOD, RoleNames.DEVIL, RoleNames.HEAVEN_PROSECUTOR, RoleNames.HEAVEN_ADVOCATE)
        return checkUserInRoles(allowedRoles)
    }

    private suspend fun checkUserCanAddComments(): ApplicationException? {
        val allowedRoles = listOf(RoleNames.HEAVEN_PROSECUTOR, RoleNames.HEAVEN_ADVOCATE)
        return checkUserInRoles(allowedRoles)
    }

    private suspend fun checkUserInRoles(roles: List<String>): ApplicationException? {
        val user = this.authorizationService.getCurrentUser() ?: return UnauthorizedException()
        return if (roles.any { user.roles.contains(it) })
            null
        else ForbiddenException("only users with roles $roles can do that")
    }
}
