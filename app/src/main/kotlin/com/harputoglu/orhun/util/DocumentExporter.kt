package com.harputoglu.orhun.util

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

object DocumentExporter {

    fun exportToTxt(context: Context, fileName: String, content: String): File? {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "$fileName.txt")
        return try {
            FileOutputStream(file).use {
                it.write(content.toByteArray())
            }
            file
        } catch (e: Exception) {
            null
        }
    }
    
    // PDF export will require a library like iText or generic Android PDF Document API
    // To be implemented in next step.
}
