package com.example.googlebooks.viewModel.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.googlebooks.data.local.AppDatabase
import com.example.googlebooks.data.local.SearchHistoryStorage
import com.example.googlebooks.data.remote.RetrofitInstance
import com.example.googlebooks.data.repository.BooksRepository
import com.example.googlebooks.domain.model.BookUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BooksViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BooksRepository(
        api = RetrofitInstance.api,
        dao = AppDatabase.getInstance(application).bookDao()
    )
    private val searchHistoryStorage = SearchHistoryStorage(application)

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _recentQueries = MutableStateFlow(searchHistoryStorage.getRecentQueries())
    val recentQueries: StateFlow<List<String>> = _recentQueries.asStateFlow()

    private val _books = MutableStateFlow<List<BookUiModel>>(emptyList())
    val books: StateFlow<List<BookUiModel>> = _books.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun onRecentQueryClick(value: String) {
        _query.value = value
        searchBooks()
    }
    fun searchBooks() {
        val q = _query.value.trim()
        if (q.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            runCatching {
                repository.searchBooks(q)
            }.onSuccess { result ->
                _books.value = result
                _recentQueries.value = searchHistoryStorage.addQuery(q)
            }.onFailure { throwable ->
                _books.value = emptyList()
                _error.value = throwable.message ?: "Unknown error"
                //_error.value = throwable.stackTraceToString()
            }

            _isLoading.value = false
        }
    }
}