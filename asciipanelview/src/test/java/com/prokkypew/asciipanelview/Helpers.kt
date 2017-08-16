package com.prokkypew.infinitecavystory

import org.junit.Assert

/**
 * Created by prokk on 16.08.2017.
 */


fun checkRectangleCleared(panel: AsciiPanelView, character: Char, x: Int, y: Int, width: Int, height: Int, glyphColor: Int?, bgColor: Int?) {
    for (i in x..x + width - 1) {
        for (j in y..y + height - 1) {
            Assert.assertEquals(panel.chars[i][j].glyph, character)
            if (glyphColor != null)
                Assert.assertEquals(panel.chars[i][j].glyphColor, glyphColor)
            if (bgColor != null)
                Assert.assertEquals(panel.chars[i][j].bgColor, bgColor)
        }
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
    for (i in posX..posX + string.length - 1) {
        Assert.assertEquals(panel.chars[i][posY].glyph, string[i - posX])
        if (color != null)
            Assert.assertEquals(panel.chars[i][posY].glyphColor, color)
        if (bgColor != null)
            Assert.assertEquals(panel.chars[i][posY].bgColor, bgColor)
    }
}