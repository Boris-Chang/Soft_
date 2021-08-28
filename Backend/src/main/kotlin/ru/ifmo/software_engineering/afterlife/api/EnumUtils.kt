package ru.ifmo.software_engineering.afterlife.api

inline fun <reified T : Enum<*>> enumValueOrNull(name: String): T? =
    T::class.java.enumConstants
        .firstOrNull { it.name == name }

inline fun <reified T : Enum<*>> enumValueOrDefault(name: String, default: T): T =
    T::class.java.enumConstants
        .firstOrNull { it.name == name } ?: default
