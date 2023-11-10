package com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases

import android.annotation.SuppressLint
import android.graphics.Color
import android.print.PrintAttributes
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.core.net.toUri
import com.free.momobooklet_by_sm.common.util.classes.WRITETO
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.ExportState
import com.free.momobooklet_by_sm.domain.managefiles.pdf.models.DailyCommissionModelPDFManager
import com.free.momobooklet_by_sm.domain.managefiles.pdf.models.TransactionTablePDFManager
import com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.pdf.PdfConfigImpl
import com.wwdablu.soumya.simplypdf.SimplyPdf
import com.wwdablu.soumya.simplypdf.SimplyPdfDocument
import com.wwdablu.soumya.simplypdf.composers.properties.TextProperties
import com.wwdablu.soumya.simplypdf.document.DocumentInfo
import com.wwdablu.soumya.simplypdf.document.Margin
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.File
import java.io.IOException

class WritePDF (val pdfConfig: PdfConfigImpl, val content: TransactionTablePDFManager, val content2: DailyCommissionModelPDFManager) {


    @SuppressLint("LogNotTimber")
    @WorkerThread
    fun writePDF() =
        flow {
            with(pdfConfig) {
                val myhostPath : String = if(location.ordinal == WRITETO.EXTERNAL.ordinal)
                    hostPath
                else
                    internalhostPath

                reportConfigRepository.setUpInternalDirectories()
                Log.d("HostPath Value : ", myhostPath)

                try{
                    val myhostPath2   = internalhostPath
                    val hostDirectory2 = File(myhostPath2)
                    val pdfFile2 = File("${hostDirectory2.path}/files/${fileName}")

                    pdfFile2.createNewFile()
                    // 👇 emit success
                    Log.d("Create File Status 11:  ", "Delete%%%Me")

                    val simplyPdfDocument = SimplyPdf.with(
                        app,
                        pdfFile2
                    )
                        .colorMode(DocumentInfo.ColorMode.COLOR)
                        .paperSize(PrintAttributes.MediaSize.ISO_A3)
                        .margin(
                            Margin(15u, 20u, 15u, 5u)
                        )
                        .firstPageBackgroundColor(Color.WHITE)
                        .paperOrientation(DocumentInfo.Orientation.PORTRAIT)//testing
                        .build()
                    setUpForA3(simplyPdfDocument)

                    writeReportHeaders(simplyPdfDocument)
                    drawTransactionsTable(simplyPdfDocument)
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
                }
                catch (ex: Exception)
                {
                    Timber.d("internal pdf write fail")
                }


                myhostPath.ifEmpty {
                    throw IllegalStateException("Wrong Path") }
                val hostDirectory = File(myhostPath)
                if (!hostDirectory.exists()) {
                    reportConfigRepository.setUpExternalDirectories()

                }

                emit(ExportState.Loading)

                try {

                    // 👇 create pdf file
                    val pdfFile = File("${hostDirectory.path}/${fileName}")

                    if(pdfFile.exists())
                           pdfFile.delete()

                    pdfFile.createNewFile()
                    // 👇 emit success
                    Log.d("Create File Status 101:  ", "Delete%%%Me")

                    val simplyPdfDocument = SimplyPdf.with(
                        app,
                        pdfFile
                    )
                        .colorMode(DocumentInfo.ColorMode.COLOR)
                        .paperSize(PrintAttributes.MediaSize.ISO_A3)
                        .margin(
                            Margin(15u, 20u, 15u, 5u)
                        )
                        .firstPageBackgroundColor(Color.WHITE)
                        .paperOrientation(DocumentInfo.Orientation.LANDSCAPE)//testing
                        .build()
                    setUpForA3(simplyPdfDocument)


                    writeReportHeaders(simplyPdfDocument)

                    drawTransactionsTable(simplyPdfDocument)

                    simplyPdfDocument.newPage()

                    simplyPdfDocument.text.write("\n\nCommission Data\n\n", TextProperties().apply {
                        textColor = "#000000"
                        textSize = 48
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
                        textSize = 48
                    },simplyPdfDocument.usablePageWidth/3)

                    simplyPdfDocument.finish()

                    Log.d("Create File Status 101:  ", "external pdf write finish")
                    emit(ExportState.Success(pdfFile.toUri()))
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

    /*****************************************************************************************************
     * writeReportHeaders -> Writes the  user name and phone number of user  in control
     *                            on top left
     *                      On the right  the date the report was generated
     *                      on the center  the Report Heading
     *
     * @param simplyPdfDocument  - instance of the pdf  file on which to draw table
     *****************************************************************************************************/
    private suspend fun PdfConfigImpl.writeReportHeaders(simplyPdfDocument: SimplyPdfDocument) {

        val userName = userRepository.getActiveUser()[0].MoMoName
        val userNumber = userRepository.getActiveUser()[0].MoMoNumber
        val reportStartDate =dates[0]
        val reportEndDate = dates[dates.lastIndex]
        val dateToday = suffix
        val reportTitle = prefix.plus("  REPORT")

        writeUserRelatedHeaders(simplyPdfDocument, userName, userNumber, generateDateRelatedHeaders(dateToday))
        writeMainHeader(simplyPdfDocument, reportEndDate, reportStartDate,reportTitle)

    }
    /**********************************************************************************************
     * writeUserRelatedHeaders - Writes the  user name and phone number of user  in control
     *                            on top left
     *
     * @param userName : String  Active user's  registered name
     * @param userNumber : String  Active user's registered cell number
     **********************************************************************************************/
    private fun writeUserRelatedHeaders(
        simplyPdfDocument: SimplyPdfDocument,
        userName: String,
        userNumber: String,
        dateHeaders: List<String>
    ) {


        val formattedUserName = formatUserName(userName)
        val formattedUsernumber = formattedUserNumber(userNumber)

        val string = formattedUserName.plus(dateHeaders[0]).plus("\n").plus(formattedUsernumber).plus(dateHeaders[1]).plus("\n\n")

        simplyPdfDocument.text.write(string, TextProperties().apply {
            textColor = "#000000"
            textSize = 15
        }, simplyPdfDocument.usablePageWidth,  simplyPdfDocument.startMargin,20)

    }


    /*********************************************************************************
     * formats user phone number to be  20 characters long
     * @param userNumber : String the 8 digit user's number
     * @return Returns  Co
     *********************************************************************************/
    private fun formattedUserNumber(userNumber: String): String {
        return ("( +268 )".plus(userNumber).plus("    "))
    }

    /***************************************************************************
     * formats user name to be exactly 20 characters long
     *     by appending ""
     *  or  cutting string
     **************************************************************************/
    private fun formatUserName(userName: String): String {

        var returnUsername = userName
        val comparator = userName.length.compareTo(20)

            if (comparator == 0 )
                return userName
        return if (comparator > 0)
            userName.substring(0,20)
        else {
            for (i in 1..(20.minus(userName.length))) {
                returnUsername = returnUsername.plus(" ")
            }
            returnUsername
        }

    }


    /**********************************************************************************************
     * generateDateRelatedHeaders -> creates top right header text
     *
     *
     * @param `Today's Date`:String  date  on which report in being generated
     *@return List<String>
     **********************************************************************************************/
    private fun generateDateRelatedHeaders(dateToday : String) : List<String> {
        return listOf("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tGenerated On: ", "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t".plus(dateToday))

    }
    private fun writeMainHeader(simplyPdfDocument: SimplyPdfDocument, reportEndDate: String, reportStartDate: String, reportTitle:  String ) {

         val string  = (" from ").plus(reportStartDate).plus("  to ").plus(reportEndDate).plus("\n\n")

        simplyPdfDocument.text.write(reportTitle, TextProperties().apply {
            textColor = "#000000"
            textSize = 26
        }, simplyPdfDocument.usablePageWidth / 2, 30, 56)

        simplyPdfDocument.text.write(string, TextProperties().apply {
            textColor = "#000000"
            textSize = 14
        }, simplyPdfDocument.usablePageWidth / 2, 30)

    }
    /***************************************************************************************
     * drawTransactionsTable -draws table using Transaction data
     *@param simplyPdfDocument  - instance of the pdf  file on which to draw table
     ***************************************************************************************/
    private fun PdfConfigImpl.drawTransactionsTable(
        simplyPdfDocument: SimplyPdfDocument
    ) {
        content.tableRowsMaker()
        simplyPdfDocument.table.draw(content.returnList, tableProperties)
    }


    private fun setUpForA3(simplyPdfDocument: SimplyPdfDocument) {
        content.setColumn1Width(simplyPdfDocument.usablePageWidth / 25)
        content.setColumn2Width(simplyPdfDocument.usablePageWidth / 9)
        content.setColumn3Width(simplyPdfDocument.usablePageWidth / 8)
        content.setColumn4Width(simplyPdfDocument.usablePageWidth / 9)
        content.setColumn5Width(simplyPdfDocument.usablePageWidth / 6)
        content.setColumn6Width(simplyPdfDocument.usablePageWidth / 12)
        content.setColumn7Width(simplyPdfDocument.usablePageWidth / 10)
        content.setColumn8Width(simplyPdfDocument.usablePageWidth / 6)
    }

}