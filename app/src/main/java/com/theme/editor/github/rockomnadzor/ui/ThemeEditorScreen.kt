package com.theme.editor.github.rockomnadzor.ui

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theme.editor.github.rockomnadzor.MtzUtils

@Composable
fun ThemeEditorScreen(uri: Uri, fileName: String) {
    val context = LocalContext.current
    var themeName by remember { mutableStateOf("…") }
    var showRenameDialog by remember { mutableStateOf(false) }
    var renameInput by remember { mutableStateOf("") }

    LaunchedEffect(uri) {
        themeName = MtzUtils.readThemeName(context, uri) ?: fileName.removeSuffix(".mtz")
        renameInput = themeName
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Файл: $fileName", fontSize = 13.sp)
        Spacer(Modifier.height(4.dp))
        Text("Название темы:", fontSize = 13.sp)
        Text(themeName, fontWeight = FontWeight.Bold, fontSize = 18.sp)

        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = { renameInput = themeName; showRenameDialog = true }) {
            Text("Изменить название темы")
        }

        Spacer(Modifier.height(24.dp))
        Button(onClick = { /* следующий шаг: экран смены обоев */ }) {
            Text("Поменять обои")
        }
    }

    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Новое название темы") },
            text = {
                OutlinedTextField(
                    value = renameInput,
                    onValueChange = { renameInput = it },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    MtzUtils.writeThemeName(context, uri, renameInput)
                    themeName = renameInput
                    showRenameDialog = false
                }) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) { Text("Отмена") }
            }
        )
    }
}
