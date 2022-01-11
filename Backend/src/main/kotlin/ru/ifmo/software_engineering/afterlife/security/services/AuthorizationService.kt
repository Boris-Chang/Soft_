package ru.ifmo.software_engineering.afterlife.security.services

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import ru.ifmo.software_engineering.afterlife.security.IdentityImpl
import ru.ifmo.software_engineering.afterlife.security.Principal
import ru.ifmo.software_engineering.afterlife.users.domain.User

interface AuthorizationService {
    suspend fun getCurrentUser(): Principal?
}

@Service
class AuthorizationServiceImpl : AuthorizationService {
    override suspend fun getCurrentUser(): Principal? {
        val principal = SecurityContextHolder.getContext().authentication.principal
        return if (principal is UserDetails) {
            Principal(
                    identity = IdentityImpl(principal.username, principal.username),
                    roles = principal.authorities.map { it.authority })
        } else {
            null
        }
    }

}
