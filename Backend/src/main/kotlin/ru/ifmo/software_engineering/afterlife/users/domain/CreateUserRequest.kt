package ru.ifmo.software_engineering.afterlife.users.domain

data class CreateUserRequest(
        val username: String,
        val password: String,
        val roles: List<String>
)