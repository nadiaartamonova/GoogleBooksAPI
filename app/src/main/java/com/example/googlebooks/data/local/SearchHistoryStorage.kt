package com.example.googlebooks.data.local

import android.content.Context
import org.json.JSONArray

class SearchHistoryStorage(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getRecentQueries(): List<String> {
        val raw = prefs.getString(KEY_RECENT_QUERIES, null) ?: return emptyList()
        return runCatching {
            val json = JSONArray(raw)
            buildList {
                for (i in 0 until json.length()) {
                    add(json.getString(i))
                }
            }
        }.getOrDefault(emptyList())
    }

    fun addQuery(query: String): List<String> {
        val cleaned = query.trim()
        if (cleaned.isBlank()) return getRecentQueries()

        val updated = buildList {
            add(cleaned)
            addAll(getRecentQueries().filterNot { it.equals(cleaned, ignoreCase = true) })
        }.take(MAX_HISTORY_SIZE)

        val json = JSONArray()
        updated.forEach { json.put(it) }
        prefs.edit().putString(KEY_RECENT_QUERIES, json.toString()).apply()

        return updated
    }

    companion object {
        private const val PREFS_NAME = "search_history"
        private const val KEY_RECENT_QUERIES = "recent_queries"
        private const val MAX_HISTORY_SIZE = 5
    }
}