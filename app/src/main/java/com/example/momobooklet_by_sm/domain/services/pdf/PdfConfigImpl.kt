package com.example.momobooklet_by_sm.domain.services.pdf

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.common.util.CommisionDatesManager
import com.example.momobooklet_by_sm.common.util.classes.WRITETO
import com.example.momobooklet_by_sm.domain.repositories.CommissionDatesManagerRepository
import com.example.momobooklet_by_sm.domain.repositories.PdfConfigRepository
import com.wwdablu.soumya.simplypdf.composers.properties.TableProperties
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

data class PdfConfigImpl @RequiresApi(Build.VERSION_CODES.O) constructor(
    val app: Application,
    private val prefix: String,
    private val datesManager: CommissionDatesManagerRepository,
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

    ):PdfConfigRepository {

    /***************************************************************************
     * setUpExternalMemoryFileDirectories() -> creates in External Storage
     *                                  the directory  "MoMoBooklet"
     *                                  subdirectories : PDF
     *                                                 : CSV
     ***************************************************************************/
    override fun setUpExternalDirectories() {
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