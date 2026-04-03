package com.example.googlebooks.data.repository

import com.example.googlebooks.data.local.BookDao
import com.example.googlebooks.data.local.BookEntity
import com.example.googlebooks.data.remote.BooksApiService
import com.example.googlebooks.domain.model.BookUiModel

class BooksRepository(
    private val api: BooksApiService,
    private val dao: BookDao
) {

    suspend fun searchBooks(query: String): List<BookUiModel> {
        return try {
            // 1) Online fetch
            val response = api.searchBooks(query = query, maxResults = 20)
            val books = response.items.orEmpty().mapNotNull { item ->
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

            // 2) Save latest results to Room for this query
            val entities = books.map { it.toEntity(query) }
            dao.deleteByQuery(query)
            dao.insertAll(entities)

            books
        } catch (e: Exception) {
            // 3) Offline fallback from Room
            val cached = dao.getBooksByQuery(query).map { it.toUiModel() }
            if (cached.isNotEmpty()) {
                cached
            } else {
                throw Exception("No internet and no cached results for \"$query\"")
            }
        }
    }

    private fun BookUiModel.toEntity(query: String): BookEntity {
        return BookEntity(
            id = if (id.isNotBlank()) id else "$title-$publishedDate",
            title = title,
            authors = authors,
            publishedDate = publishedDate,
            description = description,
            pageCount = pageCount,
            publisher = publisher,
            thumbnailUrl = thumbnailUrl,
            searchQuery = query
        )
    }

    private fun BookEntity.toUiModel(): BookUiModel {
        return BookUiModel(
            id = id,
            title = title,
            authors = authors,
            publishedDate = publishedDate,
            description = description,
            pageCount = pageCount,
            publisher = publisher,
            thumbnailUrl = thumbnailUrl
        )
    }
}