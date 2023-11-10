package com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.common.util.classes.ReportType
import com.free.momobooklet_by_sm.common.util.classes.WRITETO
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.ExportState
import com.free.momobooklet_by_sm.data.local.models.DailyCommissionModel
import com.free.momobooklet_by_sm.data.local.models.TransactionModel
import com.free.momobooklet_by_sm.domain.managefiles.csv.models.dailyCommissiontoCsv
import com.free.momobooklet_by_sm.domain.managefiles.csv.models.toCsv
import com.free.momobooklet_by_sm.domain.managefiles.pdf.models.DailyCommissionModelPDFManager
import com.free.momobooklet_by_sm.domain.managefiles.pdf.models.TransactionTablePDFManager
import com.free.momobooklet_by_sm.domain.repositories.*
import com.free.momobooklet_by_sm.domain.use_cases.commission.CustomDayCommissionNonFlow
import com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.CsvConfigImpl
import com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.Exports
import com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.pdf.PdfConfigImpl
import com.wwdablu.soumya.simplypdf.composers.Composer
import com.wwdablu.soumya.simplypdf.composers.properties.ImageProperties
import com.wwdablu.soumya.simplypdf.composers.properties.TextProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

class WriteReportUseCase @Inject constructor(
    private val repository: TransactionRepository,
    private val datesManager: CommissionDatesManagerRepository,
    private val reportConfigRepository: ReportConfigRepository,
    private val  userRepository: UserRepository,
    private val customDayCommissionNonFlow: CustomDayCommissionNonFlow,
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
                                            WRITETO.EXTERNAL, reportConfigRepository, userRepository =userRepository,
                                            dates = dates
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
     *@param csvConfig , The configurations class for creating CSVs
     ***********************************************************************/
    private  fun exportTransactionsAndCommissionsToCsv(dates: List<String>, csvConfig: CsvConfigImpl): Flow<ExportState> =
         flow {
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

    flow {
        // 👇 state manager for loading | error | success

        emit(ExportState.Empty)
        val transactions = getWeeksTransactions(dates)
        val commissions  = getWeeksDailyCommissionModels(dates)



        val commissionspdftableManager =   DailyCommissionModelPDFManager(commissions)

        val pdftableManager=
            TransactionTablePDFManager(transactions)

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

        val mainUser = userRepository.getActiveUser()[0]
        val momoNumber = mainUser.MoMoNumber.trim()

        return calculateDailyCommissionModels(weekdays,momoNumber)
    }




    /**************************************************************
     * getWeeksTransactions - gets Transactions  by comparing  Transaction dates
     *                      with set of strings , The set can be longer the 7 elements
     *                       i.e it can get A week's Transactions or : 2weeks, A month (30days)
     *                        or 3 months (90days)
     * @param weekdays: List of Strings , the set of strings to compare to Transaction dates
     *@return Returns a  list of TransactionModels
     *****************************************************/
    @SuppressLint("LogNotTimber")
    private suspend fun getWeeksTransactions(weekdays: List<String>): List<TransactionModel> {
        var weekTransactions: List<TransactionModel> = ArrayList()

        try {

            val momoNumber = userRepository.getActiveUser()[0].MoMoNumber.trim()

            for (date in weekdays)
                repository.getDailyTransactions(date,momoNumber).let {
                    weekTransactions = weekTransactions + it
                }
        }catch (ex:Exception)
        {
            Log.d("reportTransactions Failure", "")
        }
        return weekTransactions
    }

    /***************************************************************************
     * Calculates the commission earned on  a  list of days
     *    and updates the Database Daily Commission table
     *      excluding days where no transactions were made
     * @param days List of days
     * @param momoNumber phone number of active account
     ***************************************************************************/
    private  suspend fun calculateDailyCommissionModels(days: List<String>, momoNumber: String):List<DailyCommissionModel> {

        val weekCommission: MutableList<DailyCommissionModel> = ArrayList()
        lateinit  var totalRow:DailyCommissionModel
        var totalTransactions =0
        var totalCommission = 0.0
        for (day in days ) {

            val dayCommissionModel =calculateDailyCommissionModel(day, momoNumber)
            if (dayCommissionModel.Number_of_Transactions != 0) {

                totalTransactions += dayCommissionModel.Number_of_Transactions
                totalCommission += dayCommissionModel.Commission_Amount

                weekCommission.add(dayCommissionModel)

            }
        }

        totalRow  = DailyCommissionModel("TOTALS",
                                          totalTransactions,
                                          totalCommission,
                                          momoNumber
                                         )

         weekCommission.add(totalRow)

        return  weekCommission
    }

    /***************************************************************************
     * Calculates the commission earned on  a  list of days
     *    and updates the Database Daily Commission table
     * @param day  day
     * @param momoNumber phone number of active account
     ***************************************************************************/
    private  suspend fun calculateDailyCommissionModel(day: String, momoNumber: String): DailyCommissionModel {
        val commissionAmount = 0.0
        var numberOfTransactions: Int


        repository.getDailyTransactions(day,momoNumber).let{
                   numberOfTransactions = it.size
        }

        val dayCommissionModel = DailyCommissionModel(day,
                                                      numberOfTransactions,
                                                      commissionAmount,
                                                      momoNumber
                                                     )

        return customDayCommissionNonFlow(day)?:dayCommissionModel
    }

    @SuppressLint("LogNotTimber")
    private fun dateMaker(
        type: ReportType,
        startDate: String
    ): List<String> {
        val typeOrdinal = type.ordinal % 4
        lateinit var dates: List<String>

        when (typeOrdinal) {
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
        Log.d("testMyDate ", "${type.name} =   ${dates.size}::: \n $dates")
        return dates
    }

}


