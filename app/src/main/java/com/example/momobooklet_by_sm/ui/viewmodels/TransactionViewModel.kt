package com.example.momobooklet_by_sm.ui.viewmodels

import android.app.Activity
import android.app.Application
import androidx.lifecycle.*
import com.example.momobooklet_by_sm.database.local.Database
import com.example.momobooklet_by_sm.database.local.models.TransactionModel
import com.example.momobooklet_by_sm.database.local.repositories.TransactionRepository
import com.example.momobooklet_by_sm.services.pdf.TransactionTablePDFManager
import com.example.momobooklet_by_sm.services.ExportService
import com.example.momobooklet_by_sm.services.csv.CsvConfig
import com.example.momobooklet_by_sm.services.csv.Exports
import com.example.momobooklet_by_sm.services.csv.adapters.TransactionModelCSV
import com.example.momobooklet_by_sm.services.csv.adapters.toCsv
import com.example.momobooklet_by_sm.services.pdf.PdfConfig
import com.example.momobooklet_by_sm.util.CommisionDatesManager
import com.example.momobooklet_by_sm.util.Constants
import com.example.momobooklet_by_sm.util.classes.viewState.ExportState
import com.wwdablu.soumya.simplypdf.composers.Composer
import com.wwdablu.soumya.simplypdf.composers.properties.ImageProperties
import com.wwdablu.soumya.simplypdf.composers.properties.TextProperties
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber


class TransactionViewModel(
  val  application: Application,
val activity: Activity
) : ViewModel() {

    // state for export csv status
    private val _exportCsvState = MutableStateFlow<ExportState>(ExportState.Empty)
    val exportCsvState: StateFlow<ExportState> = _exportCsvState

    private val exportService: ExportService = ExportService

    private val datesManager:CommisionDatesManager = CommisionDatesManager(activity)

    val _searchResults = MutableLiveData<List<TransactionModel>>()
    val searchResults: LiveData<List<TransactionModel>>
        get() = _searchResults


    private val repository: TransactionRepository
    val _triggerNoResultsToast = MutableLiveData<Boolean>()
    val triggerNoResultsToast : LiveData<Boolean> get() = _triggerNoResultsToast



    init {
        val userDao = Database.getDatabase(application).getDao()
        repository = userDao.let { TransactionRepository(it) }
        _triggerNoResultsToast.postValue(false)


        viewModelScope.launch {

            repository.readAllTransactiondata().collect{
                _searchResults.postValue(it)
            }
            Timber.e("t_viewM->${repository.readAllTransactiondata()}")
        }
    }

    fun getAllTransaction() = viewModelScope.launch {
        repository.readAllTransactiondata().collect(){
            _searchResults.postValue(it)
        }
    }

    fun addTransaction(transaction: TransactionModel) {
        viewModelScope.launch(IO) {
            repository.addTransaction(transaction)
            Timber.e("add_transaction called in viewmodel")
        }

    }
    fun removeTransaction(transaction: TransactionModel) {
        viewModelScope.launch(IO) {
            repository.removeTransaction(transaction)
        }
    }
    fun deleteAll() {
        viewModelScope.launch(IO) {
            repository.deleteAll()
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