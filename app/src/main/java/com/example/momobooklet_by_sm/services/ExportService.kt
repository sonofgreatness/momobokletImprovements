package com.example.momobooklet_by_sm.services

import android.annotation.SuppressLint
import android.graphics.Color
import android.print.PrintAttributes
import android.util.Log
import androidx.annotation.WorkerThread
import com.example.momobooklet_by_sm.services.csv.CsvConfig
import com.example.momobooklet_by_sm.services.csv.Exportable
import com.example.momobooklet_by_sm.services.csv.Exports
import com.example.momobooklet_by_sm.services.pdf.DailyCommissionModelPDFManager
import com.example.momobooklet_by_sm.services.pdf.PdfConfig
import com.example.momobooklet_by_sm.services.pdf.TransactionTablePDFManager
import com.example.momobooklet_by_sm.util.classes.WRITETO
import com.opencsv.CSVWriter
import com.opencsv.bean.StatefulBeanToCsvBuilder
import com.wwdablu.soumya.simplypdf.SimplyPdf
import com.wwdablu.soumya.simplypdf.composers.properties.TextProperties
import com.wwdablu.soumya.simplypdf.document.DocumentInfo
import com.wwdablu.soumya.simplypdf.document.Margin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileWriter
import java.io.OutputStreamWriter

@SuppressLint("LogNotTimber")
object ExportService {

    fun <T ,Y : Exportable> export(type: Exports, content: List<T>, content2: List<Y>): Flow<Boolean> =
        when (type) {
            is Exports.CSV -> writeToCSV(type.csvConfig, content,content2)
            else -> {
                TODO("FAILURE TO DO A PROPER REFACTOR, revisit later when you are better at this")
            }
        }


    fun exportPdf(type: PdfConfig, content: TransactionTablePDFManager,content2: DailyCommissionModelPDFManager): Flow<Boolean> =
        writePDF(type, content,content2)


    @WorkerThread
    fun <T,Y : Exportable> writeToCSV(csvConfig: CsvConfig, content: List<T>,content2: List<Y>) =
        flow<Boolean> {
            with(csvConfig) {

                val myhostPath : String = if(location.ordinal == WRITETO.EXTERNAL.ordinal)
                    hostPath
                else
                    internalhostPath

                myhostPath.ifEmpty { throw IllegalStateException("Wrong Path") }
                val hostDirectory = File(myhostPath)
                if (!hostDirectory.exists()) {
                    setUpExternalDirectories()
                    //hostDirectory.mkdir() // 👈 create directory

                }
                // 👇 create csv file
                val csvFile = File("${hostDirectory.path}/${fileName}")
                val csvWriter: CSVWriter

                csvWriter = try {
                    Log.d("Create File Status (Exists) :  ", "${csvFile.exists()}")
                    CSVWriter(FileWriter(csvFile))
                } catch (e: Exception) {
                    Log.d("Create File Exception sneak Peek :  ", e.message.toString())
                    CSVWriter(OutputStreamWriter(otherHostPath))
                }

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

                /*otherHostPath?.flush()
                otherHostPath?.fd?.sync()
                otherHostPath?.close()
                */

            }
            // 👇 emit success
            emit(true)
        }

    @WorkerThread
    fun writePDF(pdfConfig: PdfConfig, content: TransactionTablePDFManager,content2: DailyCommissionModelPDFManager) =
        flow {
            with(pdfConfig) {
                val myhostPath : String = if(location.ordinal == WRITETO.EXTERNAL.ordinal)
                    hostPath
                else
                    internalhostPath

                myhostPath.ifEmpty { throw IllegalStateException("Wrong Path") }
                val hostDirectory = File(myhostPath)
                if (!hostDirectory.exists()) {
                    setUpExternalDirectories()
                    // hostDirectory.mkdir() // 👈 create directory
                        //hostDirectory.mkdir() // 👈 create directory
                }
                // 👇 create csv file
                val pdfFile = File("${hostDirectory.path}/${fileName}")
                try {
                    pdfFile.createNewFile()
                    Log.d(
                        "Create File Status11:  ",
                        "${hostDirectory.path}/${fileName}  -.${pdfFile.createNewFile()}"
                    )

                } catch (e: Exception) {
                    Log.d("Create File Status 22:  ", "${e.message}\n ${e.stackTrace.toString()}")
                }

                try {
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
                } catch (e: Exception) {
                    Log.d("Create PDF Status : -> ", "${e.message}")
                }
            }
            // 👇 emit success
            emit(true)
        }
}
