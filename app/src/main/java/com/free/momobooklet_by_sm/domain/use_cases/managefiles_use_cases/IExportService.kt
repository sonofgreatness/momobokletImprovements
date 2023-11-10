package com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases

import androidx.annotation.WorkerThread
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.ExportState
import com.free.momobooklet_by_sm.domain.managefiles.pdf.models.DailyCommissionModelPDFManager
import com.free.momobooklet_by_sm.domain.managefiles.pdf.models.TransactionTablePDFManager
import com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.CsvConfigImpl
import com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.Exportable
import com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.Exports
import com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.pdf.PdfConfigImpl
import kotlinx.coroutines.flow.Flow

interface IExportService {
    fun <T, Y : Exportable> export(
        type: Exports,
        content: List<T>,
        content2: List<Y>
    ): Flow<ExportState>

    fun exportPdf(
        type: PdfConfigImpl,
        content: TransactionTablePDFManager,
        content2: DailyCommissionModelPDFManager
    ): Flow<ExportState>

    @WorkerThread
    fun <T, Y : Exportable> writeToCSV(
        csvConfig: CsvConfigImpl,
        content: List<T>,
        content2: List<Y>
    ): Flow<ExportState>

    @WorkerThread
    fun writePDF(
        pdfConfig: PdfConfigImpl,
        content: TransactionTablePDFManager,
        content2: DailyCommissionModelPDFManager
    ): Flow<ExportState>
}