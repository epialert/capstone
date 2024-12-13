package com.dicoding.capstoneprojek.ui.result

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModel : ViewModel() {

    // Private mutable LiveData to store the image URI internally
    private val _imageUri = MutableLiveData<Uri?>()

    // Public read-only LiveData to expose the image URI to observers, like the Fragment
    val imageUri: LiveData<Uri?> get() = _imageUri

    // Function to set a new image URI, updating the LiveData's value
    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
    }
}