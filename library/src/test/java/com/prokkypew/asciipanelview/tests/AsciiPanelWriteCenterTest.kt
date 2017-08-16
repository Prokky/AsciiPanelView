package com.prokkypew.infinitecavystory.tests

import android.graphics.Color
import com.prokkypew.infinitecavystory.AsciiPanelView
import com.prokkypew.infinitecavystory.checkCorrectString
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
class AsciiPanelWriteCenterTest {
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
