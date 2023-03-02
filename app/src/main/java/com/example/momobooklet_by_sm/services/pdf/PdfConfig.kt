package com.example.momobooklet_by_sm.services.pdf

import android.app.Application
import android.app.TimePickerDialog
import android.content.Context
import android.os.Environment
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.util.CommisionDatesManager
import com.example.momobooklet_by_sm.util.classes.WRITETO
import com.wwdablu.soumya.simplypdf.composers.properties.TableProperties
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat

data class PdfConfig(
    val app: Application,
    private val prefix: String,
    private val datesManager: CommisionDatesManager,
      val location: WRITETO,
    private val suffix: String = datesManager.generateTodayDate(),

    val tableProperties: TableProperties =TableProperties().apply{
        borderColor = "#000000"
        borderWidth = 1
        drawBorder = true
    },
    val fileName: String = "$prefix-$suffix.pdf",
    @Suppress("DEPRECATION")
    val hostPath: String = Environment.getExternalStorageDirectory().path
                            .plus("/").plus(app.getString(R.string.app_name))
        .plus("/PDF")
    ,
    val internalhostPath:String = app.filesDir.path,
    val otherHostPath: FileOutputStream? = app.openFileOutput(fileName, Context.MODE_APPEND),

    ) {

    /***************************************************************************
     * setUpExternalMemoryFileDirectories() -> creates in External Storage
     *                                  the directory  "MoMoBooklet"
     *                                  subdirectories : PDF
     *                                                 : CSV
     ***************************************************************************/
    fun setUpExternalDirectories() {
        Timber.d("PDFCONFIG MEMORY SETS ")
        val hostPath = Environment.getExternalStorageDirectory().path
        val rootDirectory = File(hostPath + "/" + app.getString(R.string.app_name))

        if (!rootDirectory.exists())
            rootDirectory.mkdir()
        val pdfDirectory =
            File(hostPath + "/" + app.getString(R.string.app_name) + "/" + "PDF")
        val csvDirectory =
            File(hostPath + "/" + app.getString(R.string.app_name) + "/" + "CSV")

        if (!pdfDirectory.exists())
            pdfDirectory.mkdir()
        if (!csvDirectory.exists())
            csvDirectory.mkdir()
    }
}