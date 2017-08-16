package com.prokkypew.infinitecavystory.tests

import android.graphics.Color
import com.prokkypew.infinitecavystory.AsciiPanelView
import com.prokkypew.infinitecavystory.checkRectangleCleared
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(manifest = "app/src/main/AndroidManifest.xml",
        sdk = intArrayOf(26))
class AsciiPanelClearTest {
    lateinit var panel: AsciiPanelView

    @Before
    @Throws(Exception::class)
    fun setUp() {
        panel = AsciiPanelView(RuntimeEnvironment.application)
    }

    @Test
    fun checkPanelCreated() {
        assertNotNull(panel)
    }

    @Test
    fun checkBasicClear() {
        panel.setCursorPosition(15, 20)
        panel.writeCharWithPos('c', 15, 20)
        assertEquals(panel.chars[15][20].glyph, 'c')
        panel.clear()
        assertEquals(panel.chars[15][20].glyph, ' ')

        panel.setCursorPosition(15, 20)
        panel.writeCharWithPos('c', 15, 20)
        assertEquals(panel.chars[15][20].glyph, 'c')
        panel.clear('d')
        assertEquals(panel.chars[15][20].glyph, 'd')

        panel.setCursorPosition(15, 20)
        panel.writeChar('c', 15, 20, Color.RED)
        assertEquals(panel.chars[15][20].glyph, 'c')
        panel.clear('d', Color.BLUE, Color.WHITE)
        assertEquals(panel.chars[15][20].glyph, 'd')
        assertEquals(panel.chars[15][20].glyphColor, Color.BLUE)
        assertEquals(panel.chars[15][20].bgColor, Color.WHITE)

        panel.clear()
        panel.clearRect('b', 5, 5, 10, 10)
        checkRectangleCleared(panel, 'b', 5, 5, 10, 10, null, null)

        panel.clear()
        panel.clearRect('w', 10, 10, 5, 5, Color.RED, Color.WHITE)
        checkRectangleCleared(panel, 'w', 10, 10, 5, 5, Color.RED, Color.WHITE)
    }
}
