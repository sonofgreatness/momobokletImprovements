package com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import com.free.momobooklet_by_sm.R
import com.free.momobooklet_by_sm.common.util.classes.WRITETO
import com.free.momobooklet_by_sm.domain.repositories.CommissionDatesManagerRepository
import com.free.momobooklet_by_sm.domain.repositories.ReportConfigRepository
import java.io.FileOutputStream
import javax.inject.Inject

data class CsvConfigImpl @RequiresApi(Build.VERSION_CODES.O) @Inject constructor(

    private val app: Application,
    private val prefix: String,
    private val datesManager: CommissionDatesManagerRepository,
    val location: WRITETO = WRITETO.EXTERNAL,/*--..
   REFERS TO LOCATION IN OS's storage
    --..*/
    val reportConfigRepository: ReportConfigRepository,
    private val suffix: String = datesManager.generateTodayDate(),

    val fileName: String = "$prefix-$suffix.csv",
    @Suppress("DEPRECATION")
    val hostPath: String = Environment.getExternalStorageDirectory().path
        .plus("/").plus(app.getString(R.string.app_name))
        .plus("/CSV")
    ,
    val internalhostPath:String = app.filesDir.path,
    val otherHostPath: FileOutputStream? = app.openFileOutput(fileName, Context.MODE_APPEND),
    )



