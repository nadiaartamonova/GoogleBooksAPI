package com.example.googlebooks.data.remote

import com.example.googlebooks.data.remote.dto.BooksResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface BooksApiService {
    @GET("books/v1/volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20
    ): BooksResponseDto
}