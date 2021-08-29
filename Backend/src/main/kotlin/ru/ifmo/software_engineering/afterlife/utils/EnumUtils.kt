package ru.ifmo.software_engineering.afterlife.utils

import kotlin.reflect.KClass

fun <T : Enum<*>> enumValueOrNull(name: String, classe: KClass<T>): T? =
    classe.java.enumConstants
        .firstOrNull { it.name == name }

inline fun <reified T : Enum<*>> enumValueOrNull(name: String): T? =
    enumValueOrNull(name, T::class)
