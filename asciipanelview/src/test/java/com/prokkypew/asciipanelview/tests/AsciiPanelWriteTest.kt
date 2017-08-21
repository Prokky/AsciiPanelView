package com.prokkypew.asciipanelview.tests

import android.graphics.Color
import com.prokkypew.asciipanelview.*
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(CustomTestRunner::class)
@Config(sdk = intArrayOf(26))
class AsciiPanelWriteTest {
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
    fun checkCursor() {
        panel.setCursorPosition(15, 20)
        assertEquals(panel.cursorX, 15)
        assertEquals(panel.cursorY, 20)
        checkInvalidCursorPos(panel, -1, 5)
        checkInvalidCursorPos(panel, 5, -1)
        checkInvalidCursorPos(panel, Integer.MAX_VALUE, 5)
        checkInvalidCursorPos(panel, 5, Integer.MAX_VALUE)
    }

    @Test
    fun checkWriteChar() {
        panel.setCursorPosition(5, 14)
        panel.writeChar('c')
        assertEquals(panel.chars[5][14].char, 'c')

        panel.setCursorPosition(1, 1)
        panel.writeChar('p', Color.RED)
        assertEquals(panel.chars[1][1].char, 'p')
        assertEquals(panel.chars[1][1].charColor, Color.RED)

        panel.writeCharWithPos('a', 3, 3)
        assertEquals(panel.chars[3][3].char, 'a')

        panel.writeChar('#', 4, 4, Color.BLUE)
        assertEquals(panel.chars[4][4].char, '#')
        assertEquals(panel.chars[4][4].charColor, Color.BLUE)

        panel.setCursorPosition(15, 15)
        panel.writeCharWithColor('z', Color.RED, Color.BLACK)
        assertEquals(panel.chars[15][15].char, 'z')
        assertEquals(panel.chars[15][15].charColor, Color.RED)
        assertEquals(panel.chars[15][15].bgColor, Color.BLACK)

        checkInvalidCharPos(panel, 'c', 5, Integer.MAX_VALUE, null)
        checkInvalidCharPos(panel, 'c', Integer.MAX_VALUE, 5, null)
        checkInvalidCharPos(panel, 'c', 5, Integer.MAX_VALUE, Color.RED)
        checkInvalidCharPos(panel, 'c', Integer.MAX_VALUE, 5, Color.RED)
    }

    @Test
    fun checkWriteString() {
        val string = "pew"
        panel.writeString(string)
        checkCorrectString(panel, string, 0, 0, null, null)

        panel.setCursorPosX(0)
        panel.writeString(string, Color.BLUE)
        checkCorrectString(panel, string, 0, 0, Color.BLUE, null)

        panel.writeStringWithPos(string, 10, 1)
        checkCorrectString(panel, string, 10, 1, null, null)

        panel.writeString(string, 0, 1, Color.RED)
        checkCorrectString(panel, string, 0, 1, Color.RED, null)

        panel.setCursorPosition(15, 0)
        panel.writeStringWithColor(string, Color.RED, Color.BLACK)
        checkCorrectString(panel, string, 15, 0, Color.RED, Color.BLACK)

        panel.setCursorPosX(63)
        try {
            panel.writeString("pewly test")
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            assertNotNull(e)
        }
    }
}
