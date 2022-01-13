package ru.ifmo.software_engineering.afterlife.users.services

import org.springframework.stereotype.Service
import ru.ifmo.software_engineering.afterlife.core.exceptions.UnauthorizedException
import ru.ifmo.software_engineering.afterlife.security.Principal
import ru.ifmo.software_engineering.afterlife.security.services.AuthorizationService

interface AuthorizedUserService {
    suspend fun getCurrentUser(): Principal
}

@Service
class AuthorizedUserServiceImpl(
        private val authorizationService: AuthorizationService
) : AuthorizedUserService {
    override suspend fun getCurrentUser(): Principal {
        return authorizationService.getCurrentUser() ?: throw UnauthorizedException()
    }
}
