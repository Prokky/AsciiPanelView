package com.prokkypew.sample

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.prokkypew.asciipanelview.AsciiPanelView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AsciiPanelView.OnCharClickedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<AsciiPanelView>(R.id.panel)
                .writeChar('g', 2, 2, Color.RED, Color.YELLOW)
                .setCursorPosition(14, 15)
                .writeCharWithColor('p', Color.RED, Color.YELLOW)
                .writeString("We", 16, 17, Color.RED, Color.YELLOW)
                .writeCenter("Center TEXT String", 5, Color.YELLOW, Color.CYAN)
                .onCharClickedListener = this
    }

    override fun onCharClicked(x: Int?, y: Int?, char: AsciiPanelView.ColoredChar) {
        Log.d("char", "char clicked:" + x + ":" + y + " = " + char.char)
        panel.clearRect(' ', 0, 1, panel.panelWidth, 1)
                .writeCenter("x:" + x + " y:" + y + " char:" + char.char, 1)
    }
}
