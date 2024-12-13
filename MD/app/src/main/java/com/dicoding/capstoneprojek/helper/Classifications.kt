package com.dicoding.capstoneprojek.helper

data class Classifications(
    val categories: List<Category>
) {
    data class Category(
        val label: String,
        val score: Float
    )
}
