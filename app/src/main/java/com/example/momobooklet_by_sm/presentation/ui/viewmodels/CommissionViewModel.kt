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
import com.example.momobooklet_by_sm.common.util.classes.operationalStates.ExportState
import com.example.momobooklet_by_sm.data.local.models.CommissionModel
import com.example.momobooklet_by_sm.data.local.models.DailyCommissionModel
import com.example.momobooklet_by_sm.data.local.models.PeriodicCommissionModel
import com.example.momobooklet_by_sm.data.local.models.TransactionModel
import com.example.momobooklet_by_sm.domain.managefiles.csv.models.DailyCommissionModelCSV
import com.example.momobooklet_by_sm.domain.managefiles.csv.models.TransactionModelCSV
import com.example.momobooklet_by_sm.domain.managefiles.csv.models.dailyCommissiontoCsv
import com.example.momobooklet_by_sm.domain.managefiles.csv.models.toCsv
import com.example.momobooklet_by_sm.domain.repositories.CommissionDatesManagerRepository
import com.example.momobooklet_by_sm.domain.repositories.CommissionRepository
import com.example.momobooklet_by_sm.domain.repositories.TransactionRepository
import com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.ExportService
import com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.CsvConfigImpl
import com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.Exports

import com.example.momobooklet_by_sm.domain.managefiles.pdf.models.DailyCommissionModelPDFManager
import com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.pdf.PdfConfigImpl
import com.example.momobooklet_by_sm.domain.managefiles.pdf.models.TransactionTablePDFManager
import com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.WriteReportUseCase
import com.wwdablu.soumya.simplypdf.composers.Composer
import com.wwdablu.soumya.simplypdf.composers.properties.ImageProperties
import com.wwdablu.soumya.simplypdf.composers.properties.TextProperties
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.*
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
    val datesManager: CommissionDatesManagerRepository,
    private val writeReportUseCase: WriteReportUseCase
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
    //USE_CASE CANDIDATE
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
     *@return : Returns string version of parameter in format #.##
     **************************************************************************/
    fun makeADouble_ACurrency_String(toBeConverted: Double?): String {

        val df = DecimalFormat("##.###")
        df.roundingMode = RoundingMode.DOWN
        return if (toBeConverted != null)
            df.format(toBeConverted).toString().plus(" SZL")
        else
            "0.00 SZL"
    }





    fun setCustomDaysCommissionModel(date: String) {
        viewModelScope.launch {
            commission_repository.getDailyCommissionModel(date).let {
                _customDayCommission.postValue(it)
            }
        }
    }



    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    fun writeReport(startDate: String, type: ReportType) {

    viewModelScope.launch {
        Timber.d("ExportState 0.0 -> EmptyMadeIt")
            writeReportUseCase(startDate, type).collect{
                 when(it){
                is ExportState.Empty ->{
                Timber.d("ExportState 1 -> EmptyMadeIt")}
                is  ExportState.Loading -> {
                Timber.d("ExportState 2 -> LoadingMadeIt")
            }
                is ExportState.Success ->{
                Timber.d("ExportState 3 -> SuccessMadeIt")
                    Timber.d("ExportState 3... --> ${it.fileUri}")
            }
                is ExportState.Error ->{

                    if(it.exception_message?.contains("Operation not permitted") == true) {
                     // Request Permission  FiLeManageMent Permission Here
                        Timber.d("ExportState 4 -> ErrorMadeIt  ${it.exception_message}")
                    }
                    else
                        Timber.d("ExportState 4 -> ErrorMadeIt doesn't contain expected IO ${it.exception_message}")
                   }
                 }
            }
        }
    }
}
