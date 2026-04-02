package com.example.googlebooks.data.repository

import com.example.googlebooks.data.remote.BooksApiService
import com.example.googlebooks.domain.model.BookUiModel

class BooksRepository(
    private val api: BooksApiService
) {
    suspend fun searchBooks(query: String): List<BookUiModel> {
        val response = api.searchBooks(query = query, maxResults = 20)

        return response.items.orEmpty().mapNotNull { item ->
            val info = item.volumeInfo ?: return@mapNotNull null

            BookUiModel(
                id = item.id.orEmpty(),
                title = info.title.orEmpty(),
                authors = info.authors?.joinToString(", ").orEmpty(),
                publishedDate = info.publishedDate.orEmpty(),
                description = info.description.orEmpty(),
                pageCount = info.pageCount ?: 0,
                publisher = info.publisher.orEmpty(),
                thumbnailUrl = info.imageLinks?.thumbnail
                    ?.replace("http://", "https://")
                    .orEmpty()
            )
        }
    }
}