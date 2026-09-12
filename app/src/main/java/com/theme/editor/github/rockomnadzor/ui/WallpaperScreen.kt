package com.theme.editor.github.rockomnadzor.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theme.editor.github.rockomnadzor.MtzUtils
import com.theme.editor.github.rockomnadzor.WallpaperEntry

@Composable
fun WallpaperScreen(uri: Uri, onDone: () -> Unit) {
    val context = LocalContext.current
    var entries by remember { mutableStateOf<List<WallpaperEntry>>(emptyList()) }
    var pendingBytes by remember { mutableStateOf<Map<String, ByteArray>>(emptyMap()) }
    var saving by remember { mutableStateOf(false) }
    var savedMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uri) {
        entries = MtzUtils.listWallpaperEntries(context, uri)
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Сменить обои", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text("Текущие обои внутри темы — можно заменить каждую отдельно.", fontSize = 13.sp)
        Spacer(Modifier.height(16.dp))

        if (entries.isEmpty()) {
            Text("В архиве не нашлось картинок в папке wallpaper/", fontSize = 13.sp)
        }

        entries.forEach { entry ->
            WallpaperSlot(
                entry = entry,
                overrideBytes = pendingBytes[entry.path],
                onReplaced = { newBytes ->
                    pendingBytes = pendingBytes + (entry.path to newBytes)
                }
            )
            Spacer(Modifier.height(16.dp))
        }

        Spacer(Modifier.weight(1f))

        savedMsg?.let {
            Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = {
                saving = true
                MtzUtils.replaceWallpapers(context, uri, pendingBytes)
                saving = false
                savedMsg = "Сохранено в тему ✓"
                pendingBytes = emptyMap()
                entries = MtzUtils.listWallpaperEntries(context, uri)
            },
            enabled = pendingBytes.isNotEmpty() && !saving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (saving) "Сохраняю…" else "Сохранить")
        }
    }
}

@Composable
private fun WallpaperSlot(
    entry: WallpaperEntry,
    overrideBytes: ByteArray?,
    onReplaced: (ByteArray) -> Unit
) {
    val context = LocalContext.current
    val bytesToShow = overrideBytes ?: entry.bytes

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { picked: Uri? ->
        if (picked != null) {
            context.contentResolver.openInputStream(picked)?.use { input ->
                onReplaced(input.readBytes())
            }
        }
    }

    Card(shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(entry.path.substringAfterLast("/"), fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))

            val bmp = remember(bytesToShow) {
                BitmapFactory.decodeByteArray(bytesToShow, 0, bytesToShow.size)
            }
            if (bmp != null) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Заменить фото")
            }
        }
    }
}
