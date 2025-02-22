package com.dicoding.capstoneprojek.ui.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.capstoneprojek.R

class PasswordEditText : AppCompatEditText, View.OnTouchListener {

    private lateinit var passwordButton: Drawable
    private lateinit var passwordIcon: Drawable
    private var isPasswordVisible = false

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
        setOnTouchListener(this)

        passwordButton = ContextCompat.getDrawable(context, if (!isPasswordVisible) R.drawable.ic_visibility_24 else R.drawable.ic_visibility_off_24) as Drawable
        passwordIcon = ContextCompat.getDrawable(context, R.drawable.ic_lock_24) as Drawable
        setEditCompoundDrawables(endOfTheText = passwordButton, startOfTheText = passwordIcon)

        transformationMethod = PasswordTransformationMethod.getInstance()

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().length < 6) {
                    setError(resources.getString(R.string.minimum_char), null)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
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
            val passwordButtonStart: Float
            val passwordButtonEnd: Float
            var isPasswordButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                passwordButtonEnd = (passwordButton.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < passwordButtonEnd -> isPasswordButtonClicked = true
                }
            } else {
                passwordButtonStart = (width - paddingEnd - passwordButton.intrinsicWidth).toFloat()
                when {
                    event.x > passwordButtonStart -> isPasswordButtonClicked = true
                }
            }

            if (isPasswordButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        passwordButton = ContextCompat.getDrawable(
                            context,
                            if (isPasswordVisible) R.drawable.ic_visibility_off_24 else R.drawable.ic_visibility_24
                        ) as Drawable
                        setEditCompoundDrawables(endOfTheText = passwordButton, startOfTheText = passwordIcon)
                        // Toggle password visibility
                        transformationMethod = if (isPasswordVisible) {
                            PasswordTransformationMethod.getInstance()
                        } else {
                            null
                        }
                        isPasswordVisible = !isPasswordVisible
                    }
                }
                return true
            }
        }
        return false
    }
}
