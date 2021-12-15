package com.example.metodixrefactor.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.io.File

object CustomFileUtil {
    fun generateCSVInExternalCache(
            context: Context, fileName: String, title: String, content: String
    ) {
        val path = context.externalCacheDir?.path
        val tmpFileName = fileName.replace(":", "_")
        val file = File(path, "${tmpFileName}.csv")

        if (file.exists())
            file.delete()

        try {
            file.appendText(title, Charsets.UTF_16)
            file.appendText(content, Charsets.UTF_16)
            Toast.makeText(context, "Archivo generado ${tmpFileName}.csv", Toast.LENGTH_LONG)
                    .show()
        } catch (e: Exception) {
            Log.e("TAG", "onCreate: $e")
            Toast.makeText(context, "Error al generar archivo", Toast.LENGTH_LONG)
                    .show()
        }
    }
}