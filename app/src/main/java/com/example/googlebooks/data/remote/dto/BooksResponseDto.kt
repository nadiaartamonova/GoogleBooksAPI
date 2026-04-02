package com.example.googlebooks.data.remote.dto

data class BooksResponseDto(
    val items: List<BookItemDto>?
)

data class BookItemDto(
    val id: String?,
    val volumeInfo: VolumeInfoDto?
)

data class VolumeInfoDto(
    val title: String?,
    val authors: List<String>?,
    val publishedDate: String?,
    val description: String?,
    val pageCount: Int?,
    val publisher: String?,
    val imageLinks: ImageLinksDto?
)

data class ImageLinksDto(
    val thumbnail: String?
)