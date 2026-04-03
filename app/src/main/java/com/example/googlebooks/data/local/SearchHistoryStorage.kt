package com.example.googlebooks.data.local

import android.content.Context
import org.json.JSONArray

class SearchHistoryStorage(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getRecentQueries(): List<String> {
        val sanitized = readQueriesFromStorage()
            .fold(mutableListOf<String>()) { acc, query ->
                if (acc.none { it.equals(query, ignoreCase = true) }) {
                    acc.add(query)
                }
                acc
            }
            .take(MAX_HISTORY_SIZE)

        persistQueries(sanitized)
        return sanitized
    }

    fun addQuery(query: String): List<String> {
        val cleaned = query.trim()
        if (cleaned.isBlank()) return getRecentQueries()

        val updated = buildList {
            add(cleaned)
            addAll(getRecentQueries().filterNot { it.equals(cleaned, ignoreCase = true) })
        }.take(MAX_HISTORY_SIZE)

        persistQueries(updated)
        return updated
    }

    private fun readQueriesFromStorage(): List<String> {
        val raw = prefs.getString(KEY_RECENT_QUERIES, null) ?: return emptyList()
        return runCatching {
            val json = JSONArray(raw)
            buildList {
                for (i in 0 until json.length()) {
                    val value = json.optString(i).trim()
                    if (value.isNotBlank()) {
                        add(value)
                    }
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun persistQueries(queries: List<String>) {
        val json = JSONArray()
        queries.forEach { json.put(it) }
        prefs.edit().putString(KEY_RECENT_QUERIES, json.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "search_history"
        private const val KEY_RECENT_QUERIES = "recent_queries"
        private const val MAX_HISTORY_SIZE = 5
    }
}