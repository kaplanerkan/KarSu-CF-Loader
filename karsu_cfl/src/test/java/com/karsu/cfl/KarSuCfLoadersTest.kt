package com.karsu.cfl

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class KarSuCfLoadersTest {

    private lateinit var loader: KarSuCfLoaders

    @Before
    fun setUp() {
        val activity = Robolectric.buildActivity(Activity::class.java).create().get()
        loader = KarSuCfLoaders(activity)
    }

    // region Progress Tests

    @Test
    fun `setProgress sets correct value`() {
        loader.setProgress(50, 0)
        assertEquals("Loading: 50 percent", loader.contentDescription)
    }

    @Test
    fun `setProgress clamps negative to 0`() {
        loader.setProgress(-10, 0)
        assertEquals("Loading: 0 percent", loader.contentDescription)
    }

    @Test
    fun `setProgress clamps above 100`() {
        loader.setProgress(150, 0)
        assertEquals("Loading: 100 percent", loader.contentDescription)
    }

    @Test
    fun `setProgress with 0 is valid`() {
        loader.setProgress(0, 0)
        assertEquals("Loading: 0 percent", loader.contentDescription)
    }

    @Test
    fun `setProgress with 100 is valid`() {
        loader.setProgress(100, 0)
        assertEquals("Loading: 100 percent", loader.contentDescription)
    }

    // endregion

    // region Text Tests

    @Test
    fun `setText and getText work correctly`() {
        loader.setText("Hello")
        assertEquals("Hello", loader.getText())
    }

    @Test
    fun `setText null returns null`() {
        loader.setText("Test")
        loader.setText(null)
        assertNull(loader.getText())
    }

    @Test
    fun `setText empty string`() {
        loader.setText("")
        assertEquals("", loader.getText())
    }

    @Test
    fun `setTextColor does not throw`() {
        loader.setTextColor(Color.RED)
        loader.setTextColor(Color.BLUE)
        loader.setTextColor(Color.argb(128, 255, 0, 0))
    }

    @Test
    fun `setTextSize does not throw`() {
        loader.setTextSize(24f)
        loader.setTextSize(8f)
        loader.setTextSize(60f)
    }

    @Test
    fun `setTextFontFamily does not throw`() {
        loader.setTextFontFamily("monospace")
        loader.setTextFontFamily("sans-serif-medium")
        loader.setTextFontFamily(null)
    }

    @Test
    fun `setTextStyle accepts valid styles`() {
        loader.setTextStyle(Typeface.NORMAL)
        loader.setTextStyle(Typeface.BOLD)
        loader.setTextStyle(Typeface.ITALIC)
        loader.setTextStyle(Typeface.BOLD_ITALIC)
    }

    @Test
    fun `setTextLetterSpacing does not throw`() {
        loader.setTextLetterSpacing(0.1f)
        loader.setTextLetterSpacing(-0.05f)
        loader.setTextLetterSpacing(0f)
    }

    @Test
    fun `setTextOffsetX does not throw`() {
        loader.setTextOffsetX(50f)
        loader.setTextOffsetX(-50f)
        loader.setTextOffsetX(0f)
    }

    @Test
    fun `setTextOffsetY does not throw`() {
        loader.setTextOffsetY(80f)
        loader.setTextOffsetY(-30f)
        loader.setTextOffsetY(0f)
    }

    @Test
    fun `setTextWidthMode accepts valid modes`() {
        loader.setTextWidthMode(KarSuCfLoaders.TEXT_WIDTH_WRAP_CONTENT)
        loader.setTextWidthMode(KarSuCfLoaders.TEXT_WIDTH_MATCH_PARENT)
    }

    @Test
    fun `setTextShadow does not throw`() {
        loader.setTextShadow(4f, 2f, 2f, Color.BLACK)
        loader.setTextShadow(0f, 0f, 0f, Color.TRANSPARENT)
    }

    // endregion

    // region Progress Text Tests

    @Test
    fun `setShowProgressText does not throw`() {
        loader.setShowProgressText(true)
        loader.setShowProgressText(false)
    }

    @Test
    fun `setProgressTextFormat does not throw`() {
        loader.setProgressTextFormat("%d%%")
        loader.setProgressTextFormat("Step %d")
    }

    // endregion

    // region Subtitle Tests

    @Test
    fun `setSubtitleText and getSubtitleText work correctly`() {
        loader.setSubtitleText("Loading...")
        assertEquals("Loading...", loader.getSubtitleText())
    }

    @Test
    fun `setSubtitleText null returns null`() {
        loader.setSubtitleText("Test")
        loader.setSubtitleText(null)
        assertNull(loader.getSubtitleText())
    }

    @Test
    fun `setSubtitleTextSize does not throw`() {
        loader.setSubtitleTextSize(12f)
        loader.setSubtitleTextSize(20f)
    }

    @Test
    fun `setSubtitleTextColor does not throw`() {
        loader.setSubtitleTextColor(Color.GRAY)
        loader.setSubtitleTextColor(Color.WHITE)
    }

    @Test
    fun `setSubtitleFontFamily does not throw`() {
        loader.setSubtitleFontFamily("monospace")
        loader.setSubtitleFontFamily(null)
    }

    @Test
    fun `setSubtitleOffsetY does not throw`() {
        loader.setSubtitleOffsetY(10f)
        loader.setSubtitleOffsetY(0f)
    }

    // endregion

    // region Wave & Color Tests

    @Test
    fun `setColor does not throw`() {
        loader.setColor(Color.BLUE)
        loader.setColor(Color.parseColor("#FF9800"))
    }

    @Test
    fun `setAmplitudeRatio does not throw`() {
        loader.setAmplitudeRatio(0.03f)
        loader.setAmplitudeRatio(0.0f)
        loader.setAmplitudeRatio(0.05f)
    }

    @Test
    fun `setBorderWidth does not throw`() {
        loader.setBorderWidth(5f)
        loader.setBorderWidth(0f)
        loader.setBorderWidth(20f)
    }

    @Test
    fun `setWaveEnabled does not throw`() {
        loader.setWaveEnabled(false)
        loader.setWaveEnabled(true)
    }

    // endregion

    // region Auto-Size Tests

    @Test
    fun `setAutoSizeText does not throw`() {
        loader.setAutoSizeText(true)
        loader.setAutoSizeText(false)
    }

    @Test
    fun `setAutoSizeMinTextSize does not throw`() {
        loader.setAutoSizeMinTextSize(8f)
        loader.setAutoSizeMinTextSize(16f)
    }

    // endregion

    // region Recycle Test

    @Test
    fun `recycle does not throw`() {
        loader.recycle()
    }

    @Test
    fun `recycle after setting properties does not throw`() {
        loader.setText("Test")
        loader.setColor(Color.RED)
        loader.setProgress(50, 0)
        loader.recycle()
    }

    // endregion
}
