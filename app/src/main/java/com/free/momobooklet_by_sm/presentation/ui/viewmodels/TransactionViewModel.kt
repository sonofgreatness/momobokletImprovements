package com.free.momobooklet_by_sm.presentation.ui.viewmodels

import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.ExportState
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.Resource
import com.free.momobooklet_by_sm.data.dto.transaction.TransactionRequest
import com.free.momobooklet_by_sm.data.local.models.TransactionModel
import com.free.momobooklet_by_sm.domain.repositories.CommissionDatesManagerRepository
import com.free.momobooklet_by_sm.domain.repositories.TransactionRepository
import com.free.momobooklet_by_sm.domain.use_cases.manage_transactions.DownloadTransactionsUseCase
import com.free.momobooklet_by_sm.domain.use_cases.manage_transactions.UploadTransactionsUseCase
import com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.ExportService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class TransactionViewModel @Inject constructor (
    val transactionRepository: TransactionRepository,
    val datesManager: CommissionDatesManagerRepository,
  private  val UploadTransactionsUseCase: UploadTransactionsUseCase,
    private val downloadTransactionsUseCase: DownloadTransactionsUseCase
) : ViewModel() {

    // state for export csv status
    private val _exportCsvState = MutableStateFlow<ExportState>(ExportState.Empty)
    val exportCsvState: StateFlow<ExportState> = _exportCsvState

    private val exportService: ExportService = ExportService

    val _searchResults = MutableLiveData<List<TransactionModel>>()
    val searchResults: LiveData<List<TransactionModel>>
        get() = _searchResults



    val _triggerNoResultsToast = MutableLiveData<Boolean>()
    val triggerNoResultsToast : LiveData<Boolean> get() = _triggerNoResultsToast



    init {
        _triggerNoResultsToast.postValue(false)


        viewModelScope.launch {

            transactionRepository.readAllTransactiondata().collect{
                _searchResults.postValue(it)
            }
            Timber.e("t_viewM->${transactionRepository.readAllTransactiondata()}")
        }
    }

    fun uploadTransaction(request: TransactionRequest, activity: Activity)
    {
        viewModelScope.launch {
            UploadTransactionsUseCase(request).collect {
                when (it) {
                    is Resource.Loading -> {
                        Toast.makeText(
                            activity.applicationContext,
                            "remote upload  begun",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        Timber.d("LoadUploadTransactViewM ===>  LOADING")
                    }
                    is Resource.Success -> {

                        Toast.makeText(activity.applicationContext, "Transaction Added", Toast.LENGTH_SHORT)
                            .show()
                        Timber.d("SuccessTransactUploadViewM  ===>  Successful Added Transaction :${it.message}")
                    }
                    is Resource.Error -> {

                        Timber.d("ErrorUploadViewM ===>  Error Transaction upload because ${it.message} username ==> ${request.username}")
                    }
                }
            }
        }
    }
    fun downloadTransaction(username: String, activity: Activity)
    {
        viewModelScope.launch {
            downloadTransactionsUseCase(username).collect {
                when (it) {
                    is Resource.Loading -> {
                        Toast.makeText(
                            activity.applicationContext,
                            "remote download  begun",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        Timber.d("LoadDownloadTransactViewM ===>  LOADING")
                    }
                    is Resource.Success -> {

                        Toast.makeText(activity.applicationContext, "Transaction Downloaded", Toast.LENGTH_SHORT)
                            .show()
                        Timber.d("SuccessTransactUploadViewM  ===>  Successful Downloaded Transaction :${it.message}")
                    }
                    is Resource.Error -> {

                        Timber.d("ErrorUploadViewM ===>  Error Download upload because ${it.message} username ==> $username")
                    }
                }
            }
        }
    }



    fun getAllTransaction() = viewModelScope.launch {
        transactionRepository.readAllTransactiondata().collect(){
            _searchResults.postValue(it)
        }
    }

    fun addTransaction(transaction: TransactionModel) {
        viewModelScope.launch(IO) {
            transactionRepository.addTransaction(transaction)
            Timber.e("add_transaction called in viewmodel")
        }

    }
    fun removeTransaction(transaction: TransactionModel) {
        viewModelScope.launch(IO) {
            transactionRepository.removeTransaction(transaction)
        }
    }
    fun deleteAll() {
        viewModelScope.launch(IO) {
            transactionRepository.deleteAll()
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
        transactionRepository.getSellTransactions().collect(){
            _searchResults.postValue(it)
        }
    }
    /****************************************************************
     * Posts Transactions of type Sell (0) to _searchResults variable
     *****************************************************************/
    private suspend fun  postBuyTransactions()
    {
        _triggerNoResultsToast.postValue(false)
        transactionRepository.getBuyTransactions().collect{
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
        transactionRepository.searchTransactions(cleanQuery).collect{ searchList ->
            if(searchList.isEmpty()) {
                _triggerNoResultsToast.postValue(true)
                transactionRepository.readAllTransactiondata().collect {
                    _searchResults.postValue(it)
                }
            }
            else
                _searchResults.postValue(searchList)
        }
    }

}