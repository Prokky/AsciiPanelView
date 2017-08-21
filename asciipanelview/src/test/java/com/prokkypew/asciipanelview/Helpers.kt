package com.prokkypew.asciipanelview

import org.junit.Assert

/**
 * Created by prokk on 16.08.2017.
 */


fun checkRectangleCleared(panel: AsciiPanelView, character: Char, x: Int, y: Int, width: Int, height: Int, glyphColor: Int?, bgColor: Int?) {
    for (i in x until x + width) {
        for (j in y until y + height) {
            Assert.assertEquals(panel.chars[i][j].char, character)
            if (glyphColor != null)
                Assert.assertEquals(panel.chars[i][j].charColor, glyphColor)
            if (bgColor != null)
                Assert.assertEquals(panel.chars[i][j].bgColor, bgColor)
        }
    }
}

fun checkInvalidCursorPos(panel: AsciiPanelView, x: Int, y: Int) {
    try {
        panel.setCursorPosition(x, y)
        Assert.fail()
    } catch (e: IllegalArgumentException) {
        Assert.assertNotNull(e)
    }
}

fun checkInvalidCharPos(panel: AsciiPanelView, glyph: Char, x: Int, y: Int, color: Int?) {
    try {
        if (color == null) {
            panel.writeCharWithPos(glyph, x, y)
        } else {
            panel.writeChar(glyph, x, y, color)
        }
        Assert.fail()
    } catch (e: IllegalArgumentException) {
        Assert.assertNotNull(e)
    }
}

fun checkCorrectString(panel: AsciiPanelView, string: String, posX: Int, posY: Int, color: Int?, bgColor: Int?) {
    for (i in posX until posX + string.length) {
        Assert.assertEquals(panel.chars[i][posY].char, string[i - posX])
        if (color != null)
            Assert.assertEquals(panel.chars[i][posY].charColor, color)
        if (bgColor != null)
            Assert.assertEquals(panel.chars[i][posY].bgColor, bgColor)
    }
}