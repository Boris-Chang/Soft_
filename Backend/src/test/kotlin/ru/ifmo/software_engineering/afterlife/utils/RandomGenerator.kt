package ru.ifmo.software_engineering.afterlife.utils

import java.util.*
import kotlin.streams.asSequence

class RandomGenerator {
    companion object {
        private val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        fun generateRandomString(outputStrLength: Long): String {
            return Random().ints(outputStrLength, 0, source.length)
                .asSequence()
                .map(source::get)
                .joinToString("")
        }
    }
}
