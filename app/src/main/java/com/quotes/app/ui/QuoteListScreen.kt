package com.quotes.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quotes.app.data.Quote

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteListScreen(
    quotes: List<Quote>,
    selectedId: String?,
    widgetTextSize: Float,
    onSelect: (Quote) -> Unit,
    onAdd: (Quote) -> Unit,
    onEdit: (Quote) -> Unit,
    onDelete: (Quote) -> Unit,
    onTextSizeChange: (Float) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<Quote?>(null) }
    var deleteTarget by remember { mutableStateOf<Quote?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Châm ngôn sống") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Thêm")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Widget text size control
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Cỡ chữ widget: ${widgetTextSize.toInt()}sp",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Slider(
                        value = widgetTextSize,
                        onValueChange = onTextSizeChange,
                        valueRange = 10f..36f,
                        steps = 25,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (quotes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Chưa có câu châm ngôn nào.\nNhấn + để thêm.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quotes, key = { it.id }) { quote ->
                        QuoteCard(
                            quote = quote,
                            isSelected = quote.id == selectedId,
                            onSelect = { onSelect(quote) },
                            onEdit = { editTarget = quote },
                            onDelete = { deleteTarget = quote }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        QuoteEditDialog(
            onConfirm = { onAdd(it); showAddDialog = false },
            onDismiss = { showAddDialog = false }
        )
    }

    editTarget?.let { q ->
        QuoteEditDialog(
            existing = q,
            onConfirm = { onEdit(it); editTarget = null },
            onDismiss = { editTarget = null }
        )
    }

    deleteTarget?.let { q ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Xoá câu châm ngôn?") },
            text = { Text("\"${q.text}\"") },
            confirmButton = {
                TextButton(onClick = { onDelete(q); deleteTarget = null }) { Text("Xoá") }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("Huỷ") }
            }
        )
    }
}

@Composable
private fun QuoteCard(
    quote: Quote,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val containerColor = if (isSelected)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "\"${quote.text}\"",
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic
                )
                if (quote.author.isNotBlank()) {
                    Text(
                        text = "— ${quote.author}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Sửa", modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Xoá", modifier = Modifier.size(20.dp))
            }
        }
    }
}
