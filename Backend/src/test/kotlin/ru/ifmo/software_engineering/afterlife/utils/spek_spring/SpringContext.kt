package ru.ifmo.software_engineering.afterlife.utils.spek_spring

import org.spekframework.spek2.lifecycle.ExecutionResult
import org.spekframework.spek2.lifecycle.LifecycleListener
import org.spekframework.spek2.lifecycle.TestScope
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestContextManager

/**
 * @author Ranie Jade Ramiso
 */
class SpringContext internal constructor(testContextManager: TestContextManager)
    : LifecycleListener {
    val testContext: TestContext by lazy { testContextManager.testContext }

    inline fun <reified T: Any> inject(): T {
        return testContext.applicationContext.getBean(T::class.java)
    }
}
