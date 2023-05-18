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
object ExportService {

    fun <T ,Y : Exportable> export(type: Exports, content: List<T>, content2: List<Y>): Flow<ExportState> =
        when (type) {
            is Exports.CSV -> writeToCSV(type.csvConfig, content,content2)
            else -> {
                TODO("FAILURE TO DO A PROPER REFACTOR, revisit later when you are better at this")
            }
        }


    fun exportPdf(type: PdfConfigImpl, content: TransactionTablePDFManager, content2: DailyCommissionModelPDFManager): Flow<ExportState> =
        writePDF(type, content,content2)


    @WorkerThread
    fun <T,Y : Exportable> writeToCSV(csvConfig: CsvConfigImpl, content: List<T>, content2: List<Y>) =
        flow<ExportState> {
            with(csvConfig) {

                val myhostPath : String = if(location.ordinal == WRITETO.EXTERNAL.ordinal)
                    hostPath
                else
                    internalhostPath

                myhostPath.ifEmpty { throw IllegalStateException("Wrong Path") }

                val hostDirectory = File(myhostPath)
                if (!hostDirectory.exists()) {
                    reportConfigRepository.setUpExternalDirectories()
                    //hostDirectory.mkdir() // 👈 create directory

                }
                // 👇 create csv file
                val csvFile = File("${hostDirectory.path}/${fileName}")
                val csvWriter: CSVWriter

                emit(ExportState.Loading)
                try {
                    Log.d("Create File Status (Exists) :  csv_catch", "${csvFile.exists()}")
                    csvWriter = CSVWriter(FileWriter(csvFile))

                    // 👇 write csv file
                    StatefulBeanToCsvBuilder<T>(csvWriter)
                        .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                        .build()
                        .write(content)


                    StatefulBeanToCsvBuilder<Y>(csvWriter)
                        .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                        .build()
                        .write(content2)

                    csvWriter.close()

                    // 👇 emit success
                    emit(ExportState.Success(csvFile.toUri()))
                }
                catch (i_o: IOException) {
                    Log.d("Create File Exception sneak Peek :    csv_catch", i_o.message.toString())
                    CSVWriter(OutputStreamWriter(otherHostPath))
                    emit(ExportState.Error(i_o.message.plus("csv_catch")))
                }



                /*otherHostPath?.flush()
                otherHostPath?.fd?.sync()
                otherHostPath?.close()
                */



            }

        }

    @WorkerThread
    fun writePDF(pdfConfig: PdfConfigImpl, content: TransactionTablePDFManager, content2: DailyCommissionModelPDFManager) =
        flow<ExportState> {
            with(pdfConfig) {
                val myhostPath : String = if(location.ordinal == WRITETO.EXTERNAL.ordinal)
                    hostPath
                else
                    internalhostPath

                Log.d("HostPath Value : ", "${myhostPath}")
                myhostPath.ifEmpty {
                    Log.d("Shouldn't Exist ", "Shouldn't Exist")
                    throw IllegalStateException("Wrong Path") }
                val hostDirectory = File(myhostPath)
                if (!hostDirectory.exists()) {
                    reportConfigRepository.setUpExternalDirectories()
                    // hostDirectory.mkdir() // 👈 create directory
                        //hostDirectory.mkdir() // 👈 create directory
                }
                emit(ExportState.Loading)
                // 👇 create csv file
                val pdfFile = File("${hostDirectory.path}/${fileName}")
                try {
                    pdfFile.createNewFile()
                    // 👇 emit success
                    Log.d("Create File Status 11:  ", "Delete%%%Me")

                    val simplyPdfDocument = SimplyPdf.with(
                        app,
                        pdfFile
                    )
                        .colorMode(DocumentInfo.ColorMode.COLOR)
                        .paperSize(PrintAttributes.MediaSize.ISO_A3)
                        .margin(
                            Margin(15u, 20u, 15u, 20u)
                        )
                        .firstPageBackgroundColor(Color.WHITE)
                        .paperOrientation(DocumentInfo.Orientation.PORTRAIT)
                        .build()


                    content.setColumn1Width(simplyPdfDocument.usablePageWidth / 25)
                    content.setColumn2Width(simplyPdfDocument.usablePageWidth / 9)
                    content.setColumn3Width(simplyPdfDocument.usablePageWidth / 8)
                    content.setColumn4Width(simplyPdfDocument.usablePageWidth / 9)
                    content.setColumn5Width(simplyPdfDocument.usablePageWidth / 6)
                    content.setColumn6Width(simplyPdfDocument.usablePageWidth / 12)
                    content.setColumn7Width(simplyPdfDocument.usablePageWidth / 10)
                    content.setColumn8Width(simplyPdfDocument.usablePageWidth / 6)
                    content.tableRowsMaker()

                    simplyPdfDocument.text.write("\n\nTransaction Data\n\n", TextProperties().apply {
                        textColor = "#000000"
                        textSize = 24
                    },simplyPdfDocument.usablePageWidth/3)
                    simplyPdfDocument.table.draw(content.returnList, tableProperties)
                    simplyPdfDocument.newPage()
                    simplyPdfDocument.text.write("\n\nCommission Data\n\n", TextProperties().apply {
                        textColor = "#000000"
                        textSize = 24
                    },simplyPdfDocument.usablePageWidth/3)
                    //RESET  AFFECTED COLUMN WIDTHS
                    content2.setColumn1Width(simplyPdfDocument.usablePageWidth / 4)
                    content2.setColumn2Width(simplyPdfDocument.usablePageWidth / 4)
                    content2.setColumn3Width(simplyPdfDocument.usablePageWidth / 4)
                    content2.tableRowsMaker()


                    simplyPdfDocument.table.draw(content2.returnList, tableProperties)

                    //WRITE EOF FLAG
                    simplyPdfDocument.text.write("\n\n :::::::::::::::EOF::::::::::::::\n\n", TextProperties().apply {
                        textColor = "#000000"
                        textSize = 24
                    },simplyPdfDocument.usablePageWidth/3)
                    simplyPdfDocument.finish()


                    emit(ExportState.Success(pdfFile.toUri()))
                }
                catch (http:HttpException){
                    emit(ExportState.Error(http.message()))
                    Log.d("Create File Status 33:  ", " Won' t :: Show${http.message}\n ${http.stackTrace.toString()}")
                }
                catch (i_o: IOException){
                    emit(ExportState.Error(i_o.message + "pdf_catch"))
                    Log.d("Create File Status 29 -->  ", "${i_o.message.plus("pdf_catch")}\n ${i_o.stackTrace.toString()}")
                }
                catch (e: Exception) {
                    emit(ExportState.Error(e.message + "pdf_catch"))
                    Log.d("Create File Status 22:  ", "${e.message}\n ${e.stackTrace.toString()}")
                }

        }
        }
}
