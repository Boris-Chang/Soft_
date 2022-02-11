package ru.ifmo.software_engineering.afterlife.classificator.services

import org.springframework.stereotype.Service
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoul
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoulsQueryFilter
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.classificator.repositories.SoulRepository
import ru.ifmo.software_engineering.afterlife.core.exceptions.ForbiddenException
import ru.ifmo.software_engineering.afterlife.core.exceptions.UnauthorizedException
import ru.ifmo.software_engineering.afterlife.core.models.PageRequest
import ru.ifmo.software_engineering.afterlife.core.models.PagedResult
import ru.ifmo.software_engineering.afterlife.security.services.AuthorizationService
import ru.ifmo.software_engineering.afterlife.users.constants.RoleNames

interface SoulsQueryService {
    suspend fun getAllReportedSouls(reportFilter: ReportedSoulsQueryFilter? = null, pageRequest: PageRequest? = null): PagedResult<ReportedSoul>
    suspend fun getSoulById(id: Long): Soul?
}

@Service
class SoulsQueryServiceImpl(
    private val repository: SoulRepository,
    private val authorizationService: AuthorizationService
) : SoulsQueryService {
    override suspend fun getAllReportedSouls(reportFilter: ReportedSoulsQueryFilter?, pageRequest: PageRequest?): PagedResult<ReportedSoul> {
        checkUserCanViewSouls()
        return repository.getReportedSouls(reportFilter, pageRequest ?: PageRequest.default())
    }

    override suspend fun getSoulById(id: Long): Soul? {
        checkUserCanViewSouls()
        return repository.findById(id)
    }

    private suspend fun checkUserCanViewSouls() {
        val user = authorizationService.getCurrentUser() ?: throw UnauthorizedException()
        val validRoles = listOf(RoleNames.GOD, RoleNames.DEVIL, RoleNames.HEAVEN_ADVOCATE, RoleNames.HEAVEN_PROSECUTOR)
        if (validRoles.none { user.roles.contains(it) }) {
            throw ForbiddenException("Only roles $validRoles can do this")
        }
    }
}
