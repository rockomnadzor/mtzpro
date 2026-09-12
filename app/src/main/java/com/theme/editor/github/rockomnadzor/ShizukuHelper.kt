package com.theme.editor.github.rockomnadzor

import android.content.pm.PackageManager
import rikka.shizuku.Shizuku

object ShizukuHelper {

    fun check(callback: (granted: Boolean, message: String) -> Unit) {
        try {
            val alive = Shizuku.pingBinder()
            if (!alive) {
                callback(false, "Shizuku не запущен.\nВключи приложение Shizuku (ADB-режим или root) и нажми «Проверить снова».")
                return
            }
            val granted = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            if (granted) {
                callback(true, "Shizuku подключён, права выданы ✓")
            } else {
                Shizuku.requestPermission(100)
                callback(false, "Shizuku найден, разреши доступ во всплывающем окне и нажми «Проверить снова».")
            }
        } catch (e: Exception) {
            callback(false, "Shizuku не найден на устройстве.\nУстанови приложение Shizuku и активируй его.")
        }
    }
}
