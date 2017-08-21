package com.prokkypew.asciipanelview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.prokkypew.asciipanelview.AsciiPanelView.ColoredChar
import com.prokkypew.asciipanelview.AsciiPanelView.OnCharClickedListener

/**
 * An implementation of terminal view for old-school games.
 * Initially ported from JPanel AsciiPanel https://github.com/trystan/AsciiPanel to Android View
 *
 * @author Alexander Roman
 *
 * @property chars matrix of [ColoredChar] to be drawn on the panel
 * @property panelWidth width of panel in chars
 * @property panelHeight height of panel in chars
 * @property charColor color of chars to be drawn if not specified
 * @property bgColor color of char background to be drawn if not specified
 * @property cursorX position X of cursor to draw next char at
 * @property cursorY position Y of cursor to draw next char at
 * @property fontFamily font file name to use for drawing chars
 * @property onCharClickedListener interface of [OnCharClickedListener] to be called on panel char click
 */

class AsciiPanelView : View {
    companion object {
        /**
         * Default panel width in chars = 64
         */
        const val DEFAULT_PANEL_WIDTH: Int = 64
        /**
         * Default panel height in chars = 27
         */
        const val DEFAULT_PANEL_HEIGHT: Int = 27
        /**
         * Default char color = [Color.BLACK]
         */
        const val DEFAULT_CHAR_COLOR: Int = Color.BLACK
        /**
         * Default char background color = [Color.WHITE]
         */
        const val DEFAULT_BG_COLOR: Int = Color.WHITE
        /**
         * Default font
         */
        const val DEFAULT_FONT: String = "font.ttf"

        private const val CLICK_ACTION_THRESHOLD: Int = 200
    }

    private var tileWidth: Float = 0f
    private var tileHeight: Float = 0f
    private var textPaint: Paint = Paint()
    private var textBgPaint: Paint = Paint()
    private var widthCompensation: Float = 0f
    private var lastTouchDown: Long = 0

    lateinit var chars: Array<Array<ColoredChar>>

    var onCharClickedListener: OnCharClickedListener? = null
    var panelWidth: Int = DEFAULT_PANEL_WIDTH
    var panelHeight: Int = DEFAULT_PANEL_HEIGHT
    var charColor: Int = DEFAULT_CHAR_COLOR
    var bgColor: Int = DEFAULT_BG_COLOR
    var cursorX: Int = 0
    var cursorY: Int = 0
    var fontFamily: String = DEFAULT_FONT

    /**
     *@constructor Default View constructors by context
     */
    constructor(context: Context) : super(context) {
        init()
    }

