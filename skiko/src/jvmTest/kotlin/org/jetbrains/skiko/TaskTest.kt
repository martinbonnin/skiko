package org.jetbrains.skiko

import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test
import org.junit.jupiter.api.Timeout
import org.junitpioneer.jupiter.RetryingTest
import java.util.concurrent.Executors.newSingleThreadExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
internal class TaskTest {
    @Test
    fun `runAndAwait with finish`() = test {
        val task = Task()

        val job = launch {
            task.runAndAwait {}
        }

        advanceUntilIdle()

        task.finish()
        advanceUntilIdle()

        assertTrue(job.isCompleted)
    }

    @Test
    fun `runAndAwait without finish`() = test {
        val task = Task()

        val job = launch {
            task.runAndAwait {}
        }

        advanceUntilIdle()
        assertFalse(job.isCompleted)
    }

    @Test
    fun `finish inside runAndAwait`() = test {
        val task = Task()

        val job = launch {
            task.runAndAwait {
                task.finish()
            }
        }

        advanceUntilIdle()
        assertTrue(job.isCompleted)
    }

    @Test
    fun `finish before runAndAwait`() = test {
        val task = Task()

        val job = launch {
            task.finish()
            task.runAndAwait {}
        }

        advanceUntilIdle()
        assertFalse(job.isCompleted)
    }

    @Test
    @Timeout(value = 5_000, unit = TimeUnit.MILLISECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `finish in another thread`() {
        val task = Task()

        runBlocking {
            repeat(1000) {
                task.runAndAwait {
                    launch(Dispatchers.IO) {
                        task.finish()
                    }
                }
            }
        }
    }

    @Test
    @Timeout(value = 5_000, unit = TimeUnit.MILLISECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `simulate MacOs layer`() {
        runBlocking {
            val job = Job()

            val layer = object : MacOsSimulatedLayer(scope = CoroutineScope(coroutineContext + job)) {
                val draw = Task()

                override fun draw() {
                    draw.finish()
                }

                suspend fun display() {
                    draw.runAndAwait {
                        setNeedsDisplay()
                    }
                }
            }

            repeat(10000) {
                layer.display()
            }

            job.cancel()
        }
    }

    // TODO why do this test fail on CI? https://github.com/JetBrains/skiko/runs/5096776296?check_suite_focus=true
    //  (test timed out after 10000 milliseconds)
    //  What is wrong - Task class, this test, or UI tests somehow interfere with this test?
    @Test
    @Timeout(value = 20_000, unit = TimeUnit.MILLISECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    @Ignore
    fun `simulate MacOs layer with another renderings`() {
        runBlocking {
            val job = Job()

            val layer = object : MacOsSimulatedLayer(scope = CoroutineScope(coroutineContext + job)) {
                val draw = Task()

                override fun draw() {
                    draw.finish()
                }

                suspend fun display() {
                    draw.runAndAwait {
                        setNeedsDisplay()
                    }
                }
            }

            val random = Random(42)

            val anotherRenderingsJob1 = launch(newSingleThreadExecutor().asCoroutineDispatcher()) {
                while (true) {
                    repeat(random.nextInt(4)) {
                        layer.setNeedsDisplay()
                    }
                    yield()
                }
            }

            val anotherRenderingsJob2 = launch(newSingleThreadExecutor().asCoroutineDispatcher()) {
                while (true) {
                    repeat(random.nextInt(4)) {
                        layer.setNeedsDisplay()
                    }
                    yield()
                }
            }

            repeat(10000) {
                layer.display()
            }

            job.cancel()
            anotherRenderingsJob1.cancel()
            anotherRenderingsJob2.cancel()
        }
    }

    private abstract class MacOsSimulatedLayer(scope: CoroutineScope) {
        private val dispatcher = newSingleThreadExecutor().asCoroutineDispatcher()

        private var needsDisplay = AtomicBoolean(false)

        init {
            scope.launch(dispatcher) {
                while (isActive) {
                    if (needsDisplay.getAndSet(false)) {
                        draw()
                    }
                    yield()
                }
            }
        }

        abstract fun draw()

        fun setNeedsDisplay() {
            needsDisplay.set(true)
        }
    }

    private fun test(
        block: suspend TestCoroutineScope.() -> Unit
    ) = runBlockingTest {
        pauseDispatcher()
        val job = Job()
        TestCoroutineScope(coroutineContext + job).block()
        job.cancel()
    }
}