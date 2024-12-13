package com.dicoding.capstoneprojek.ui.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.capstoneprojek.R

class NameEditText : AppCompatEditText, View.OnTouchListener {

    private lateinit var nameIcon: Drawable
    private lateinit var clearButtonImage: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        nameIcon = ContextCompat.getDrawable(context, R.drawable.ic_person_24) as Drawable
        setEditCompoundDrawables(startOfTheText = nameIcon)

        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_clear_24) as Drawable

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    setError(resources.getString(R.string.empty), null)
                    hideClearButton()
                } else if (s.length < 3) {
                    setError(resources.getString(R.string.too_short), null)
                    showClearButton()
                } else {
                    error = null
                    showClearButton()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        setOnTouchListener(this)
    }

    private fun showClearButton() {
        setEditCompoundDrawables(startOfTheText = nameIcon, endOfTheText = clearButtonImage)
    }

    private fun hideClearButton() {
        setEditCompoundDrawables(startOfTheText = nameIcon)
    }

    private fun setEditCompoundDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearButtonImage.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearButtonEnd -> isClearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - clearButtonImage.intrinsicWidth).toFloat()
                when {
                    event.x > clearButtonStart -> isClearButtonClicked = true
                }
            }
            if (isClearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        showClearButton()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        text?.clear()
                        hideClearButton()
                        return true
                    }
                    else -> return false
                }
            } else return false
        }
        return false
    }
}
