package ru.ifmo.software_engineering.afterlife.classificator.services

import org.springframework.stereotype.Service
import ru.ifmo.software_engineering.afterlife.classificator.domain.AfterworldSection
import ru.ifmo.software_engineering.afterlife.classificator.domain.ArguedClassifierDecision
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.classificator.repositories.ArguedClassifierDecisionRepository
import ru.ifmo.software_engineering.afterlife.classificator.repositories.SoulRepository
import ru.ifmo.software_engineering.afterlife.core.exceptions.ConflictException
import ru.ifmo.software_engineering.afterlife.core.exceptions.ForbiddenException
import ru.ifmo.software_engineering.afterlife.core.exceptions.NotFoundException
import ru.ifmo.software_engineering.afterlife.core.exceptions.UnauthorizedException
import ru.ifmo.software_engineering.afterlife.security.Principal
import ru.ifmo.software_engineering.afterlife.security.services.AuthorizationService
import ru.ifmo.software_engineering.afterlife.users.constants.RoleNames

interface ArgueClassifierService {
    suspend fun markClassifierDecisionArgued(soulId: Long): ArguedClassifierDecision
    suspend fun changeClassifierDecision(soulId: Long, changedDecision: AfterworldSection): ArguedClassifierDecision
    suspend fun getSoulArgue(soulId: Long): ArguedClassifierDecision
}

@Service
class ArgueClassifierServiceImpl(
        private val authorizationService: AuthorizationService,
        private val soulsRepository: SoulRepository,
        private val arguedClassifierDecisionRepository: ArguedClassifierDecisionRepository
) : ArgueClassifierService {

    override suspend fun markClassifierDecisionArgued(soulId: Long): ArguedClassifierDecision {
        val user = getCurrentUserOrThrow().apply { checkCanArgue() }
        val soul = getSoulOrThrow(soulId).apply { checkCanBeArgued() }

        val argue = arguedClassifierDecisionRepository.findArgueForSoul(soul)
        val argueToCreate =
                if (user.isGod()) argue.asArguedByGod()
                else if (user.isDevil()) argue.asArguedByDevil()
                else argue
        return arguedClassifierDecisionRepository.updateSoulArgue(soul, argueToCreate)
    }

    override suspend fun changeClassifierDecision(soulId: Long, changedDecision: AfterworldSection): ArguedClassifierDecision {
        val user = getCurrentUserOrThrow().apply { checkCanArgue() }
        val soul = getSoulOrThrow(soulId).apply { checkCanBeArgued() }

        val argue = arguedClassifierDecisionRepository.findArgueForSoul(soul)
        if (changedDecision == soul.classifiedAfterworldSection) {
            return argue
        }

        val argueToCreate =
                if (user.isGod()) argue.asChangedDecisionByGod(changedDecision)
                else if (user.isDevil()) argue.asChangedDecisionByDevil(changedDecision)
                else argue
        return arguedClassifierDecisionRepository.updateSoulArgue(soul, argueToCreate)
    }

    override suspend fun getSoulArgue(soulId: Long): ArguedClassifierDecision {
        getCurrentUserOrThrow().apply { this.checkCanViewArgue() }
        val soul = getSoulOrThrow(soulId)

        return arguedClassifierDecisionRepository.findArgueForSoul(soul)
    }

    private suspend fun getCurrentUserOrThrow(): Principal =
            authorizationService.getCurrentUser() ?: throw UnauthorizedException()

    private suspend fun getSoulOrThrow(soulId: Long): Soul =
            soulsRepository.findById(soulId) ?: throw NotFoundException("Soul not found")

    private fun Principal.checkCanArgue() {
        if (!this.isGod() && !this.isDevil()) {
            throw ForbiddenException("Only ${RoleNames.GOD} or ${RoleNames.DEVIL} can argue classifier decision")
        }
    }

    private fun Soul.checkCanBeArgued() =
            this.classifiedAfterworldSection ?: throw ConflictException("You can not argue or change decision of " +
                    "classifier if soul was not classified yet")

    private fun Principal.checkCanViewArgue() {
        if (!this.isGod() && !this.isDevil() && !this.isAdvocateOrProsecutor()) {
            throw ForbiddenException("Only ${RoleNames.GOD}/${RoleNames.DEVIL}/" +
                    "${RoleNames.HEAVEN_ADVOCATE}/${RoleNames.HEAVEN_PROSECUTOR} can argue classifier decision")
        }
    }

    private fun Principal.isGod() = this.roles.contains(RoleNames.GOD)

    private fun Principal.isDevil() = this.roles.contains(RoleNames.DEVIL)

    private fun Principal.isAdvocateOrProsecutor() =
            this.roles.contains(RoleNames.HEAVEN_PROSECUTOR) || this.roles.contains(RoleNames.HEAVEN_ADVOCATE)
}