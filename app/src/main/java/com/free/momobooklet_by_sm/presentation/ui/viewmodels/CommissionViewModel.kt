package com.free.momobooklet_by_sm.presentation.ui.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.free.momobooklet_by_sm.data.local.models.CommissionModel
import com.free.momobooklet_by_sm.data.local.models.DailyCommissionModel
import com.free.momobooklet_by_sm.data.local.models.PeriodicCommissionModel
import com.free.momobooklet_by_sm.domain.repositories.CommissionDatesManagerRepository
import com.free.momobooklet_by_sm.domain.repositories.CommissionRepository
import com.free.momobooklet_by_sm.domain.use_cases.commission.*
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val datesManager: CommissionDatesManagerRepository,
    private val todayCommissionUseCase: TodayCommissionUseCase,
    private val yesterdayCommissionUseCase: YesterdayCommissionUseCase,
    private   val customDayCommissionUseCase: CustomDayCommissionUseCase,
    private  val lastMonthUseCase: LastMonthUseCase,
    private  val thisMonthUsecase: ThisMonthUsecase,
    private  val commissionRepository: CommissionRepository
    ) : ViewModel() {



    private   val _commissionChart = MutableLiveData<List<CommissionModel>>()
    val commissionChart: LiveData<List<CommissionModel>> get() = _commissionChart


    private   val _dailyCommission = MutableLiveData<DailyCommissionModel>()
    val dailyCommission: LiveData<DailyCommissionModel> get() = _dailyCommission

    private  val _yesterDayCommission = MutableLiveData<DailyCommissionModel>()
    val yesterDayCommission: LiveData<DailyCommissionModel> get() = _yesterDayCommission


    private   val _customDayCommission = MutableLiveData<DailyCommissionModel>()
    val customDayCommission: LiveData<DailyCommissionModel> get() = _customDayCommission


   private    val _monthlyCommission = MutableLiveData<PeriodicCommissionModel>()
    val monthlyCommission: LiveData<PeriodicCommissionModel> get() = _monthlyCommission


   private  val _thisMonthCommission = MutableLiveData<PeriodicCommissionModel>()
    val thisMonthCommission: LiveData<PeriodicCommissionModel> get() = _thisMonthCommission


    init {

        setTodayCommission()
        setYesterDayCommission()
        setLastMonthCommission()
        setThisMonthCommission()
        setCommissionChart()
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


    /******************************************************************
     *updates  _dailyCommission variable  using today commission useCase
     *****************************************************************/
     fun setTodayCommission(){
        viewModelScope.launch {
            todayCommissionUseCase().collect{
                _dailyCommission.postValue(it)
            }
        }
    }

    /******************************************************************
     *updates _yesterdayCommission variable  using yesterdaycommission useCase
     *****************************************************************/

    fun setYesterDayCommission() {
        viewModelScope.launch {
            yesterdayCommissionUseCase().collect{
                _yesterDayCommission.postValue(it)
            }
        }
    }

    /******************************************************************
     *updates _yesterdayCommission variable  using customDaycommission useCase
     *****************************************************************/
    fun  setCustomDayCommission(date: String)
    {
      viewModelScope.launch {
          customDayCommissionUseCase(date).collect{
              _customDayCommission.postValue(it)
          }
      }
    }

    /******************************************************************
     *updates _lastMonthCommission variable  using customDaycommission useCase
     *****************************************************************/
    fun  setLastMonthCommission()
    {
        viewModelScope.launch {
            lastMonthUseCase().collect{
                _monthlyCommission.postValue(it)
               Timber.d("lastMonthCommission  $it")
            }
        }
    }



    private fun setCommissionChart() {
        viewModelScope.launch {
            commissionRepository.getCommissionChart().let {
                _commissionChart.postValue(it)
              Timber.d("commissionChart -> $it")
            }
        }
    }

    /******************************************************************
     *updates _thisMonthCommission variable  using customDaycommission useCase
     *****************************************************************/
    fun setThisMonthCommission()
    {
        viewModelScope.launch {
            thisMonthUsecase().collect{
                _thisMonthCommission.postValue(it)
                Timber.d("thisMonthCommission-> $it")
            }
        }
    }



}