package com.free.momobooklet_by_sm.domain.repositories

import com.free.momobooklet_by_sm.data.local.models.CommissionModel
import com.free.momobooklet_by_sm.data.local.models.DailyCommissionModel
import com.free.momobooklet_by_sm.data.local.models.PeriodicCommissionModel
import kotlinx.coroutines.flow.Flow

interface CommissionRepository {


    suspend fun getCommissionChart ():List<CommissionModel>
    suspend fun addDayCommission (commission: DailyCommissionModel)
    fun  getDaysCommission(Date: String, momoNumber:String) :Flow<DailyCommissionModel?>
    suspend fun  getDailyCommissionModel(Date: String, momoNumber:String):DailyCommissionModel?
    suspend fun addMonthlyCommission (MonthlyCommission: PeriodicCommissionModel)
    fun getLastMonthCommission(startDate: String, endDate: String,momoNumber:String) :Flow<PeriodicCommissionModel?>
    suspend fun updateDailyCommission(dailyCommissionModel: DailyCommissionModel)
    suspend fun updatePeriodicCommissionModel(periodicCommissionModel: PeriodicCommissionModel)
    suspend fun getPeriodicCommissionModel(startDate: String, endDate: String,momoNumber: String) :PeriodicCommissionModel?

}
