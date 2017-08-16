package com.prokkypew.asciipanelview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


/**
 * An implementation of terminal view for oldschool games
 * @author Alexander Roman
 */

class AsciiPanelView : View {
    companion object {
        const val DEFAULT_PANEL_WIDTH: Int = 64
        const val DEFAULT_PANEL_HEIGHT: Int = 27
        const val DEFAULT_GLYPH_COLOR: Int = Color.BLACK
        const val DEFAULT_bgColor_COLOR: Int = Color.WHITE
        const val DEFAULT_FONT: String = "font.ttf"
    }

    var panelWidth: Int = DEFAULT_PANEL_WIDTH
    var panelHeight: Int = DEFAULT_PANEL_HEIGHT
    var basicGlyphColor: Int = DEFAULT_GLYPH_COLOR
    var basicBgColor: Int = DEFAULT_bgColor_COLOR
    lateinit var chars: Array<Array<ColoredChar>>
    var tileWidth: Float = 0f
    var tileHeight: Float = 0f
    var textPaint: Paint = Paint()
    var textBgPaint: Paint = Paint()
    var cursorX: Int = 0
    var cursorY: Int = 0
    var widthCompensation: Float = 0f
    var fontFamily: String = DEFAULT_FONT

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        readAttributes(attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        readAttributes(attrs)
        init()
    }

