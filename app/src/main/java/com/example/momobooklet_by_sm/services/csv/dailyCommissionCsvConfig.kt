package com.example.momobooklet_by_sm.services.csv

import android.app.Application
import android.content.Context
import com.example.momobooklet_by_sm.util.CommisionDatesManager
import java.io.FileOutputStream
import java.text.DateFormat

data class dailyCommissionCsvConfig(
    private val app: Application,
    private val datesManager: CommisionDatesManager,
    private val prefix: String ,
    private val suffix: String = DateFormat
        .getDateTimeInstance()
        .format(System.currentTimeMillis())
        .toString()
        .replace(",","")
        .replace(" ", "_"),


    val fileName: String = "$prefix-$suffix.csv",
    @Suppress("DEPRECATION")
    val hostPath: String = app.filesDir.path.plus("/Documents/DigiBooklet") ?: "",
    val otherHostPath: FileOutputStream? = app.openFileOutput(fileName, Context.MODE_APPEND),
)
