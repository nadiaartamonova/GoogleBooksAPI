package com.example.googlebooks.domain.model

data class BookUiModel(
    val id: String,
    val title: String,
    val authors: String,
    val publishedDate: String,
    val description: String,
    val pageCount: Int,
    val publisher: String,
    val thumbnailUrl: String
)