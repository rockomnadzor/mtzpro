package com.theme.editor.github.rockomnadzor.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theme.editor.github.rockomnadzor.ShizukuHelper

@Composable
fun ShizukuCheckScreen(onReady: () -> Unit) {
    var status by remember { mutableStateOf("Проверяю Shizuku…") }
    var granted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        ShizukuHelper.check { ok, msg -> granted = ok; status = msg }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Shizuku", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text(status, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            if (granted) onReady()
            else ShizukuHelper.check { ok, msg -> granted = ok; status = msg; if (ok) onReady() }
        }) {
            Text(if (granted) "Продолжить" else "Проверить снова")
        }
    }
}
