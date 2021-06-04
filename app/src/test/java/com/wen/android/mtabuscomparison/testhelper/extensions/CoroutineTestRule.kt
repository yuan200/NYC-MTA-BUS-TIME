package com.wen.android.mtabuscomparison.testhelper.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class CoroutineTestRule(
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        print(">>>>>>>>>>>>>>>>>>>")
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        print(">>>>>>>>>>>>>>>>>>>")
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}

fun CoroutineTestRule.runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) =
    this.testDispatcher.runBlockingTest {
        block()
    }

fun CoroutineTestRule.CoroutineScope(): CoroutineScope = CoroutineScope(testDispatcher)