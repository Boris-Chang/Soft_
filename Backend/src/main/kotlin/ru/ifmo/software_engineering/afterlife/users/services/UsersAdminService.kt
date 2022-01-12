package ru.ifmo.software_engineering.afterlife.users.services

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.ifmo.software_engineering.afterlife.core.exceptions.ConflictException
import ru.ifmo.software_engineering.afterlife.core.exceptions.ForbiddenException
import ru.ifmo.software_engineering.afterlife.core.exceptions.UnauthorizedException
import ru.ifmo.software_engineering.afterlife.security.IdentityImpl
import ru.ifmo.software_engineering.afterlife.security.Principal
import ru.ifmo.software_engineering.afterlife.security.services.AuthorizationService
import ru.ifmo.software_engineering.afterlife.users.constants.RoleNames
import ru.ifmo.software_engineering.afterlife.users.domain.Authority
import ru.ifmo.software_engineering.afterlife.users.domain.CreateUserRequest
import ru.ifmo.software_engineering.afterlife.users.domain.User
import ru.ifmo.software_engineering.afterlife.users.repositories.AuthorityRepository

interface UsersAdminService {
    suspend fun createUser(user: CreateUserRequest): Principal
}

@Service
class UserAdminServiceImpl(
        private val authorityRepository: AuthorityRepository,
        private val authorizationService: AuthorizationService,
        private val passwordEncoder: PasswordEncoder
) : UsersAdminService {
    override suspend fun createUser(user: CreateUserRequest): Principal {
        checkAdmin()
        checkUserNotExist(user.username)
        val createdAuthority = authorityRepository.createAuthority(Authority(
                User(0, user.username),
                passwordHash = passwordEncoder.encode(user.password),
                roles = user.roles))
        return Principal(
                identity = IdentityImpl(
                        createdAuthority.id.toString(), createdAuthority.user.username),
                roles = user.roles)
    }

    private suspend fun checkAdmin() {
        val currentUser = authorizationService.getCurrentUser() ?: throw UnauthorizedException()
        if (!currentUser.roles.contains(RoleNames.ADMIN)) {
            throw ForbiddenException()
        }
    }

    private suspend fun checkUserNotExist(username: String) {
        if (authorityRepository.findUserByUsername(username) != null) {
            throw ConflictException("User with username $username already exist")
        }
    }
}
