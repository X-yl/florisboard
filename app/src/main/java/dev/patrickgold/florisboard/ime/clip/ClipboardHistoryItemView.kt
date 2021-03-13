package dev.patrickgold.florisboard.ime.clip

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.theme.Theme
import dev.patrickgold.florisboard.ime.theme.ThemeManager

class ClipboardHistoryItemView: ConstraintLayout, ThemeManager.OnThemeUpdatedListener {

    lateinit var keyboardView: ClipboardHistoryView
    constructor(context: Context) : this(context, null as AttributeSet?)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var popupManager: ClipboardPopupManager? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        popupManager = ClipboardPopupManager(keyboardView, FlorisBoard.getInstance().popupLayerView, this)

        setOnClickListener{
            onClickItem(this)
        }

        setOnLongClickListener{
            onLongClickItem()
        }

        val themeManager = ThemeManager.default()
        themeManager.registerOnThemeUpdatedListener(this)
    }

    override fun onThemeUpdated(theme: Theme) {
        background.setTint(theme.getAttr(Theme.Attr.KEY_BACKGROUND).toSolidColor().color)
        val pin = findViewById<ImageView>(R.id.clipboard_pin).drawable
        pin?.setTint(theme.getAttr(Theme.Attr.KEY_FOREGROUND).toSolidColor().color)
    }


    private fun onLongClickItem() : Boolean {
        popupManager?.show(this)
        return true
    }

    private fun onClickItem(view: View){
        val florisBoard = FlorisBoard.getInstance()
        val position = florisBoard.clipInputManager.getPositionOfView(view)
        florisBoard.florisClipboardManager!!
            .changeCurrent(florisBoard.florisClipboardManager!!.peekHistoryOrPin(position))
    }

    fun setPinned() {
        val view = findViewById<TextView>(R.id.clipboard_history_item_text)
        view?.run {
            val params = layoutParams as LayoutParams
            params.marginEnd = resources.getDimensionPixelSize(R.dimen.clipboard_text_item_pin_margin)
            layoutParams = params
        }
        findViewById<ImageView>(R.id.clipboard_pin).visibility = VISIBLE
        invalidate()
        val themeManager = ThemeManager.default()
        onThemeUpdated(themeManager.activeTheme)
    }

    fun setUnpinned(){
        val view = findViewById<TextView>(R.id.clipboard_history_item_text)
        // if text view, also update margin.
        view?.run {
            val params = layoutParams as LayoutParams
            params.marginEnd = 0
            layoutParams = params
            invalidate()
        }
        findViewById<ImageView>(R.id.clipboard_pin).visibility = INVISIBLE
    }
}