package com.quotes.app.data

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class QuoteRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("quotes_prefs", Context.MODE_PRIVATE)

    fun getQuotes(): List<Quote> {
        val json = prefs.getString(KEY_QUOTES, null) ?: return defaultQuotes()
        return runCatching { Json.decodeFromString<List<Quote>>(json) }.getOrDefault(defaultQuotes())
    }

    fun saveQuotes(quotes: List<Quote>) {
        prefs.edit().putString(KEY_QUOTES, Json.encodeToString(quotes)).apply()
    }

    fun getSelectedQuoteId(): String? = prefs.getString(KEY_SELECTED_ID, null)

    fun setSelectedQuoteId(id: String) {
        prefs.edit().putString(KEY_SELECTED_ID, id).apply()
    }

    fun getSelectedQuote(): Quote? {
        val id = getSelectedQuoteId() ?: return null
        return getQuotes().find { it.id == id }
    }

    fun getWidgetTextSize(widgetId: Int): Float =
        prefs.getFloat("text_size_$widgetId", 18f)

    fun setWidgetTextSize(widgetId: Int, size: Float) {
        prefs.edit().putFloat("text_size_$widgetId", size).apply()
    }

    private fun defaultQuotes() = listOf(
        Quote("1", "Hành trình vạn dặm bắt đầu từ một bước chân.", "Lão Tử"),
        Quote("2", "Không có gì là không thể nếu bạn thực sự muốn.", ""),
        Quote("3", "Mỗi ngày là một cơ hội mới để trở nên tốt hơn.", "")
    ).also { saveQuotes(it) }

    companion object {
        private const val KEY_QUOTES = "quotes"
        private const val KEY_SELECTED_ID = "selected_quote_id"
    }
}
