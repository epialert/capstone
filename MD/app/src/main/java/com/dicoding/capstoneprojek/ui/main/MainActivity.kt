@file:Suppress("DEPRECATION")

package com.dicoding.capstoneprojek.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.capstoneprojek.helper.ImageClassifierHelper
import com.dicoding.capstoneprojek.R
import com.dicoding.capstoneprojek.databinding.ActivityMainBinding
import com.dicoding.capstoneprojek.helper.ClassificationResult
import com.dicoding.capstoneprojek.ui.ViewModel.FactoryViewModel
import com.dicoding.capstoneprojek.ui.login.LoginActivity
import com.dicoding.capstoneprojek.ui.result.ResultActivity
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentImageUri?.let {
            outState.putString("current_image_uri", it.toString())
        }
    }
    private val viewModel by viewModels<ViewModelMain> {
        FactoryViewModel.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null

    private var exitDialog: AlertDialog? = null
    private var logoutDialog: AlertDialog? = null

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "EpiAlert"
        }

        setupView()
        setupObservers()
        setupAction()


        onBackPressedDispatcher.addCallback(this) {
            showExitConfirmationDialog()
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLoggedIn) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        viewModel.imageUri.observe(this) { uri ->
            if (uri != null) {
                binding.previewImageView.setImageURI(uri)
                binding.previewImageView.background = null // Pastikan background dihapus
                binding.analyzeButton.visibility = View.VISIBLE
            } else {
                binding.previewImageView.setImageDrawable(null)
                binding.previewImageView.background = getDrawable(R.drawable.ic_image_holder_24) // Atur latar belakang default jika diperlukan
                binding.analyzeButton.visibility = View.GONE
            }
        }

        binding.apply {
            analyzeButton.setOnClickListener {
                viewModel.imageUri.value?.let {
                    analyzeImage(it)
                } ?: showToast(getString(R.string.empty_image))
            }
            btnGallery.setOnClickListener {
                openGallery()
            }
            btnCamera.setOnClickListener {
                currentImageUri = getImageUri(this@MainActivity)
                cameraLaunch.launch(currentImageUri!!)
            }
        }

    }

    private fun openGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            uri?.let {
                viewModel.setImageUri(it)
                startCrop(it)
            } ?: Log.d("Photo Picker", "No media selected")
        }

    private fun startCrop(imageUri: Uri) {
        val uniqueFileName = "cropped_image_${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(cacheDir, uniqueFileName))

        val uCropIntent = UCrop.of(imageUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(224, 224)
            .getIntent(this)

        cropImageResultLauncher.launch(uCropIntent)
    }

    private val cropImageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val resultUri = UCrop.getOutput(result.data!!)
                resultUri?.let {
                    viewModel.setImageUri(it)
                    showImage() // Tampilkan gambar dengan background dihapus
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                val cropError: Throwable? = UCrop.getError(result.data!!)
                cropError?.let {
                    showToast(getString(R.string.load_failed))
                }
            }
        }


    private fun showImage() {
        viewModel.imageUri.value?.let {
            binding.previewImageView.setImageURI(it)
            binding.previewImageView.background = null // Hapus background
            binding.analyzeButton.visibility = View.VISIBLE
        }
    }


    // Menganalisis gambar menggunakan model TensorFlow Lite
    private fun analyzeImage(imageUri: Uri) {
        val imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    showToast(error) // Menampilkan pesan jika terjadi kesalahan
                }

                override fun onResults(result: Map<String, Any>) {  // Terima hasil sebagai Map
                    val predictedClass = result["predicted_class"] as? String ?: "Unknown"
                    val confidence = result["confidence"] as? Float ?: 0f

                    val resultString = "$predictedClass: ${(confidence * 100).toInt()}%"
                    moveToResult(imageUri, resultString) // Pindah ke ResultActivity dengan hasil klasifikasi
                }
            }
        )

        imageClassifierHelper.classifyStaticImage(imageUri)
    }



    private fun moveToResult(imageUri: Uri, result: String) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, imageUri.toString())
        intent.putExtra(ResultActivity.EXTRA_RESULT, result)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private val cameraLaunch =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                imageShow()
            } else {
                currentImageUri = null
            }
        }

    private fun imageShow() {
        currentImageUri?.let { uri ->
            binding.previewImageView.setImageURI(uri)
            binding.previewImageView.background = null
            startCrop(uri) // Tambahkan langkah ini
        }
    }


    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            supportActionBar?.show()
        }
    }

    private fun setupObservers() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLoggedIn) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        if (logoutDialog?.isShowing == true) {
            logoutDialog?.dismiss()
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.logout)
        builder.setMessage(R.string.confirmation_logout)
        builder.setPositiveButton(R.string.done) { _, _ ->
            viewModel.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        logoutDialog = builder.show()
    }

    private fun showExitConfirmationDialog() {
        if (exitDialog?.isShowing == true) {
            exitDialog?.dismiss()
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.exit_apl)
        builder.setMessage(R.string.confirmation_logout)
        builder.setPositiveButton(R.string.done) { _, _ ->
            finish()
        }
        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        exitDialog = builder.show()
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredUri = savedInstanceState.getParcelable<Uri>("currentImageUri")
        restoredUri?.let { viewModel.setImageUri(it) }
    }


    override fun onDestroy() {
        super.onDestroy()
        exitDialog?.dismiss()
        logoutDialog?.dismiss()
    }

}
