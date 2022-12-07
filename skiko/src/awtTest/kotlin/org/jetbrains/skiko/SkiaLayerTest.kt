package org.jetbrains.skiko

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.FontMgr
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect
import org.jetbrains.skia.paragraph.FontCollection
import org.jetbrains.skia.paragraph.ParagraphBuilder
import org.jetbrains.skia.paragraph.ParagraphStyle
import org.jetbrains.skia.paragraph.TextStyle
import org.jetbrains.skiko.context.JvmContextHandler
import org.jetbrains.skiko.redrawer.Redrawer
import org.jetbrains.skiko.util.ScreenshotTestRule
import org.jetbrains.skiko.util.UiTestScope
import org.jetbrains.skiko.util.UiTestWindow
import org.jetbrains.skiko.util.uiTest
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import java.awt.Color
import java.awt.Dimension
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.JLayeredPane
import javax.swing.WindowConstants
import kotlin.random.Random
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Suppress("BlockingMethodInNonBlockingContext", "SameParameterValue")
class SkiaLayerTest {
    private val fontCollection = FontCollection()
        .setDefaultFontManager(FontMgr.default)

    private fun paragraph(size: Float, text: String) =
        ParagraphBuilder(ParagraphStyle(), fontCollection)
            .pushStyle(
                TextStyle().apply {
                    color = Color.RED.rgb
                    fontSize = size
                }
            )
            .addText(text)
            .popStyle()
            .build()

    @get:Rule
    val screenshots = ScreenshotTestRule()

    @Test(timeout = 60000)
    fun `stress test - open and paint immediately`() = uiTest {
        fun openWindow() = UiTestWindow(
            properties = SkiaLayerProperties(isVsyncEnabled = false, isVsyncFramelimitFallbackEnabled = true)
        ).apply {
            setLocation(200, 200)
            setSize(400, 200)
            preferredSize = Dimension(400, 200)
            defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
            layer.skikoView = object : SkikoView {
                override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
                }
            }
        }

        repeat(30) {
            delay(100)
            val window = openWindow()
            window.isVisible = true
            window.layer.needRedraw()
            yield()
            window.paint(window.graphics)
            window.close()
        }
    }




    private suspend fun UiTestScope.testFallbackToSoftware(nonSoftwareRenderFactory: RenderFactory) {
        val window = UiTestWindow(
            renderFactory = OverrideNonSoftwareRenderFactory(nonSoftwareRenderFactory)
        )
        try {
            window.setLocation(200, 200)
            window.setSize(400, 200)
            window.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
            val app = RectRenderer(window.layer, 200, 100, Color.RED)
            window.layer.skikoView = app
            window.isUndecorated = true
            window.isVisible = true

            delay(1000)
            screenshots.assert(window.bounds, "frame1", "testFallbackToSoftware")

            app.rectWidth = 100
            window.layer.needRedraw()
            delay(1000)
            screenshots.assert(window.bounds, "frame2", "testFallbackToSoftware")

            assertEquals(GraphicsApi.SOFTWARE_COMPAT, window.layer.renderApi)
        } finally {
            window.close()
        }
    }

    private class OverrideNonSoftwareRenderFactory(
        private val nonSoftwareRenderFactory: RenderFactory
    ) : RenderFactory {
        override fun createRedrawer(
            layer: SkiaLayer,
            renderApi: GraphicsApi,
            analytics: SkiaLayerAnalytics,
            properties: SkiaLayerProperties
        ): Redrawer {
            return if (renderApi == GraphicsApi.SOFTWARE_COMPAT) {
                RenderFactory.Default.createRedrawer(layer, renderApi, analytics, properties)
            } else {
                nonSoftwareRenderFactory.createRedrawer(layer, renderApi, analytics, properties)
            }
        }
    }


    private fun testRenderText(os: OS) = uiTest {
        assumeTrue(hostOs == os)

        val window = UiTestWindow()
        try {
            window.setLocation(200, 200)
            window.setSize(400, 200)
            window.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE

            val paragraph by lazy { paragraph(window.layer.contentScale * 40, "=-+Нп") }

            window.layer.skikoView = object : SkikoView {
                override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
                    canvas.clear(Color.WHITE.rgb)
                    paragraph.layout(Float.POSITIVE_INFINITY)
                    paragraph.paint(canvas, 0f, 0f)
                }
            }

            window.isUndecorated = true
            window.isVisible = true
            delay(1000)

            // check the line metrics
            val lineMetrics = paragraph.lineMetrics
            assertTrue(lineMetrics.isNotEmpty())
            assertEquals(0, lineMetrics.first().startIndex)
            assertEquals(5, lineMetrics.first().endIndex)
            assertEquals(5, lineMetrics.first().endExcludingWhitespaces)
            assertEquals(5, lineMetrics.first().endIncludingNewline)
            assertEquals(true, lineMetrics.first().isHardBreak)
            assertEquals(0, lineMetrics.first().lineNumber)

            screenshots.assert(window.bounds)
        } finally {
            window.close()
        }
    }

    private class RectRenderer(
        private val layer: SkiaLayer,
        var rectWidth: Int,
        var rectHeight: Int,
        private val rectColor: Color
    ) : SkikoView {
        override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
            val dpi = layer.contentScale
            canvas.drawRect(Rect(0f, 0f, width.toFloat(), height.toFloat()), Paint().apply {
                color = Color.WHITE.rgb
            })
            canvas.drawRect(Rect(0f, 0f, rectWidth * dpi, rectHeight * dpi), Paint().apply {
                color = rectColor.rgb
            })
        }
    }

    private class AnimatedBoxRenderer(
        private val layer: SkiaLayer,
        private val pixelsPerSecond: Double,
        private val size: Double
    ) : SkikoView {
        private var oldNanoTime = Long.MAX_VALUE
        private var x = 0.0

        override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
            canvas.clear(Color.WHITE.rgb)

            val dt = (nanoTime - oldNanoTime).coerceAtLeast(0) / 1E9
            oldNanoTime = nanoTime

            x += dt * pixelsPerSecond
            if (x - size > width) {
                x = 0.0
            }

            canvas.drawRect(Rect(x.toFloat(), 0f, x.toFloat() + size.toFloat(), size.toFloat()), Paint().apply {
                color = Color.RED.rgb
            })

            layer.needRedraw()
        }
    }
}

private fun JFrame.close() = dispatchEvent(WindowEvent(this, WindowEvent.WINDOW_CLOSING))
