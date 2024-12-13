package com.dicoding.capstoneprojek.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.capstoneprojek.R
import com.dicoding.capstoneprojek.data.result.ResultCode
import com.dicoding.capstoneprojek.databinding.ActivityLoginBinding
import com.dicoding.capstoneprojek.ui.ViewModel.FactoryViewModel
import com.dicoding.capstoneprojek.ui.main.MainActivity
import com.dicoding.capstoneprojek.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<ViewModelLogin> {
        FactoryViewModel.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
        setupCreateAccountText()
    }

    private fun setupCreateAccountText() {
        val text = SpannableString("Belum punya akun? Buat akun")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = ContextCompat.getColor(this@LoginActivity, R.color.teal_200)
            }
        }

        val startIndex = text.indexOf("Buat akun")
        val endIndex = startIndex + "Buat akun".length
        text.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvCreateAccount.apply {
            setText(text, TextView.BufferType.SPANNABLE)
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val account = binding.edtAccount.text.toString()
            val password = binding.edtPassword.text.toString()

            if (account.isEmpty() || password.isEmpty()) {
                showErrorDialog(getString(R.string.error_empty_fields))
                return@setOnClickListener
            }

            showLoading(true)
            viewModel.login(account, password).observe(this) { loginResponse ->
                showLoading(false)
                println(loginResponse)
                when (loginResponse) {
                    is ResultCode.Error -> showErrorDialog(loginResponse.error)
                    is ResultCode.Success -> showSuccessDialog(loginResponse.data.account)
                    ResultCode.Loading -> Unit
                }
            }
        }
    }

    private fun showSuccessDialog(name: String) {
        if (isFinishing || isDestroyed) return // Pastikan activity masih hidup
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.success))
            setMessage(getString(R.string.pop_up_success, name))
            setPositiveButton(getString(R.string.next)) { _, _ ->
                startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                })
                finish()
            }
            create()
            show()
        }
    }

    private fun showErrorDialog(errorMessage: String) {
        if (isFinishing || isDestroyed) return // Pastikan activity masih hidup
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.login_error))
            setMessage(errorMessage)
            setPositiveButton(getString(R.string.done), null)
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
    }

    private fun playAnimation() {
        val translationAnim = ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        val fadeInLoginText = ObjectAnimator.ofFloat(binding.LoginTextView, View.ALPHA, 1f).setDuration(100)
        val fadeInButton = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(fadeInLoginText, fadeInButton)
            start()
        }

        translationAnim.start()
    }
}
