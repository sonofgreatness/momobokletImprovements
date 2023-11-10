package com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases

import android.annotation.SuppressLint
import androidx.annotation.WorkerThread
import com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.CsvConfigImpl
import com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.Exportable
import com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.Exports
import com.free.momobooklet_by_sm.domain.managefiles.pdf.models.DailyCommissionModelPDFManager
import com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.pdf.PdfConfigImpl
import com.free.momobooklet_by_sm.domain.managefiles.pdf.models.TransactionTablePDFManager
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.ExportState
import com.free.momobooklet_by_sm.domain.managefiles.csv.models.DailyCommissionModelCSV
import com.free.momobooklet_by_sm.domain.managefiles.csv.models.TransactionModelCSV
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@SuppressLint("LogNotTimber")
object ExportService : IExportService {
    override fun <T ,Y : Exportable> export(type: Exports, content: List<T>, content2: List<Y>): Flow<ExportState> =
        when (type) {
            is Exports.CSV -> writeToCSV(type.csvConfig, content,content2)

              // delete this
            else -> {
                flow{
                    emit(ExportState.Loading)
                }

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

