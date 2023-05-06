package com.example.momobooklet_by_sm.presentation.ui.viewmodels
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.momobooklet_by_sm.common.util.Constants
import com.example.momobooklet_by_sm.common.util.classes.ReportType
import com.example.momobooklet_by_sm.data.local.models.CommissionModel
import com.example.momobooklet_by_sm.data.local.models.DailyCommissionModel
import com.example.momobooklet_by_sm.data.local.models.PeriodicCommissionModel
import com.example.momobooklet_by_sm.data.local.models.TransactionModel
import com.example.momobooklet_by_sm.domain.repositories.CommissionDatesManagerRepository
import com.example.momobooklet_by_sm.domain.repositories.CommissionRepository
import com.example.momobooklet_by_sm.domain.repositories.TransactionRepository
import com.example.momobooklet_by_sm.domain.services.ExportService
import com.example.momobooklet_by_sm.domain.services.csv.CsvConfigImpl
import com.example.momobooklet_by_sm.domain.services.csv.Exports
import com.example.momobooklet_by_sm.domain.services.csv.adapters.DailyCommissionModelCSV
import com.example.momobooklet_by_sm.domain.services.csv.adapters.TransactionModelCSV
import com.example.momobooklet_by_sm.domain.services.csv.adapters.dailyCommissiontoCsv
import com.example.momobooklet_by_sm.domain.services.csv.adapters.toCsv
import com.example.momobooklet_by_sm.domain.services.pdf.DailyCommissionModelPDFManager
import com.example.momobooklet_by_sm.domain.services.pdf.PdfConfigImpl
import com.example.momobooklet_by_sm.domain.services.pdf.TransactionTablePDFManager
import com.wwdablu.soumya.simplypdf.composers.Composer
import com.wwdablu.soumya.simplypdf.composers.properties.ImageProperties
import com.wwdablu.soumya.simplypdf.composers.properties.TextProperties
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject
/************************************************************************************
 * This Class abstracts access to database, to make viewing commissions
 *      from Views easy make make sure user views up to date date
 *  This class also abstracts acess to database for better writing of  report files
 *        The last (bottom most) methods are for that exact purpose
 **********************************************************************************/
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CommissionViewModel @Inject constructor(
    val commission_repository: CommissionRepository,
    val repository: TransactionRepository,
    val datesManager: CommissionDatesManagerRepository


) : ViewModel(),Observable {

    private lateinit var commissionChart: List<CommissionModel>



    val _dailyCommission = MutableLiveData<DailyCommissionModel>()
    val dailyCommission: LiveData<DailyCommissionModel> get() = _dailyCommission


    val _yesterDayCommission = MutableLiveData<DailyCommissionModel>()
    val yesterDayCommission: LiveData<DailyCommissionModel> get() = _yesterDayCommission


    val _customDayCommission = MutableLiveData<DailyCommissionModel>()
    val customDayCommission: LiveData<DailyCommissionModel> get() = _customDayCommission


    val _monthlyCommission = MutableLiveData<PeriodicCommissionModel>()
    val monthlyCommission: LiveData<PeriodicCommissionModel> get() = _monthlyCommission


    val _thisMonthCommission = MutableLiveData<PeriodicCommissionModel>()
    val thisMonthCommission: LiveData<PeriodicCommissionModel> get() = _thisMonthCommission


    init {

        viewModelScope.launch {
            commissionChart = commission_repository.getCommissionChart()
            Timber.d("Commission Chart -> ${commission_repository.getCommissionChart()}")
        }

        /***************************************************************************
         *If Today's Commission Model exits
         *
         *Assigns  _dailyCommission Variable  to Today's CommissionModel
         ****************************************************************************/
        viewModelScope.launch {
            commission_repository.getDaysCommission(datesManager.generateTodayDate()).collect(){
                _dailyCommission.postValue(it)
            }
        }
        /**************************************************************************
         * Check  IF Last Month's Commission exists,
         *             If Not calculate it and add it to DB
         *If it exist Assigns _monthlyCommission Variable
         ***************************************************************************/
        viewModelScope.launch {
            val dates = datesManager.getLastMonthDates_()
            val size = dates.size
            commission_repository.getLastMonthCommission(dates[0], dates[size - 1]).collect {
                if (it == null)
                    calculateLastMonth_Commission()
                else
                    _monthlyCommission.postValue(it)
            }
        }


        viewModelScope.launch {
            getThisMonthCommission().let {
                _thisMonthCommission.postValue(it)
            }
        }

        getYesterdayCommission()
    }


    /********************************************************************
     * addMonthlyCommission  -> creates a PeriodicCommissionModelObject
     *                          and  adds it to database
     *******************************************************************/
    private suspend fun addMonthlyCommission(
        sumofCommissionAmmounts: Double,
        numberofTransactions: Int
    ) {

        val dates = datesManager.getLastMonthDates_()
        val size = dates.size
        val myPeriodicCommission = PeriodicCommissionModel(
            0,
            dates[0],
            dates[size - 1],
            numberofTransactions,
            sumofCommissionAmmounts
        )

        commission_repository.addMonthlyCommission(myPeriodicCommission)

        commission_repository.getLastMonthCommission(dates[0], dates[size - 1]).collect {
            _monthlyCommission.postValue(it)
        }
    }
    //USE_ CASE CANDIDATE
    /*********************************************************************************
     *Reads Database (RECORD_SHEET) table  and calculates
     *         each transaction commission Using Commission Chart table
     *Update DailyCommission Table
     ***********************************************************************************/
    fun calculateThenUpdateDailyCommission() {

        viewModelScope.launch {
            repository.getTodaysTransactions(datesManager.generateTodayDate())
                .collect{ listOfTransactions ->
                    Timber.d("Selected List of Transactions -> ${listOfTransactions.size}")
                    commissionChart.let {
                        if (it != null) {
                            var sum = 0.0

                            for (commissionModel in it) {
                                for (transaction in listOfTransactions) {
                                    val amount_condition: Boolean =
                                        (transaction.Amount >= commissionModel.Min && transaction.Amount <= commissionModel.Max)
                                    val typeCondition: Boolean =
                                        (transaction.Transaction_type == commissionModel.Type)
                                    if (amount_condition && typeCondition) {
                                        Timber.d("Update Commission Called, Amount -> : ${commissionModel.Commission_Amount}")
                                        sum = sum.plus(commissionModel.Commission_Amount)
                                    }
                                }
                            }
                            updateDailyCommissionModel(
                                datesManager.generateTodayDate(),
                                listOfTransactions.size,
                                sum
                            )
                        }
                    }
                }
        }

    }

    /****************************************************************
     *updateDailyCommissionModel - retrieves and Updates A DailyCommissionModel Model in Room db
     *   @param date , string Variable to identify CommissionModel (date is a primary key)
     *   @param updateAmount  the new Amount for DailyCommissionModel
     *****************************************************************/
    fun updateDailyCommissionModel(date: String, numTransaction: Int, updateAmount: Double) {
        viewModelScope.launch {
            var updatedDayModel = DailyCommissionModel(date, numTransaction, updateAmount)
            commission_repository.addDayCommission(updatedDayModel)
        }
    }

    /*********************************************************************
     *getYesterdayCommission() -> gets The previous day's commission
     *                            and updates _yesterDayCommission variable
     *                            with it
     *******************************************************************/
    fun getYesterdayCommission() {
        viewModelScope.launch {

            datesManager.getYesterdayStringUltimate().let { Strin ->
                try {
                    Log.d("yesterdayString", "${Strin}")
                    val today = datesManager.generateTodayDate()
                    commission_repository.getDaysCommission(Strin).collect {
                        _yesterDayCommission.postValue(it)
                        Log.d("yesterdayStringDoubleTomorrowDay : ", "${today}")
                        Log.d("yesterdayStringDouble", "${it}")
                    }
                } catch (e: Exception) {
                    Log.d("yesterdayString", "${e.message} ${e.stackTrace}")
                }

            }
        }
    }


    /*****************************************************************************************
     * calculateLastMonth_Commission -> Sums daily Commission Amount between 2 dates
     *                                   creates a PeriodicCommissionModel with calculated date
     *                                    Assigns  _monthlyCommission Model to created
     *                                                     PeriodicCommissionModel
     *******************************************************************************************/
    private suspend fun calculateLastMonth_Commission() {
        val listofDates = datesManager.getLastMonthDates_()
        var currentDailyCommissionModel: DailyCommissionModel?
        var sumofCommissionAmmounts = 0.0
        var numberofTransactions = 0
        for (date in listofDates) {
            currentDailyCommissionModel = commission_repository.getDailyCommissionModel(date)

            if (currentDailyCommissionModel != null) {
                sumofCommissionAmmounts += currentDailyCommissionModel.Commission_Amount
                numberofTransactions += currentDailyCommissionModel.Number_of_Transactions
            }
        }
        addMonthlyCommission(sumofCommissionAmmounts, numberofTransactions)
    }


    /*******************************************************************
     * getThisMonthCommission() -> calculates this months commission
     *                          by adding DailyCommissionModels
     *                         from *startDate to current date -1 AkA Yesterday
     *@return  return a PeriodicCommissionModel Object with RowId = 0
     *                          (not to be added in database)
     *******************************************************************/

    private suspend fun getThisMonthCommission(): PeriodicCommissionModel {
        val listofDates = datesManager.getThisMonthDates_()
        var currentDailyCommissionModel: DailyCommissionModel?
        var sumofCommissionAmmounts = 0.0
        var numberofTransactions = 0
        val size = listofDates.size
        for (date in listofDates) {
            currentDailyCommissionModel = commission_repository.getDailyCommissionModel(date)

            if (currentDailyCommissionModel != null) {
                sumofCommissionAmmounts += currentDailyCommissionModel.Commission_Amount
                numberofTransactions += currentDailyCommissionModel.Number_of_Transactions

            }
        }
        return PeriodicCommissionModel(
            0, listofDates[0], listofDates[size - 1], numberofTransactions, sumofCommissionAmmounts
        )
    }


    /**************************************************************************
     * makeADouble_ACurrency_String -> converts a double into
     *                                      a string , formatted  look like
     *                                      currency
     *@param toBeConverted Double , the value to be converted
     * @return : Returns string version of parameter in format #.##
     **************************************************************************/
    fun makeADouble_ACurrency_String(toBeConverted: Double?): String {

        val df = DecimalFormat("##.###")
        df.roundingMode = RoundingMode.DOWN
        return if (toBeConverted != null)
            df.format(toBeConverted).toString().plus(" SZL")
        else
            "0.00 SZL"
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


    fun setCustomDaysCommissionModel(date: String) {
        viewModelScope.launch {
            commission_repository.getDailyCommissionModel(date).let {
                _customDayCommission.postValue(it)
            }
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

    /*****************************************************************************************
     * writeReportTransactions -> writes a CSV or PDF  Report of Selected data
     *                              The files are store in internal memory in fileDir folder and
     *                              in external memory  in a folder "MoMoBooklet"
     *                                      with subfolders "PDF" and " CSV"
     ************************************************************************************/
    fun writeReport(startDate:String,type:ReportType)
    {
       /*
        val dates: List<String> = dateMaker(type, startDate)

          if (type.ordinal > 3)
              writeCSVReport(dates, CsvConfigImpl(application,type.name,datesManager,WRITETO.EXTERNAL))

          else
                  writePDFReportTransactions(
                      dates,
                      PdfConfigImpl(application, type.name, datesManager, WRITETO.EXTERNAL))

    */
        print("WROTE  REPORT")
    }





    private fun writeCSVReport(dates:List<String>,csvConfig: CsvConfigImpl) {
        exportTransactionsAndCommissionsToCsv(dates,csvConfig)
    }

    /***********************************************************************
     * exportTransactionsToCsv -> exports Transaction and Commission Data
     *                              of chosen dates into one file
     *@param dates,   List of Dates which are strings of the form
     *                                          dd-MM-YYYY
     *@param CsvConfig , The configurations class for creating CSVs
     ***********************************************************************/
    private fun exportTransactionsAndCommissionsToCsv(dates: List<String>, csvConfig: CsvConfigImpl) =
        viewModelScope.launch(Dispatchers.IO) {
            // 👇 state manager for loading | error | success
            // _exportCsvState.value = ViewState.LoadingSuccess

            // 👇 get all transaction detail from repository
            val transactions = getWeeksTransactions(dates)
            val commissions = getWeeksDailyCommissionModels(dates)
            // 👇 call export function from Export serivce
            ExportService.export<TransactionModelCSV,DailyCommissionModelCSV>(
                type = Exports.CSV(csvConfig), // 👈 apply config + type of export
                content = transactions.toCsv(),// 👈 send transformed data of exportable type
                content2 = commissions.dailyCommissiontoCsv()
            ).catch { error ->
                // 👇 handle error here
                //  _exportCsvState.value = ViewState.Error(error)
                Timber.d("Write Transactions to CSV a FAILURE->${error}")
            }.collect { _ ->
                // 👇 do anything on success
                Timber.d("Write Transactions to CSV a SUCCESS")
            }
        }




    /**************************************************************
     * writePDFReportTransactions -> writes a Full report
     *                TransactionModels  + DailyCommissionModels
     ***********************************************************/
   private fun writePDFReportTransactions(dates: List<String>,pdfConfig:PdfConfigImpl)
    {
        exportTransactionsAndCommissionsToPdf(dates, pdfConfig)
    }

    private fun exportTransactionsAndCommissionsToPdf(dates: List<String>, pdfConfig: PdfConfigImpl) = viewModelScope.launch(IO) {
        // 👇 state manager for loading | error | success
        // _exportCsvState.value = ViewState.LoadingSuccess

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
        }.collect { _ ->
            // 👇 do anything on success
            // _exportCsvState.value = ViewState.Success(emptyList())
            // Let User Know You are done.
            Timber.d("Write Transactions to PDF a SUCCESS")
        }
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
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}
