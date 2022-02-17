package ru.ifmo.software_engineering.afterlife.utils.spek_spring

import org.spekframework.spek2.dsl.Root
import org.springframework.test.context.TestContextManager
import kotlin.reflect.KClass


fun Root.createContext(spec: KClass<*>): SpringContext {
    return SpringContext(TestContextManager(spec.java)).apply {
        registerListener(this)
    }
}