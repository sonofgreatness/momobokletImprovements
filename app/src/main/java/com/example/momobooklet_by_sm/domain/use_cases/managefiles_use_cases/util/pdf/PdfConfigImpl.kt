package com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.pdf

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.common.util.classes.WRITETO
import com.example.momobooklet_by_sm.domain.repositories.CommissionDatesManagerRepository
import com.example.momobooklet_by_sm.domain.repositories.ReportConfigRepository
import com.example.momobooklet_by_sm.domain.repositories.UserRepository
import com.wwdablu.soumya.simplypdf.composers.properties.TableProperties
import java.io.FileOutputStream
import javax.inject.Inject

data class PdfConfigImpl @RequiresApi(Build.VERSION_CODES.O) @Inject constructor(
    val app: Application,
     val prefix: String,
    private val datesManager: CommissionDatesManagerRepository,
    val location: WRITETO = WRITETO.EXTERNAL,
    /*--..
     REFERS TO LOCATION IN OS's storage
    --..*/
    val reportConfigRepository: ReportConfigRepository,
    val suffix: String = datesManager.generateTodayDate(),

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
    val userRepository: UserRepository,
    val dates : List<String>
    )
