package com.example.momobooklet_by_sm.domain.repositories

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.momobooklet_by_sm.data.local.daos.CommissionDao
import com.example.momobooklet_by_sm.data.local.models.CommissionModel
import com.example.momobooklet_by_sm.data.local.models.DailyCommissionModel
import com.example.momobooklet_by_sm.data.local.models.PeriodicCommissionModel
import kotlinx.coroutines.flow.Flow

interface CommissionRepository {


    suspend fun getCommissionChart ():List<CommissionModel>
    suspend fun addDayCommission (commission: DailyCommissionModel)
    fun  getDaysCommission(Date: String) :Flow<DailyCommissionModel?>
    suspend fun  getDailyCommissionModel(Date: String):DailyCommissionModel?
    suspend fun addMonthlyCommission (MonthlyCommission: PeriodicCommissionModel)
    fun getLastMonthCommission(startDate: String, endDate: String) :Flow<PeriodicCommissionModel?>

}
