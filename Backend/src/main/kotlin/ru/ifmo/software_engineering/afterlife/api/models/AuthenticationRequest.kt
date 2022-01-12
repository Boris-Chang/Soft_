package ru.ifmo.software_engineering.afterlife.api.models

data class AuthenticationRequest(
        val login: String,
        val password: String
)