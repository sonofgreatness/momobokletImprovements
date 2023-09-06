package com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases

import android.annotation.SuppressLint
import android.graphics.Color
import android.print.PrintAttributes
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.core.net.toUri
import com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.CsvConfigImpl
import com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.Exportable
import com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.Exports
import com.example.momobooklet_by_sm.domain.managefiles.pdf.models.DailyCommissionModelPDFManager
import com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.pdf.PdfConfigImpl
import com.example.momobooklet_by_sm.domain.managefiles.pdf.models.TransactionTablePDFManager
import com.example.momobooklet_by_sm.common.util.classes.WRITETO
import com.example.momobooklet_by_sm.common.util.classes.operationalStates.ExportState
import com.example.momobooklet_by_sm.domain.managefiles.csv.models.DailyCommissionModelCSV
import com.example.momobooklet_by_sm.domain.managefiles.csv.models.TransactionModelCSV
import com.opencsv.CSVWriter
import com.opencsv.bean.StatefulBeanToCsvBuilder
import com.wwdablu.soumya.simplypdf.SimplyPdf
import com.wwdablu.soumya.simplypdf.composers.properties.TextProperties
import com.wwdablu.soumya.simplypdf.document.DocumentInfo
import com.wwdablu.soumya.simplypdf.document.Margin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.OutputStreamWriter

@SuppressLint("LogNotTimber")
object ExportService : IExportService {
    override fun <T ,Y : Exportable> export(type: Exports, content: List<T>, content2: List<Y>): Flow<ExportState> =
        when (type) {
            is Exports.CSV -> writeToCSV(type.csvConfig, content,content2)
            else -> {
                TODO("FAILURE TO DO A PROPER REFACTOR, revisit later when you are better at this")
            }
        }

    override fun exportPdf(type: PdfConfigImpl, content: TransactionTablePDFManager, content2: DailyCommissionModelPDFManager): Flow<ExportState> =
        writePDF(type, content,content2)

    @WorkerThread
    override fun <T,Y : Exportable> writeToCSV(csvConfig: CsvConfigImpl, content: List<T>, content2: List<Y>) =
      WriteCSV(csvConfig,content as List<TransactionModelCSV>, content2 as List<DailyCommissionModelCSV>).writeCSV()

    @WorkerThread
    override fun writePDF(pdfConfig: PdfConfigImpl, content: TransactionTablePDFManager, content2: DailyCommissionModelPDFManager) =
            WritePDF(pdfConfig, content, content2).writePDF()
    }

