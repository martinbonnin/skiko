package org.jetbrains.skiko.util

import org.jetbrains.skia.Image
import org.jetbrains.skiko.toImage
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.awt.Rectangle
import java.awt.Robot
import java.io.File
import kotlin.test.currentStackTrace

// WARNING!!!
// macOS has wrong colors ([128, 128, 128] isn't [128, 128, 128] on screenshot). Only white, black, red and green are correct.
// So use only these color for cross-platform screenshots tests.
// TODO fix colors on macOS

inline fun getDefaultIdentifier() =
    with(currentStackTrace().get(0)) {
        "${className}_$methodName"
            .replace(".", "_")
            .replace(",", "_")
            .replace(" ", "_")
            .replace("(", "_")
            .replace(")", "_")
            .replace("__", "_")
            .replace("__", "_")
            .removePrefix("_")
            .removeSuffix("_")
    }

object screenshots {
    internal lateinit var testIdentifier: String
    @PublishedApi
    internal val robot by lazy { Robot() }
    @PublishedApi
    internal val screenshotsDir = File(System.getProperty("skiko.test.screenshots.dir")!!)

    inline fun assert(
        rectangle: Rectangle,
        id: String = "",
        testIdentifier: String = getDefaultIdentifier()
    ) {
        val actual = robot.createScreenCapture(rectangle)
        assert(actual.toImage(), id, testIdentifier)
    }

    inline fun assert(
        actual: Image,
        id: String = "",
        testIdentifier: String = getDefaultIdentifier()
    ) {
        val name = if (id.isNotEmpty()) "${testIdentifier}_$id" else testIdentifier
        val actualFile = File(screenshotsDir, "${name}_actual.png")
        val expectedFile = File(screenshotsDir, "$name.png")
        if (actualFile.exists()) {
            actualFile.delete()
        }
        if (expectedFile.exists()) {
            val expected = Image.makeFromEncoded(expectedFile.readBytes())
            // macOs screenshots can have different color on different configurations
            if (!isContentSame(expected, actual, sensitivity = 0.25)) {
                actualFile.writeBytes(actual.encodeToData()!!.bytes)
                throw AssertionError(
                    "Image mismatch! Expected image ${expectedFile.absolutePath}, actual: ${actualFile.absolutePath}"
                )
            }
        } else {
            actualFile.writeBytes(actual.encodeToData()!!.bytes)
            throw AssertionError(
                "Missing screenshot image " +
                        "${actualFile.absolutePath}. " +
                        "Did you mean to check in a new image?"
            )
        }
    }
}
