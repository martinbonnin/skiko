package org.jetbrains.skiko

import org.junit.Test

internal class VersionTest {
    @Test(timeout = 1_000)
    fun `skiko version`() {
        assert(org.jetbrains.skiko.Version.skiko.isNotBlank())
    }

    @Test(timeout = 1_000)
    fun `skia version`() {
        assert( org.jetbrains.skiko.Version.skia.isNotBlank())
    }
}