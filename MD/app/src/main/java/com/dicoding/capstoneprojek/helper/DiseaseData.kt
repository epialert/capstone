package com.dicoding.capstoneprojek.helper

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader


data class DiseaseData(
    val penyakit: Map<String, DiseaseDetail>
)

data class DiseaseDetail(
    val id: String,
    val penjelasan: String,
    val pertanyaan: String,
    val jawaban: List<Answer>
)

data class Answer(
    val id: String,
    val kategori: String,
    val text: String
)

data class ChatMessage(
    val message: String,
    val isUser: Boolean
)

// Fungsi untuk memuat data JSON
fun loadDiseaseData(context: Context): DiseaseData {
    val assetManager = context.assets
    val inputStream = assetManager.open("diseases.json")
    val reader = InputStreamReader(inputStream)
    val type = object : TypeToken<DiseaseData>() {}.type
    return Gson().fromJson(reader, type)
}

fun getDiseaseDetails(labelCodes: String, diseaseData: DiseaseData): DiseaseDetail? {
    // Memastikan penyakit berisi label yang dicari di dalam map penyakit
    Log.d("getDiseaseDetails", "Searching for label: $labelCodes")
    val formattedLabelCodes = labelCodes.lowercase().replace(" ", "_")
    val result = formattedLabelCodes.substringBefore(":")
    println(result) // Output: melanoma
    println(diseaseData)
    // Cari DiseaseDetail berdasarkan label
    val diseaseDetail = diseaseData.penyakit[result]

    Log.d("getDiseaseDetails", "Found diseaseDetail: $diseaseDetail")
    return diseaseDetail
}


