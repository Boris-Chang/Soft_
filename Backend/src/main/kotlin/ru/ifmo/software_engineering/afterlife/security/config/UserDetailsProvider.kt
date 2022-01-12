package ru.ifmo.software_engineering.afterlife.security.config

import kotlinx.coroutines.runBlocking
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import ru.ifmo.software_engineering.afterlife.users.repositories.AuthorityRepository

@Component
class UserDetailsProvider(
        private val userRepository: AuthorityRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails? {
        if (username == null) {
            return null
        }

        val authority = runBlocking {
            userRepository.findUserByUsername(username)
        } ?: return null

        val authorities = authority.roles.map { SimpleGrantedAuthority(it) }

        return User(authority.user.username, authority.passwordHash, authorities)
    }
}