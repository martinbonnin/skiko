package org.jetbrains.skiko

import org.jetbrains.skiko.tests.runTest
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Test
import java.awt.Font
import java.awt.GraphicsEnvironment

class AwtFontInteropTest {
    private val fontManager = AwtFontManager()

    private fun assumeOk() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless())
        Assume.assumeTrue(hostOs != OS.Linux)
    }


}