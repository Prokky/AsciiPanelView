package com.prokkypew.asciipanelview.tests

import android.graphics.Color
import com.prokkypew.asciipanelview.AsciiPanelView
import com.prokkypew.asciipanelview.CustomTestRunner
import com.prokkypew.asciipanelview.checkCorrectString
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(CustomTestRunner::class)
@Config(sdk = intArrayOf(26))
class AsciiPanelWriteCenterTest {
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
    fun checkWriteCenterString() {
        val string = "some long test text"
        panel.writeCenter(string, 1)
        val x = (panel.panelWidth - string.length) / 2
        checkCorrectString(panel, string, x, 1, null, null)

        panel.writeCenter(string, 3, Color.RED)
        checkCorrectString(panel, string, x, 3, Color.RED, null)

        panel.writeCenter(string, 5, Color.YELLOW, Color.BLUE)
        checkCorrectString(panel, string, x, 5, Color.YELLOW, Color.BLUE)
    }
}
