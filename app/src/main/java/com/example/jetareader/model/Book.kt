package com.example.jetareader.model

data class Book(
    val items: List<Item>,
    val kind: String,
    val totalItems: Int
)