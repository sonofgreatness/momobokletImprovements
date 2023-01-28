package com.example.momobooklet_by_sm.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.momobooklet_by_sm.database.local.models.CommissionModel
import com.example.momobooklet_by_sm.database.local.models.DailyCommissionModel
import com.example.momobooklet_by_sm.database.local.Database
import com.example.momobooklet_by_sm.database.local.models.TransactionModel
import com.example.momobooklet_by_sm.database.local.repositories.CommissionRepository
import com.example.momobooklet_by_sm.database.local.repositories.TransactionRepository
import com.example.momobooklet_by_sm.util.CommisionDatesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


class TransactionViewModel(
    application: Application

) : AndroidViewModel(application) {



    private val datesManager:CommisionDatesManager = CommisionDatesManager()

    val _searchResults = MutableLiveData<List<TransactionModel>>()
    val searchResults: LiveData<List<TransactionModel>>
        get() = _searchResults


    private lateinit var commissionChart : List<CommissionModel>
    private val repository: TransactionRepository
    private val commission_repository:CommissionRepository

    val _dailyCommission = MutableLiveData<Double>()
    val dailyCommission : LiveData<Double> get() = _dailyCommission

    val _triggerNoResultsToast = MutableLiveData<Boolean>()
    val triggerNoResultsToast : LiveData<Boolean> get() = _triggerNoResultsToast



    init {
        val userDao = Database.getDatabase(application)?.getDao()
        val commissionDao = Database.getDatabase(application)?.getCommissionDao()
        commission_repository = commissionDao?.let{ CommissionRepository(it) }
        repository = userDao?.let { TransactionRepository(it) }!!
        _triggerNoResultsToast.postValue(false)


        viewModelScope.launch {

            repository.readAllTransactiondata().collect(){
                _searchResults.postValue(it)
            }
            Timber.e("t_viewM->${repository.readAllTransactiondata()}")
        }
        viewModelScope.launch{
            commissionChart = commission_repository.getCommissionChart()
        Timber.d("Commission Chart -> ${commission_repository.getCommissionChart()}")
        }

        /***************************************************************************
         *Checks if Today's Commission Model exits
         *    if not Creates it
         *Assigns  _dailyCommission Variable  to Today's CommissionModel
         ****************************************************************************/
        viewModelScope.launch{
          if(commission_repository.getDaysCommissionModel(datesManager.generateTodayDate()) == null)
                commission_repository.addDayCommission(DailyCommissionModel(datesManager.generateTodayDate(),0,0.0))
            commission_repository.getDaysCommission(datesManager.generateTodayDate()).collect(){
                _dailyCommission.postValue(it)
            }
        }

    }

    fun getAllTransaction() = viewModelScope.launch {
        repository.readAllTransactiondata().collect(){
            _searchResults.postValue(it)
        }
    }

    fun addTransaction(transaction: TransactionModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTransaction(transaction)
            Timber.e("add_transaction called in viewmodel")
        }

    }
    fun removeTransaction(transaction: TransactionModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeTransaction(transaction)
        }
    }
    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }
    /*********************************************************************************
     *Reads Database (RECORD_SHEET) table  and calculates
     *         each transaction commission Using Commission Chart table
     *Update DailyCommission Table
     ***********************************************************************************/
    fun calculateThenUpdateDailyCommission()
    {

        viewModelScope.launch {
                repository.getTodaysTransactions(datesManager.generateTodayDate()).collect(){ listOfTransactions->
                    Timber.d("Selected List of Transactions -> ${listOfTransactions.size}")
            commissionChart.let {
                if (it != null && listOfTransactions != null)
                {

                    var sum = 0.0

                    for (commissionModel in it)
                    {   for (transaction in listOfTransactions) {


                        val amount_condition: Boolean =
                            (transaction.Amount >= commissionModel.Min && transaction.Amount < commissionModel.Max)
                        val typeCondition: Boolean =
                            (transaction.Transaction_type == commissionModel.Type)
                        if (amount_condition && typeCondition) {
                            Timber.d("Update Commission Called, Amount -> : ${commissionModel.Commission_Amount}")
                                sum = sum.plus(commissionModel.Commission_Amount)

                        }
                     }
                    }
                    updateDailyCommissionModel(datesManager.generateTodayDate(), listOfTransactions.size,sum)
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
    fun updateDailyCommissionModel(date:String,numTransaction:Int, updateAmount:Double)
    {
        viewModelScope.launch {
            var updatedDayModel = DailyCommissionModel(date, numTransaction,updateAmount)
            //updateDailyCommission Model
            commission_repository.addDayCommission(updatedDayModel)
        }
    }

    /***********************************************************************
     * searchTransactions - searches for TransactionModels using FTS
     *       post results to _searchResults variable
     *       if no results found post ALL transactions
     *********************************************************************/
    fun searchTransactions(query:String)
    {
        viewModelScope.launch {
            val cleanQuery = sanitizeQuery(query)
            if (cleanQuery.lowercase().contains("buy"))
            {
                postBuyTransactions()
            }
            if(cleanQuery.lowercase().contains("sell"))
            {
                postSellTransactions()
            }
            performFTS(cleanQuery)
        }
    }

    /*******************************************************************
     *sanitizeQuery - surrounds string with double qoutes
     *              escapes double double qoutes
     *******************************************************************/
    private fun sanitizeQuery(query: String) :String
    {
        val queryWithEscapedQuotes = query.replace(Regex.fromLiteral("\""), "\"\"")
        return "*\"$queryWithEscapedQuotes\"*"
    }


    /****************************************************************
    * Posts Transactions of type Sell (0) to _searchResults variable
    *****************************************************************/
    private suspend fun  postSellTransactions()
    {
        _triggerNoResultsToast.postValue(false)
        repository.getSellTransactions().collect(){
            _searchResults.postValue(it)
        }
    }
    /****************************************************************
     * Posts Transactions of type Sell (0) to _searchResults variable
     *****************************************************************/
    private suspend fun  postBuyTransactions()
    {
        _triggerNoResultsToast.postValue(false)
        repository.getBuyTransactions().collect{
            _searchResults.postValue(it)
        }
    }


    /******************************************************************
     * performFTS - Posts FTS results to _searchResults variable
     *              if FTS results == empty posts ALL Transactions
     * @param cleanQuery : query string
     *******************************************************************/
    private suspend fun performFTS(cleanQuery:String)
    {
        _triggerNoResultsToast.postValue(false)
        repository.searchTransactions(cleanQuery).collect{ searchList ->
            if(searchList.isEmpty()) {
                _triggerNoResultsToast.postValue(true)
                repository.readAllTransactiondata().collect {
                    _searchResults.postValue(it)
                }
            }
            else
                _searchResults.postValue(searchList)
        }
    }
}