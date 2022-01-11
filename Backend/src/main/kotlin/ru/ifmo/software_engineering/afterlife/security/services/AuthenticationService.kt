package ru.ifmo.software_engineering.afterlife.security.services

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.ifmo.software_engineering.afterlife.security.utils.JwtTokenUtil
import ru.ifmo.software_engineering.afterlife.security.toIdentity
import ru.ifmo.software_engineering.afterlife.users.repositories.AuthorityRepository

interface AuthenticationService {
    suspend fun authenticationToken(login: String, password: String): String?
}

@Service
class AuthenticationServiceImpl(
        private val passwordEncoder: PasswordEncoder,
        private val authorityRepository: AuthorityRepository,
        private val jwtTokenUtil: JwtTokenUtil
) : AuthenticationService {
    override suspend fun authenticationToken(login: String, password: String): String? {
        val authority = authorityRepository.findUserByUsername(login) ?: return null

        if (!passwordEncoder.matches(password, authority.passwordHash)) {
            return null
        }

        return jwtTokenUtil.generateAccessToken(authority.toIdentity())
    }
}