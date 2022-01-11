package ru.ifmo.software_engineering.afterlife.security

import ru.ifmo.software_engineering.afterlife.users.domain.Authority


interface Identity {
    val id: String
    val username: String
}

fun Authority.toIdentity(): Identity =
    IdentityImpl(this.user.id.toString(), this.user.username)

data class IdentityImpl(
        override val id: String,
        override val username: String
) : Identity

data class Principal(
        val identity: Identity,
        val roles: List<String>
)
