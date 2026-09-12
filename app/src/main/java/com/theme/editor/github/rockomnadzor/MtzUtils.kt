package com.theme.editor.github.rockomnadzor

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

data class WallpaperEntry(
    val path: String,
    val bytes: ByteArray
)

object MtzUtils {

    fun readThemeName(context: Context, uri: Uri): String? {
        return try {
            val tmp = copyToCache(context, uri, "src.mtz")
            ZipFile(tmp).use { zip ->
                val entry = zip.getEntry("description.xml") ?: return null
                val text = zip.getInputStream(entry).bufferedReader().readText()
                Regex("<name>(.*?)</name>").find(text)?.groupValues?.get(1)
            }
        } catch (e: Exception) {
            null
        }
    }

    fun writeThemeName(context: Context, uri: Uri, newName: String) {
        replaceInZip(context, uri) { entryName, bytes ->
            if (entryName == "description.xml") {
                val text = String(bytes)
                Regex("<name>(.*?)</name>")
                    .replace(text, "<name>$newName</name>")
                    .toByteArray()
            } else bytes
        }
    }

    /** Находит все картинки внутри папки wallpaper/ в архиве */
    fun listWallpaperEntries(context: Context, uri: Uri): List<WallpaperEntry> {
        val result = mutableListOf<WallpaperEntry>()
        try {
            val tmp = copyToCache(context, uri, "src.mtz")
            ZipFile(tmp).use { zip ->
                val entries = zip.entries()
                while (entries.hasMoreElements()) {
                    val e = entries.nextElement()
                    if (!e.isDirectory &&
                        e.name.startsWith("wallpaper/", ignoreCase = true) &&
                        Regex("\\.(png|jpg|jpeg|webp)$", RegexOption.IGNORE_CASE).containsMatchIn(e.name)
                    ) {
                        val bytes = zip.getInputStream(e).readBytes()
                        result.add(WallpaperEntry(e.name, bytes))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    /** Заменяет содержимое нескольких файлов в архиве по их путям */
    fun replaceWallpapers(context: Context, uri: Uri, replacements: Map<String, ByteArray>) {
        replaceInZip(context, uri) { entryName, bytes ->
            replacements[entryName] ?: bytes
        }
    }

    private fun replaceInZip(
        context: Context,
        uri: Uri,
        transform: (entryName: String, bytes: ByteArray) -> ByteArray
    ) {
        try {
            val srcTmp = copyToCache(context, uri, "src.mtz")
            val outTmp = File(context.cacheDir, "out.mtz")

            ZipFile(srcTmp).use { zip ->
                ZipOutputStream(outTmp.outputStream()).use { zos ->
                    val entries = zip.entries()
                    while (entries.hasMoreElements()) {
                        val entry = entries.nextElement()
                        val bytes = zip.getInputStream(entry).readBytes()
                        val finalBytes = transform(entry.name, bytes)

                        zos.putNextEntry(ZipEntry(entry.name))
                        zos.write(finalBytes)
                        zos.closeEntry()
                    }
                }
            }

            context.contentResolver.openOutputStream(uri, "wt")?.use { out ->
                outTmp.inputStream().use { it.copyTo(out) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun copyToCache(context: Context, uri: Uri, name: String): File {
        val tmp = File(context.cacheDir, name)
        context.contentResolver.openInputStream(uri)?.use { input ->
            tmp.outputStream().use { output -> input.copyTo(output) }
        }
        return tmp
    }
}
