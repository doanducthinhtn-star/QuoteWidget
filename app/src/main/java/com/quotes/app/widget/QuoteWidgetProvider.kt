package com.quotes.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.widget.RemoteViews
import com.quotes.app.MainActivity
import com.quotes.app.R
import com.quotes.app.data.QuoteRepository

class QuoteWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { id ->
            updateWidget(context, appWidgetManager, id)
        }
    }

    companion object {

        fun updateAllWidgets(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, QuoteWidgetProvider::class.java)
            )
            ids.forEach { id -> updateWidget(context, manager, id) }
        }

        fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
            val repo = QuoteRepository(context)
            val quote = repo.getSelectedQuote()

            // Dùng size của widget này, fallback về default (-1) nếu chưa set
            val textSize = if (repo.getWidgetTextSize(widgetId) != 18f) {
                repo.getWidgetTextSize(widgetId)
            } else {
                repo.getWidgetTextSize(-1).takeIf { it != 18f } ?: 18f
            }

            val views = RemoteViews(context.packageName, R.layout.widget_quote)

            val displayText = if (quote != null) {
                if (quote.author.isNotBlank()) {
                    "\"${quote.text}\"\n— ${quote.author}"
                } else {
                    "\"${quote.text}\""
                }
            } else {
                context.getString(R.string.widget_no_quote)
            }

            views.setTextViewText(R.id.widget_quote_text, displayText)
            views.setTextViewTextSize(
                R.id.widget_quote_text,
                TypedValue.COMPLEX_UNIT_SP,
                textSize
            )

            // Tap widget → mở app
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                widgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            manager.updateAppWidget(widgetId, views)
        }
    }
}
