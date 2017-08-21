package com.prokkypew.asciipanelview.tests

import android.graphics.Color
import com.prokkypew.asciipanelview.AsciiPanelView
import com.prokkypew.asciipanelview.CustomTestRunner
import com.prokkypew.asciipanelview.checkRectangleCleared
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config


@RunWith(CustomTestRunner::class)
@Config(sdk = intArrayOf(26))
class AsciiPanelClearTest {
    private lateinit var panel: AsciiPanelView

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
        assertEquals(panel.chars[15][20].char, 'c')
        panel.clear()
        assertEquals(panel.chars[15][20].char, ' ')

        panel.setCursorPosition(15, 20)
        panel.writeCharWithPos('c', 15, 20)
        assertEquals(panel.chars[15][20].char, 'c')
        panel.clear('d')
        assertEquals(panel.chars[15][20].char, 'd')

        panel.setCursorPosition(15, 20)
        panel.writeChar('c', 15, 20, Color.RED)
        assertEquals(panel.chars[15][20].char, 'c')
        panel.clear('d', Color.BLUE, Color.WHITE)
        assertEquals(panel.chars[15][20].char, 'd')
        assertEquals(panel.chars[15][20].charColor, Color.BLUE)
        assertEquals(panel.chars[15][20].bgColor, Color.WHITE)

        panel.clear()
        panel.clearRect('b', 5, 5, 10, 10)
        checkRectangleCleared(panel, 'b', 5, 5, 10, 10, null, null)

        panel.clear()
        panel.clearRect('w', 10, 10, 5, 5, Color.RED, Color.WHITE)
        checkRectangleCleared(panel, 'w', 10, 10, 5, 5, Color.RED, Color.WHITE)
    }
}
