package com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases

import android.app.Application
import android.util.Log
import com.example.momobooklet_by_sm.common.util.Constants
import com.example.momobooklet_by_sm.common.util.classes.ReportType
import com.example.momobooklet_by_sm.common.util.classes.WRITETO
import com.example.momobooklet_by_sm.common.util.classes.operationalStates.ExportState
import com.example.momobooklet_by_sm.data.local.models.DailyCommissionModel
import com.example.momobooklet_by_sm.data.local.models.TransactionModel
import com.example.momobooklet_by_sm.domain.managefiles.csv.models.dailyCommissiontoCsv
import com.example.momobooklet_by_sm.domain.managefiles.csv.models.toCsv
import com.example.momobooklet_by_sm.domain.managefiles.pdf.models.DailyCommissionModelPDFManager
import com.example.momobooklet_by_sm.domain.managefiles.pdf.models.TransactionTablePDFManager
import com.example.momobooklet_by_sm.domain.repositories.CommissionDatesManagerRepository
import com.example.momobooklet_by_sm.domain.repositories.CommissionRepository
import com.example.momobooklet_by_sm.domain.repositories.ReportConfigRepository
import com.example.momobooklet_by_sm.domain.repositories.TransactionRepository
import com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.CsvConfigImpl
import com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.Exports
import com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.pdf.PdfConfigImpl
import com.wwdablu.soumya.simplypdf.composers.Composer
import com.wwdablu.soumya.simplypdf.composers.properties.ImageProperties
import com.wwdablu.soumya.simplypdf.composers.properties.TextProperties
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

