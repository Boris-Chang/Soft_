package ru.ifmo.software_engineering.afterlife.users.domain

data class User(
    val id: Long,
    val username: String,
) {
    private constructor() : this(0, "")

    companion object {
        val empty = User()
    }
}
