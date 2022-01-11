package ru.ifmo.software_engineering.afterlife.users.domain

data class Authority(
        val user: User,
        val passwordHash: String,
        val roles: List<String>
) {
    val id = user.id
}