class WriteReportUseCase @Inject constructor(
    private  val commission_repository: CommissionRepository,
    private val repository: TransactionRepository,
    private val datesManager: CommissionDatesManagerRepository,
    private val reportConfigRepository: ReportConfigRepository,
    private  val application: Application

) {


    operator fun invoke(startDate:String, type: ReportType):Flow<ExportState> = flow {

        try {


            emit(ExportState.Loading)
            val dates: List<String> = dateMaker(type, startDate)

            if (type.ordinal > 3)
               emit(
                   writeCSVReport(
                                 dates, CsvConfigImpl(
                                 application, type.name, datesManager,
                                  WRITETO.EXTERNAL, reportConfigRepository
                                   )
                                 )
                  )
            else
                emit(
                    writePDFReportTransactions(
                                            dates,PdfConfigImpl(
                                            application, type.name,datesManager,
                                            WRITETO.EXTERNAL, reportConfigRepository
                                            )
                                        )
                    )

        }
        catch (e:Exception)
        {
            emit(ExportState.Error(e.message))
            Timber.d("UseCaseError -> ${e.message}")
        }



    }




    private suspend fun writeCSVReport(dates:List<String>, csvConfig: CsvConfigImpl):ExportState{

        Timber.d("ExportState 0.00 WriteCSV -> EmptyMadeIt")
        lateinit var myExportState:ExportState
        exportTransactionsAndCommissionsToCsv(dates,csvConfig).collect{
                     myExportState = it
            }
        return myExportState
        }



    /***********************************************************************
     * exportTransactionsToCsv -> exports Transaction and Commission Data
     *                              of chosen dates into one file
     *@param dates,   List of Dates which are strings of the form
     *                                          dd-MM-YYYY
     *@param CsvConfig , The configurations class for creating CSVs
     ***********************************************************************/
    private  fun exportTransactionsAndCommissionsToCsv(dates: List<String>, csvConfig: CsvConfigImpl): Flow<ExportState> =
         flow<ExportState>{
            // 👇 state manager for loading | error | success
            // _exportCsvState.value = ViewState.LoadingSuccess
             emit(ExportState.Empty)
            // 👇 get all transaction detail from repository
            val transactions = getWeeksTransactions(dates)
            val commissions = getWeeksDailyCommissionModels(dates)
            // 👇 call export function from Export service
            ExportService.export(
                type = Exports.CSV(csvConfig), // 👈 apply config + type of export
                content = transactions.toCsv(),// 👈 send transformed data of exportable type
                content2 = commissions.dailyCommissiontoCsv()
            ).catch { error ->
                // 👇 handle error here
                Timber.d("Write Transactions to CSV a FAILURE->${error}")
                emit(ExportState.Error(error.message))
            }.collect { ExportState ->
                // 👇 do anything on success
                Timber.d("Write Transactions to CSV a SUCCESS ---> $ExportState")
                emit(ExportState)
            }
        }



    /**************************************************************
     * writePDFReportTransactions -> writes a Full report
     *                TransactionModels  + DailyCommissionModels
     ***********************************************************/
    private suspend fun writePDFReportTransactions(dates: List<String>, pdfConfig: PdfConfigImpl):ExportState
    {

        lateinit var myExportState:ExportState
        exportTransactionsAndCommissionsToPdf(dates, pdfConfig).collect{
            myExportState = it
        }
        return myExportState
    }

    private   fun exportTransactionsAndCommissionsToPdf(dates: List<String>, pdfConfig: PdfConfigImpl) =

    flow<ExportState>{
        // 👇 state manager for loading | error | success

        emit(ExportState.Empty)
        val transactions = getWeeksTransactions(dates)
        val commissions  = getWeeksDailyCommissionModels(dates)
        val myTextProperties = TextProperties().apply{
            textSize = Constants.PDF_TABLE_TEXT_SIZE
            textColor = Constants.PDF_TABLE_TEXT_COLOR
        }

        val myImageProperties = ImageProperties().apply {
            alignment = Composer.Alignment.START
        }

        val commissionspdftableManager =   DailyCommissionModelPDFManager(commissions,myTextProperties)
        val pdftableManager=
            TransactionTablePDFManager(transactions,myTextProperties, myImageProperties)

        // 👇 call export function from Export serivce
        ExportService.exportPdf(
            type = pdfConfig, // 👈 apply config + type of export
            content = pdftableManager,
            content2= commissionspdftableManager // 👈 send transformed data of exportable type
        ).catch { error ->
            // 👇 handle error here
            //  _exportCsvState.value = ViewState.Error(error)
            Timber.d("Write  Transactions to PDF a FAILURE->${error}")
        }.collect { ExportState ->
            // 👇 do anything on success
            // _exportCsvState.value = ViewState.Success(emptyList())
            // Let User Know You are done.
            Timber.d("Write Transactions to PDF a SUCCESS ")
            emit(ExportState)
        }
    }




    /***************************************************************************
     * getWeeksDailyCommissionModels - reads database to find all
     *              DailyCommissionModels with DailyCommissionModel.date  = date
     *@param weekdays , a list of dates that are used to perform the comparison
     *@return Returns a List of DailyCommissionModels
     ****************************************************************************/
    private suspend fun getWeeksDailyCommissionModels(weekdays: List<String>): List<DailyCommissionModel> {
        val weekCommission: MutableList<DailyCommissionModel> = ArrayList()

        for (date in weekdays)
            commission_repository.getDailyCommissionModel(date.trim())?.let {
                weekCommission.add(it)
            }
        return weekCommission
    }




    /**************************************************************
     * getWeeksTransactions - gets Transactions  by comparing  Transaction dates
     *                      with set of strings , The set can be longer the 7 elements
     *                       i.e it can get A week's Transactions or : 2weeks, A month (30days)
     *                        or 3 months (90days)
     * @param weekdays: List of Strings , the set of strings to compare to Transaction dates
     *@return Returns a  list of TransactionModels
     *****************************************************/
    private suspend fun getWeeksTransactions(weekdays: List<String>): List<TransactionModel> {
        var weekTransactions: List<TransactionModel> = ArrayList()

        for (date in weekdays)
            repository.getDailyTransactions(date).let {
                weekTransactions = weekTransactions + it
            }
        return weekTransactions
    }



    private fun dateMaker(
        type: ReportType,
        startDate: String
    ): List<String> {
        val TypeOrdinal = type.ordinal % 4
        lateinit var dates: List<String>

        when (TypeOrdinal) {
            0 -> {
                dates = datesManager.getWeekDates_(startDate)
            }
            1 -> {
                dates = datesManager.getBiWeeklyDates_(startDate)
            }
            2 -> {
                dates = datesManager.getMonthDates_(startDate)
            }
            3 -> {
                dates = datesManager.getTriMonthDates(startDate)
            }
        }
        Log.d("testMyDate ", "${type.name} =   ${dates.size}::: \n ${dates.toString()}")
        return dates
    }


    /********************************************************************
     * getLastMonthStartAndEndDates -> gives the start and
     *                              end dates used to calculate this month's
     *                              commission
     * @return : Returns an array of size 2  [startDate, endDate]
     ********************************************************************/
    fun getThisMonthStartAndEndDates(): Array<String> {
        val listofDates = datesManager.getThisMonthDates_()
        val size = listofDates.size
        return arrayOf(listofDates[0], listofDates[size - 1])

    }




    }


