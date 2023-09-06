package com.example.momobooklet_by_sm.data.local.repositories

import android.app.Application
import android.os.Environment
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.domain.repositories.ReportConfigRepository
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class ReportConfigRepositoryImpl @Inject constructor(val app: Application) : ReportConfigRepository
{

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
    /***************************************************************************
     * setUpExternalMemoryFileDirectories() -> creates in External Storage
     *                                  the directory  "MoMoBooklet"
     *                                  subdirectories : PDF
     *                                                 : CSV
     ***************************************************************************/
    override fun setUpInternalDirectories() {
        val hostPath = app.filesDir.absolutePath
        val rootDirectory = File("$hostPath/files")

        if (!rootDirectory.exists())
            rootDirectory.mkdir()

        /* val pdfDirectory =
            File(hostPath + "/" + app.getString(R.string.app_name) + "/" + "PDF")
        val csvDirectory =
            File(hostPath + "/" + app.getString(R.string.app_name) + "/" + "CSV")

        if (!pdfDirectory.exists())
            pdfDirectory.mkdir()
        if (!csvDirectory.exists())
            csvDirectory.mkdir()
    }
    */

    }
}
