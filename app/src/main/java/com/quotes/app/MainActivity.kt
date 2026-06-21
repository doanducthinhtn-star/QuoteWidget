package com.quotes.app

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.quotes.app.data.QuoteRepository
import com.quotes.app.ui.QuoteListScreen
import com.quotes.app.ui.theme.QuoteWidgetTheme
import com.quotes.app.widget.QuoteWidgetProvider

class MainActivity : ComponentActivity() {

    private lateinit var repo: QuoteRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        repo = QuoteRepository(this)

        setContent {
            QuoteWidgetTheme {
                var quotes by remember { mutableStateOf(repo.getQuotes()) }
                var selectedId by remember { mutableStateOf(repo.getSelectedQuoteId()) }

                // Lấy widgetId đầu tiên để hiển thị text size (nếu chưa có widget thì dùng id -1)
                val activeWidgetIds = AppWidgetManager.getInstance(this)
                    .getAppWidgetIds(ComponentName(this, QuoteWidgetProvider::class.java))
                val previewWidgetId = activeWidgetIds.firstOrNull() ?: -1
                var textSize by remember { mutableFloatStateOf(repo.getWidgetTextSize(previewWidgetId)) }

                QuoteListScreen(
                    quotes = quotes,
                    selectedId = selectedId,
                    widgetTextSize = textSize,
                    onSelect = { quote ->
                        repo.setSelectedQuoteId(quote.id)
                        selectedId = quote.id
                        QuoteWidgetProvider.updateAllWidgets(this)
                    },
                    onAdd = { quote ->
                        val updated = quotes + quote
                        repo.saveQuotes(updated)
                        quotes = updated
                    },
                    onEdit = { quote ->
                        val updated = quotes.map { if (it.id == quote.id) quote else it }
                        repo.saveQuotes(updated)
                        quotes = updated
                        if (selectedId == quote.id) {
                            QuoteWidgetProvider.updateAllWidgets(this)
                        }
                    },
                    onDelete = { quote ->
                        val updated = quotes.filter { it.id != quote.id }
                        repo.saveQuotes(updated)
                        quotes = updated
                        if (selectedId == quote.id) {
                            val nextId = updated.firstOrNull()?.id
                            if (nextId != null) repo.setSelectedQuoteId(nextId)
                            selectedId = nextId
                            QuoteWidgetProvider.updateAllWidgets(this)
                        }
                    },
                    onTextSizeChange = { size ->
                        textSize = size
                        // Lưu size cho tất cả widget đang active
                        val widgetIds = AppWidgetManager.getInstance(this)
                            .getAppWidgetIds(ComponentName(this, QuoteWidgetProvider::class.java))
                        if (widgetIds.isEmpty()) {
                            // Chưa có widget, lưu vào id -1 làm default
                            repo.setWidgetTextSize(-1, size)
                        } else {
                            widgetIds.forEach { id -> repo.setWidgetTextSize(id, size) }
                            QuoteWidgetProvider.updateAllWidgets(this)
                        }
                    }
                )
            }
        }
    }
}
