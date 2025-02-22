package com.dicoding.capstoneprojek.ui.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.capstoneprojek.R

class AccountEditText : AppCompatEditText, View.OnTouchListener {

    private lateinit var emailIcon: Drawable
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
        emailIcon = ContextCompat.getDrawable(context, R.drawable.ic_person_24) as Drawable
        setEditCompoundDrawables(startOfTheText = emailIcon)

        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_clear_24) as Drawable

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when {
                    s.isNullOrEmpty() -> {
                        error = resources.getString(R.string.input_empty) // Tampilkan error jika kosong
                        hideClearButton()
                    }
                    s.contains(" ") -> {
                        error = resources.getString(R.string.input_no_spaces) // Tidak boleh ada spasi
                        showClearButton()
                    }
                    !isValidEmailOrUsername(s) -> {
                        error = resources.getString(R.string.input_invalid) // Error untuk input tidak valid
                        showClearButton()
                    }
                    else -> {
                        error = null // Input valid
                        showClearButton()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        setOnTouchListener(this)
    }

    private fun isValidEmailOrUsername(input: CharSequence): Boolean {
        return if (input.contains("@")) {
            // Jika ada karakter '@', validasi sebagai email
            Patterns.EMAIL_ADDRESS.matcher(input).matches()
        } else {
            // Jika tidak, validasi sebagai username (contoh: panjang >= 4 karakter)
            input.length >= 4
        }
    }

    private fun showClearButton() {
        setEditCompoundDrawables(startOfTheText = emailIcon, endOfTheText = clearButtonImage)
    }

    private fun hideClearButton() {
        setEditCompoundDrawables(startOfTheText = emailIcon)
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