    /**
     *@constructor Default View constructors by context and attributes
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        readAttributes(attrs)
        init()
    }

    /**
     *@constructor Default View constructors by context, attributes and default style
     */
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        readAttributes(attrs)
        init()
    }


    private fun readAttributes(attrs: AttributeSet) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.AsciiPanelView)
        try {
            panelWidth = ta.getInt(R.styleable.AsciiPanelView_panelWidth, DEFAULT_PANEL_WIDTH)
            panelHeight = ta.getInt(R.styleable.AsciiPanelView_panelHeight, DEFAULT_PANEL_HEIGHT)
            charColor = ta.getColor(R.styleable.AsciiPanelView_defaultCharColor, DEFAULT_CHAR_COLOR)
            bgColor = ta.getColor(R.styleable.AsciiPanelView_defaultBackgroundColor, DEFAULT_BG_COLOR)
            if (ta.hasValue(R.styleable.AsciiPanelView_fontFamily))
                fontFamily = ta.getString(R.styleable.AsciiPanelView_fontFamily)
        } finally {
            ta.recycle()
        }
    }

    private fun init() {
        chars = Array(panelWidth) { Array(panelHeight) { ColoredChar(' ', charColor, bgColor) } }

        val font = Typeface.create(Typeface.createFromAsset(context.assets, fontFamily), Typeface.BOLD)
        textPaint.typeface = font
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> lastTouchDown = System.currentTimeMillis()
            MotionEvent.ACTION_UP -> if (System.currentTimeMillis() - lastTouchDown < CLICK_ACTION_THRESHOLD) {
                val x = event.x.div(tileWidth).toInt()
                val y = event.y.div(tileHeight).toInt()
                onCharClickedListener?.onCharClicked(x, y, chars[x][y])
            }
        }
        return true
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
        for (w in 0 until panelWidth) {
            val posX = tileWidth * w
            for (h in 0 until panelHeight) {
                textBgPaint.color = chars[w][h].bgColor
                canvas.drawRect(posX, tileHeight * h, posX + tileWidth, tileHeight * h + tileHeight, textBgPaint)
            }
        }
        for (w in 0 until panelWidth) {
            val posX = tileWidth * w
            for (h in 0 until panelHeight) {
                textPaint.color = chars[w][h].charColor
                val posY = tileHeight * (h + 1) - textPaint.descent()
                canvas.drawText(chars[w][h].char.toString(), posX + widthCompensation, posY, textPaint)
            }
        }
    }

    /**
     * Sets the distance from the left new text will be written to.
     * @param cursorX the distance from the left new text should be written to
     * @return this for convenient chaining of method calls
     */
    fun setCursorPosX(cursorX: Int): AsciiPanelView {
        if (cursorX < 0 || cursorX >= panelWidth) throw IllegalArgumentException("cursorX $cursorX must be in range [0,$panelWidth).")

        this.cursorX = cursorX
        return this
    }

    /**
     * Sets the distance from the top new text will be written to.
     * @param cursorY the distance from the top new text should be written to
     * @return this for convenient chaining of method calls
     */
    fun setCursorPosY(cursorY: Int): AsciiPanelView {
        if (cursorY < 0 || cursorY >= panelHeight) throw IllegalArgumentException("cursorY $cursorY must be in range [0,$panelHeight).")

        this.cursorY = cursorY
        return this
    }

    /**
     * Sets the x and y position of where new text will be written to. The origin (0,0) is the upper left corner.
     * @param x the distance from the left new text should be written to
     * @param y the distance from the top new text should be written to
     * @return this for convenient chaining of method calls
     */
    fun setCursorPosition(x: Int, y: Int): AsciiPanelView {
        setCursorPosX(x)
        setCursorPosY(y)
        return this
    }

    /**
     * Write a character to the cursor's position.
     * This updates the cursor's position.
     * @param character  the character to write
     * @return this for convenient chaining of method calls
     */
    fun writeChar(character: Char): AsciiPanelView {
        return writeChar(character, cursorX, cursorY, charColor, bgColor)
    }

    /**
     * Write a character to the cursor's position with the specified foreground color.
     * This updates the cursor's position but not the default foreground color.
     * @param character  the character to write
     * @param charColor the foreground color or null to use the default
     * @return this for convenient chaining of method calls
     */
    fun writeChar(character: Char, charColor: Int): AsciiPanelView {
        return writeChar(character, cursorX, cursorY, charColor, bgColor)
    }

    /**
     * Write a character to the cursor's position with the specified foreground and background colors.
     * This updates the cursor's position but not the default foreground or background colors.
     * @param character  the character to write
     * @param charColor the foreground color or null to use the default
     * @param bgColor the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    fun writeCharWithColor(character: Char, charColor: Int, bgColor: Int): AsciiPanelView {
        return writeChar(character, cursorX, cursorY, charColor, bgColor)
    }

    /**
     * Write a character to the specified position.
     * This updates the cursor's position.
     * @param character  the character to write
     * @param x          the distance from the left to begin writing from
     * @param y          the distance from the top to begin writing from
     * @return this for convenient chaining of method calls
     */
    fun writeCharWithPos(character: Char, x: Int, y: Int): AsciiPanelView {
        if (x < 0 || x >= panelWidth) throw IllegalArgumentException("x $x must be in range [0,$panelWidth)")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight)")

        return writeChar(character, x, y, charColor, bgColor)
    }

    /**
     * Write a character to the specified position with the specified foreground color.
     * This updates the cursor's position but not the default foreground color.
     * @param character  the character to write
     * @param x          the distance from the left to begin writing from
     * @param y          the distance from the top to begin writing from
     * @param charColor the foreground color or null to use the default
     * @return this for convenient chaining of method calls
     */
    fun writeChar(character: Char, x: Int, y: Int, charColor: Int): AsciiPanelView {
        if (x < 0 || x >= panelWidth) throw IllegalArgumentException("x $x must be in range [0,$panelWidth)")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight)")

        return writeChar(character, x, y, charColor, bgColor)
    }

    /**
     * Write a character to the specified position with the specified foreground and background colors.
     * This updates the cursor's position but not the default foreground or background colors.
     * @param character  the character to write
     * @param x          the distance from the left to begin writing from
     * @param y          the distance from the top to begin writing from
     * @param charColor the foreground color or null to use the default
     * @param bgColor the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    fun writeChar(character: Char, x: Int, y: Int, charColor: Int?, bgColor: Int?): AsciiPanelView {
        if (x < 0 || x >= panelWidth) throw IllegalArgumentException("x $x must be in range [0,$panelWidth)")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight)")

        var gColor = charColor
        var bColor = bgColor

        if (gColor == null) gColor = this.charColor
        if (bColor == null) bColor = this.bgColor

        chars[x][y] = ColoredChar(character, gColor, bColor)
        cursorX = x + 1
        cursorY = y

        invalidate()

        return this
    }

    /**
     * Write a string to the cursor's position.
     * This updates the cursor's position.
     * @param string     the string to write
     * @return this for convenient chaining of method calls
     */
    fun writeString(string: String): AsciiPanelView {
        if (cursorX + string.length > panelWidth) throw IllegalArgumentException("cursorX + string.length() " + (cursorX + string.length) + " must be less than " + panelWidth + ".")

        return writeString(string, cursorX, cursorY, charColor, bgColor)
    }

    /**
     * Write a string to the cursor's position with the specified foreground color.
     * This updates the cursor's position but not the default foreground color.
     * @param string     the string to write
     * @param charColor the foreground color or null to use the default
     * @return this for convenient chaining of method calls
     */
    fun writeString(string: String, charColor: Int): AsciiPanelView {
        if (cursorX + string.length > panelWidth) throw IllegalArgumentException("cursorX + string.length() " + (cursorX + string.length) + " must be less than " + panelWidth + ".")

        return writeString(string, cursorX, cursorY, charColor, bgColor)
    }

    /**
     * Write a string to the cursor's position with the specified foreground and background colors.
     * This updates the cursor's position but not the default foreground or background colors.
     * @param string     the string to write
     * @param charColor the foreground color or null to use the default
     * @param bgColor the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    fun writeStringWithColor(string: String, charColor: Int, bgColor: Int): AsciiPanelView {
        if (cursorX + string.length > panelWidth) throw IllegalArgumentException("cursorX + string.length() " + (cursorX + string.length) + " must be less than " + panelWidth + ".")

        return writeString(string, cursorX, cursorY, charColor, bgColor)
    }

    /**
     * Write a string to the specified position.
     * This updates the cursor's position.
     * @param string     the string to write
     * @param x          the distance from the left to begin writing from
     * @param y          the distance from the top to begin writing from
     * @return this for convenient chaining of method calls
     */
    fun writeStringWithPos(string: String, x: Int, y: Int): AsciiPanelView {
        if (x + string.length > panelWidth) throw IllegalArgumentException("x + string.length() " + (x + string.length) + " must be less than " + panelWidth + ".")
        if (x < 0 || x >= panelWidth) throw IllegalArgumentException("x $x must be in range [0,$panelWidth)")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight)")

        return writeString(string, x, y, charColor, bgColor)
    }

    /**
     * Write a string to the specified position with the specified foreground color.
     * This updates the cursor's position but not the default foreground color.
     * @param string     the string to write
     * @param x          the distance from the left to begin writing from
     * @param y          the distance from the top to begin writing from
     * @param charColor the foreground color or null to use the default
     * @return this for convenient chaining of method calls
     */
    fun writeString(string: String, x: Int, y: Int, charColor: Int): AsciiPanelView {
        if (x + string.length > panelWidth) throw IllegalArgumentException("x + string.length() " + (x + string.length) + " must be less than " + panelWidth + ".")
        if (x < 0 || x >= panelWidth) throw IllegalArgumentException("x $x must be in range [0,$panelWidth)")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight)")

        return writeString(string, x, y, charColor, bgColor)
    }

    /**
     * Write a string to the specified position with the specified foreground and background colors.
     * This updates the cursor's position but not the default foreground or background colors.
     * @param string     the string to write
     * @param x          the distance from the left to begin writing from
     * @param y          the distance from the top to begin writing from
     * @param charColor the foreground color or null to use the default
     * @param bgColor the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    fun writeString(string: String, x: Int, y: Int, charColor: Int?, bgColor: Int?): AsciiPanelView {
        if (x + string.length > panelWidth) throw IllegalArgumentException("x + string.length() " + (x + string.length) + " must be less than " + panelWidth + ".")
        if (x < 0 || x >= panelWidth) throw IllegalArgumentException("x $x must be in range [0,$panelWidth).")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight).")

        var gColor = charColor
        var bColor = bgColor

        if (gColor == null) gColor = this.charColor

        if (bColor == null) bColor = this.bgColor

        for (i in 0 until string.length) {
            writeChar(string[i], x + i, y, gColor, bColor)
        }
        return this
    }

    /**
     * Clear the entire screen to whatever the default background color is.
     * @return this for convenient chaining of method calls
     */
    fun clear(): AsciiPanelView {
        return clearRect(' ', 0, 0, panelWidth, panelHeight, charColor, bgColor)
    }

    /**
     * Clear the entire screen with the specified character and whatever the default foreground and background colors are.
     * @param character  the character to write
     * @return this for convenient chaining of method calls
     */
    fun clear(character: Char): AsciiPanelView {
        return clearRect(character, 0, 0, panelWidth, panelHeight, charColor, bgColor)
    }

    /**
     * Clear the entire screen with the specified character and whatever the specified foreground and background colors are.
     * @param character  the character to write
     * @param charColor the foreground color or null to use the default
     * @param bgColor the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    fun clear(character: Char, charColor: Int, bgColor: Int): AsciiPanelView {
        return clearRect(character, 0, 0, panelWidth, panelHeight, charColor, bgColor)
    }

    /**
     * Clear the section of the screen with the specified character and whatever the default foreground and background colors are.
     * The cursor position will not be modified.
     * @param character  the character to write
     * @param x          the distance from the left to begin writing from
     * @param y          the distance from the top to begin writing from
     * @param width      the height of the section to clear
     * @param height     the width of the section to clear
     * @return this for convenient chaining of method calls
     */
    fun clearRect(character: Char, x: Int, y: Int, width: Int, height: Int): AsciiPanelView {
        if (x < 0 || x >= panelWidth) throw IllegalArgumentException("x $x must be in range [0,$panelWidth).")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight).")
        if (width < 1) throw IllegalArgumentException("width $width must be greater than 0.")
        if (height < 1) throw IllegalArgumentException("height $height must be greater than 0.")
        if (x + width > panelWidth) throw IllegalArgumentException("x + width " + (x + width) + " must be less than " + (panelWidth + 1) + ".")
        if (y + height > panelHeight) throw IllegalArgumentException("y + height " + (y + height) + " must be less than " + (panelHeight + 1) + ".")

        return clearRect(character, x, y, width, height, charColor, bgColor)
    }

    /**
     * Clear the section of the screen with the specified character and whatever the specified foreground and background colors are.
     * @param character  the character to write
     * @param x          the distance from the left to begin writing from
     * @param y          the distance from the top to begin writing from
     * @param width      the height of the section to clear
     * @param height     the width of the section to clear
     * @param charColor the foreground color or null to use the default
     * @param bgColor the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    fun clearRect(character: Char, x: Int, y: Int, width: Int, height: Int, charColor: Int, bgColor: Int): AsciiPanelView {
        if (x < 0 || x >= panelWidth) throw IllegalArgumentException("x $x must be in range [0,$panelWidth)")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight)")
        if (width < 1) throw IllegalArgumentException("width $width must be greater than 0.")
        if (height < 1) throw IllegalArgumentException("height $height must be greater than 0.")
        if (x + width > panelWidth) throw IllegalArgumentException("x + width " + (x + width) + " must be less than " + (panelWidth + 1) + ".")
        if (y + height > panelHeight) throw IllegalArgumentException("y + height " + (y + height) + " must be less than " + (panelHeight + 1) + ".")

        val originalCursorX = cursorX
        val originalCursorY = cursorY
        for (xo in x until x + width) {
            for (yo in y until y + height) {
                writeChar(character, xo, yo, charColor, bgColor)
            }
        }
        cursorX = originalCursorX
        cursorY = originalCursorY

        return this
    }

    /**
     * Write a string to the center of the panel at the specified y position.
     * This updates the cursor's position.
     * @param string     the string to write
     * @param y          the distance from the top to begin writing from
     * @return this for convenient chaining of method calls
     */
    fun writeCenter(string: String, y: Int): AsciiPanelView {
        if (string.length > panelWidth) throw IllegalArgumentException("string.length() " + string.length + " must be less than " + panelWidth + ".")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight)")

        val x = (panelWidth - string.length) / 2

        return writeString(string, x, y, charColor, bgColor)
    }

    /**
     * Write a string to the center of the panel at the specified y position with the specified foreground color.
     * This updates the cursor's position but not the default foreground color.
     * @param string     the string to write
     * @param y          the distance from the top to begin writing from
     * @param charColor the foreground color or null to use the default
     * @return this for convenient chaining of method calls
     */
    fun writeCenter(string: String, y: Int, charColor: Int): AsciiPanelView {
        if (string.length > panelWidth) throw IllegalArgumentException("string.length() " + string.length + " must be less than " + panelWidth + ".")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight)")

        val x = (panelWidth - string.length) / 2

        return writeString(string, x, y, charColor, bgColor)
    }

    /**
     * Write a string to the center of the panel at the specified y position with the specified foreground and background colors.
     * This updates the cursor's position but not the default foreground or background colors.
     * @param string     the string to write
     * @param y          the distance from the top to begin writing from
     * @param charColor the foreground color or null to use the default
     * @param bgColor the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    fun writeCenter(string: String, y: Int, charColor: Int?, bgColor: Int?): AsciiPanelView {
        if (string.length > panelWidth) throw IllegalArgumentException("string.length() " + string.length + " must be less than " + panelWidth + ".")
        if (y < 0 || y >= panelHeight) throw IllegalArgumentException("y $y must be in range [0,$panelHeight).")

        var gColor = charColor
        var bColor = bgColor

        val x = (panelWidth - string.length) / 2

        if (gColor == null) gColor = this.charColor

        if (bColor == null) bColor = this.bgColor

        for (i in 0 until string.length) {
            writeChar(string[i], x + i, y, gColor, bColor)
        }
        return this
    }

    /**
     * Interface to be called on panel character click
     */
    interface OnCharClickedListener {
        /**
         * Callback, which is called when panel is clicked
         * @param x position of char clicked
         * @param y position of char clicked
         * @param char object of [ColoredChar] clicked
         */
        fun onCharClicked(x: Int?, y: Int?, char: ColoredChar)
    }

    /**
     * Object for chars to print on the panel
     * @property char char to print
     * @property charColor color of the char
     * @property bgColor color of background of the char
     */
    class ColoredChar(var char: Char, var charColor: Int, var bgColor: Int)
}