package com.quotes.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quotes.app.data.Quote
import java.util.UUID

@Composable
fun QuoteEditDialog(
    existing: Quote? = null,
    onConfirm: (Quote) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(existing?.text ?: "") }
    var author by remember { mutableStateOf(existing?.author ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Thêm câu châm ngôn" else "Sửa câu châm ngôn") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Câu châm ngôn *") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Tác giả (tuỳ chọn)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onConfirm(
                            Quote(
                                id = existing?.id ?: UUID.randomUUID().toString(),
                                text = text.trim(),
                                author = author.trim()
                            )
                        )
                    }
                },
                enabled = text.isNotBlank()
            ) { Text("Lưu") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Huỷ") }
        }
    )
}
