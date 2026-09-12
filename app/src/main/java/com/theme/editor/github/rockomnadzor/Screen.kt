package com.theme.editor.github.rockomnadzor

sealed class Screen {
    object ShizukuCheck : Screen()
    object PickFile : Screen()
    object ThemeEditor : Screen()
    object Wallpaper : Screen()
}
