package com.prokkypew.sample

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.prokkypew.asciipanelview.AsciiPanelView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<AsciiPanelView>(R.id.panel)
                .writeString("g", 0, 0, Color.RED, Color.YELLOW)
                .writeString("p", 14, 15, Color.RED, Color.YELLOW)
                .writeString("We", 16, 17, Color.RED, Color.YELLOW)
                .writeCenter("Center TEXT String", 5, Color.YELLOW, Color.CYAN)
    }
}
