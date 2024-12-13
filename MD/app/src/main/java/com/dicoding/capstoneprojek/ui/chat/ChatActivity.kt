package com.dicoding.capstoneprojek.ui.chat

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.capstoneprojek.R
import com.dicoding.capstoneprojek.helper.ChatMessage
import com.dicoding.capstoneprojek.helper.getDiseaseDetails
import com.dicoding.capstoneprojek.helper.loadDiseaseData

class ChatActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)



        // Inisialisasi RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.chat_recycler_view)
        chatAdapter = ChatAdapter(chatMessages)
        recyclerView.adapter = chatAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Tombol kirim
        val sendButton = findViewById<Button>(R.id.send_button)
        val inputField = findViewById<EditText>(R.id.input_field)

        sendButton.setOnClickListener {
            val userInput = inputField.text.toString().trim()
            if (userInput.isNotEmpty()) {
                addMessageToChat(userInput, isUser = true)
                inputField.text.clear()
                handleUserInput(userInput)
            }
        }

        // Menangani pengiriman data penyakit
        val diseaseLabel = intent.getStringExtra("disease_label")
        Log.d("ChatActivity", "Received diseaseLabel: $diseaseLabel")  // Log untuk memverifikasi label yang diterima
        if (diseaseLabel == null) {
            addMessageToChat("Maaf, label penyakit tidak ditemukan.", isUser = false)
            return
        }


        // Memuat data penyakit
        val diseaseData = try {
            loadDiseaseData(this)
        } catch (e: Exception) {
            addMessageToChat("Maaf, terjadi kesalahan saat memuat data penyakit.", isUser = false)
            return
        }

        // Menampilkan penjelasan penyakit
        val diseaseDetail = getDiseaseDetails(diseaseLabel, diseaseData)
        if (diseaseDetail == null) {
            addMessageToChat("Maaf, data penyakit tidak ditemukan.", isUser = false)
        } else {
            addMessageToChat(diseaseDetail.penjelasan, isUser = false)
        }
    }

    private fun handleUserInput(input: String) {
        // Menangani masukan pengguna
        val diseaseLabel = intent.getStringExtra("disease_label")
        val diseaseData = try {
            loadDiseaseData(this)
        } catch (e: Exception) {
            addMessageToChat("Maaf, terjadi kesalahan saat memuat data penyakit.", isUser = false)
            return
        }

        val diseaseDetail = diseaseLabel?.let { getDiseaseDetails(it, diseaseData) }

        if (diseaseDetail == null) {
            addMessageToChat("Maaf, saya tidak dapat menemukan informasi terkait.", isUser = false)
            return
        }

        val response = diseaseDetail.jawaban.find { input.contains(it.kategori, ignoreCase = true) }
        if (response != null) {
            addMessageToChat(response.text, isUser = false)
        } else {
            addMessageToChat("Maaf, saya tidak mengerti pertanyaan Anda. Coba gunakan kata kunci seperti 'Gejala', 'Penyebab', atau 'Cara pengobatan'.", isUser = false)
        }
    }

    private fun addMessageToChat(message: String, isUser: Boolean) {
        chatMessages.add(ChatMessage(message, isUser))
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        // Scroll otomatis ke item terbaru
        findViewById<RecyclerView>(R.id.chat_recycler_view).scrollToPosition(chatMessages.size - 1)
    }
}