    fun readAttributes(attrs: AttributeSet) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.AsciiPanelView)
        try {
            panelWidth = ta.getInt(R.styleable.AsciiPanelView_panelWidth, DEFAULT_PANEL_WIDTH)
            panelHeight = ta.getInt(R.styleable.AsciiPanelView_panelHeight, DEFAULT_PANEL_HEIGHT)
            basicGlyphColor = ta.getColor(R.styleable.AsciiPanelView_defaultGlyphColor, DEFAULT_GLYPH_COLOR)
            basicBgColor = ta.getColor(R.styleable.AsciiPanelView_defaultBackgroundColor, DEFAULT_bgColor_COLOR)
            if (ta.hasValue(R.styleable.AsciiPanelView_fontFamily))
                fontFamily = ta.getString(R.styleable.AsciiPanelView_fontFamily)
        } finally {
            ta.recycle()
        }
    }

    fun init() {
        chars = Array(panelWidth) { Array(panelHeight) { ColoredChar(' ', basicGlyphColor, basicBgColor) } }

        val font = Typeface.create(Typeface.createFromAsset(context.assets, fontFamily), Typeface.BOLD)
        textPaint.typeface = font
    }

    override fun onSizeChanged(xNew: Int, yNew: Int, xOld: Int, yOld: Int) {
        super.onSizeChanged(xNew, yNew, xOld, yOld)
        tileWidth = xNew.toFloat() / panelWidth.toFloat()
        tileHeight = (yNew.toFloat()) / panelHeight.toFloat()
        textPaint.textSize = tileHeight
        val bounds = Rect()
        textPaint.getTextBounds("W", 0, 1, bounds)
        widthCompensation = (tileWidth - bounds.width()) / 2
    }

    override fun onDraw(canvas: Canvas) {
        for (w in 0..panelWidth - 1) {
            val posX = tileWidth * w
            for (h in 0..panelHeight - 1) {
                textBgPaint.color = chars[w][h].bgColor
                canvas.drawRect(posX, tileHeight * h, posX + tileWidth, tileHeight * h + tileHeight, textBgPaint)
            }
        }
        for (w in 0..panelWidth - 1) {
            val posX = tileWidth * w
            for (h in 0..panelHeight - 1) {
                textPaint.color = chars[w][h].glyphColor
                val posY = tileHeight * (h + 1) - textPaint.descent()
                canvas.drawText(chars[w][h].glyph.toString(), posX + widthCompensation, posY, textPaint)
            }
        }
    }

    fun setCursorPosX(cursorX: Int) {
        if (cursorX < 0 || cursorX >= panelWidth) throw IllegalArgumentException("cursorX $cursorX must be in range [0,$panelWidth).")

        this.cursorX = cursorX
    }

    fun setCursorPosY(cursorY: Int) {
        if (cursorY < 0 || cursorY >= panelHeight) throw IllegalArgumentException("cursorY $cursorY must be in range [0,$panelHeight).")

        this.cursorY = cursorY
    }

    fun setCursorPosition(x: Int, y: Int) {
        setCursorPosX(x)
        setCursorPosY(y)
    }

    fun writeChar(character: Char): AsciiPanelView {
        return writeChar(character, cursorX, cursorY, basicGlyphColor, basicBgColor)
    }

    fun writeChar(character: Char, glyphColor: Int): AsciiPanelView {
        return writeChar(character, cursorX, cursorY, glyphColor, basicBgColor)
    }

    fun writeCharWithColor(character: Char, glyphColor: Int, bgColor: Int): AsciiPanelView {
        return writeChar(character, cursorX, cursorY, glyphColor, bgColor)
    }

    fun writeCharWithPos(character: Char, x: Int, y: Int): AsciiPanelView {
        if (x < 0 || x >= panelWidth) throw IllegalArgumentException("x $x must be in range [0,$panelWidth)")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight)")

        return writeChar(character, x, y, basicGlyphColor, basicBgColor)
    }

    fun writeChar(character: Char, x: Int, y: Int, glyphColor: Int): AsciiPanelView {
        if (x < 0 || x >= panelWidth) throw IllegalArgumentException("x $x must be in range [0,$panelWidth)")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight)")

        return writeChar(character, x, y, glyphColor, basicBgColor)
    }

    fun writeChar(character: Char, x: Int, y: Int, glyphColor: Int?, bgColor: Int?): AsciiPanelView {
        if (x < 0 || x >= panelWidth) throw IllegalArgumentException("x $x must be in range [0,$panelWidth)")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight)")

        var gColor = glyphColor
        var bColor = bgColor

        if (gColor == null) gColor = basicGlyphColor
        if (bColor == null) bColor = basicBgColor

        chars[x][y] = ColoredChar(character, gColor, bColor)
        cursorX = x + 1
        cursorY = y
        return this
    }

    fun writeString(string: String): AsciiPanelView {
        if (cursorX + string.length > panelWidth) throw IllegalArgumentException("cursorX + string.length() " + (cursorX + string.length) + " must be less than " + panelWidth + ".")

        return writeString(string, cursorX, cursorY, basicGlyphColor, basicBgColor)
    }

    fun writeString(string: String, glyphColor: Int): AsciiPanelView {
        if (cursorX + string.length > panelWidth) throw IllegalArgumentException("cursorX + string.length() " + (cursorX + string.length) + " must be less than " + panelWidth + ".")

        return writeString(string, cursorX, cursorY, glyphColor, basicBgColor)
    }

    fun writeStringWithColor(string: String, glyphColor: Int, bgColor: Int): AsciiPanelView {
        if (cursorX + string.length > panelWidth) throw IllegalArgumentException("cursorX + string.length() " + (cursorX + string.length) + " must be less than " + panelWidth + ".")

        return writeString(string, cursorX, cursorY, glyphColor, bgColor)
    }

    fun writeStringWithPos(string: String, x: Int, y: Int): AsciiPanelView {
        if (x + string.length > panelWidth) throw IllegalArgumentException("x + string.length() " + (x + string.length) + " must be less than " + panelWidth + ".")
        if (x < 0 || x >= panelWidth) throw IllegalArgumentException("x $x must be in range [0,$panelWidth)")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight)")

        return writeString(string, x, y, basicGlyphColor, basicBgColor)
    }

    fun writeString(string: String, x: Int, y: Int, glyphColor: Int): AsciiPanelView {
        if (x + string.length > panelWidth) throw IllegalArgumentException("x + string.length() " + (x + string.length) + " must be less than " + panelWidth + ".")
        if (x < 0 || x >= panelWidth) throw IllegalArgumentException("x $x must be in range [0,$panelWidth)")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight)")

        return writeString(string, x, y, glyphColor, basicBgColor)
    }

    fun writeString(string: String, x: Int, y: Int, glyphColor: Int?, bgColor: Int?): AsciiPanelView {
        if (x + string.length > panelWidth) throw IllegalArgumentException("x + string.length() " + (x + string.length) + " must be less than " + panelWidth + ".")
        if (x < 0 || x >= panelWidth) throw IllegalArgumentException("x $x must be in range [0,$panelWidth).")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight).")

        var gColor = glyphColor
        var bColor = bgColor

        if (gColor == null) gColor = basicGlyphColor

        if (bColor == null) bColor = basicBgColor

        for (i in 0..string.length - 1) {
            writeChar(string[i], x + i, y, gColor, bColor)
        }
        return this
    }

    fun clear(): AsciiPanelView {
        return clearRect(' ', 0, 0, panelWidth, panelHeight, basicGlyphColor, basicBgColor)
    }

    fun clear(character: Char): AsciiPanelView {
        return clearRect(character, 0, 0, panelWidth, panelHeight, basicGlyphColor, basicBgColor)
    }

    fun clear(character: Char, glyphColor: Int, bgColor: Int): AsciiPanelView {
        return clearRect(character, 0, 0, panelWidth, panelHeight, glyphColor, bgColor)
    }

    fun clearRect(character: Char, x: Int, y: Int, width: Int, height: Int): AsciiPanelView {
        if (x < 0 || x >= panelWidth) throw IllegalArgumentException("x $x must be in range [0,$panelWidth).")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight).")
        if (width < 1) throw IllegalArgumentException("width $width must be greater than 0.")
        if (height < 1) throw IllegalArgumentException("height $height must be greater than 0.")
        if (x + width > panelWidth) throw IllegalArgumentException("x + width " + (x + width) + " must be less than " + (panelWidth + 1) + ".")
        if (y + height > panelHeight) throw IllegalArgumentException("y + height " + (y + height) + " must be less than " + (panelHeight + 1) + ".")

        return clearRect(character, x, y, width, height, basicGlyphColor, basicBgColor)
    }

    fun clearRect(character: Char, x: Int, y: Int, width: Int, height: Int, glyphColor: Int, bgColor: Int): AsciiPanelView {
        if (x < 0 || x >= panelWidth) throw IllegalArgumentException("x $x must be in range [0,$panelWidth)")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight)")
        if (width < 1) throw IllegalArgumentException("width $width must be greater than 0.")
        if (height < 1) throw IllegalArgumentException("height $height must be greater than 0.")
        if (x + width > panelWidth) throw IllegalArgumentException("x + width " + (x + width) + " must be less than " + (panelWidth + 1) + ".")
        if (y + height > panelHeight) throw IllegalArgumentException("y + height " + (y + height) + " must be less than " + (panelHeight + 1) + ".")

        val originalCursorX = cursorX
        val originalCursorY = cursorY
        for (xo in x..x + width - 1) {
            for (yo in y..y + height - 1) {
                writeChar(character, xo, yo, glyphColor, bgColor)
            }
        }
        cursorX = originalCursorX
        cursorY = originalCursorY

        return this
    }

    fun writeCenter(string: String, y: Int): AsciiPanelView {
        if (string.length > panelWidth) throw IllegalArgumentException("string.length() " + string.length + " must be less than " + panelWidth + ".")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight)")

        val x = (panelWidth - string.length) / 2

        return writeString(string, x, y, basicGlyphColor, basicBgColor)
    }

    fun writeCenter(string: String, y: Int, glyphColor: Int): AsciiPanelView {
        if (string.length > panelWidth) throw IllegalArgumentException("string.length() " + string.length + " must be less than " + panelWidth + ".")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight)")

        val x = (panelWidth - string.length) / 2

        return writeString(string, x, y, glyphColor, basicBgColor)
    }

    fun writeCenter(string: String, y: Int, glyphColor: Int?, bgColor: Int?): AsciiPanelView {
        if (string.length > panelWidth) throw IllegalArgumentException("string.length() " + string.length + " must be less than " + panelWidth + ".")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight).")

        var gColor = glyphColor
        var bColor = bgColor

        val x = (panelWidth - string.length) / 2

        if (gColor == null) gColor = basicGlyphColor

        if (bColor == null) bColor = basicBgColor

        for (i in 0..string.length - 1) {
            writeChar(string[i], x + i, y, gColor, bColor)
        }
        return this
    }

    class ColoredChar(var glyph: Char, var glyphColor: Int, var bgColor: Int)
}