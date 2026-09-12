package com.theme.editor.github.rockomnadzor

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.theme.editor.github.rockomnadzor.ui.PickFileScreen
import com.theme.editor.github.rockomnadzor.ui.ShizukuCheckScreen
import com.theme.editor.github.rockomnadzor.ui.ThemeEditorScreen
import com.theme.editor.github.rockomnadzor.ui.WallpaperScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    var screen by remember { mutableStateOf<Screen>(Screen.ShizukuCheck) }
    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    var pickedName by remember { mutableStateOf<String?>(null) }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (screen) {
            is Screen.ShizukuCheck -> ShizukuCheckScreen(
                onReady = { screen = Screen.PickFile }
            )
            is Screen.PickFile -> PickFileScreen(
                onPicked = { uri, name -> pickedUri = uri; pickedName = name },
                pickedName = pickedName,
                onContinue = { screen = Screen.ThemeEditor }
            )
            is Screen.ThemeEditor -> pickedUri?.let {
                ThemeEditorScreen(
                    uri = it,
                    fileName = pickedName ?: "theme.mtz",
                    onChangeWallpaper = { screen = Screen.Wallpaper }
                )
            }
            is Screen.Wallpaper -> pickedUri?.let {
                WallpaperScreen(uri = it, onDone = { screen = Screen.ThemeEditor })
            }
        }
    }
}
