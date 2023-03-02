package com.example.momobooklet_by_sm.database.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.momobooklet_by_sm.database.local.models.CommissionModel
import com.example.momobooklet_by_sm.database.local.models.DailyCommissionModel
import com.example.momobooklet_by_sm.database.local.models.PeriodicCommissionModel
import kotlinx.coroutines.flow.Flow


@Dao
interface CommissionDao {
    @Query("SELECT *  FROM Commission_Chart")
    suspend fun getCommissionChart() : List<CommissionModel>

    @Insert
        (onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDayCommission (commission: DailyCommissionModel)
    /**************************************************************
    * Retrieves The Commission Amount
    *   For A  particular Day (Date)
    ***************************************************************/
    @Query("SELECT * FROM `Daily Commission` WHERE Date =:Date")
    fun  getCommissionAmount(Date: String) :Flow<DailyCommissionModel?>

    /*****************************************************
    * Retrieves a CommissionModel corresponding
    *by using it's primaryKey (Date)
    * @return  : Returns null if record matches the key
                    else returns CommissionModel i.e record
    *******************************************************/
    @Query("SELECT * FROM `Daily Commission`  WHERE Date =:Date")
    suspend fun  getDailyCommissionModel(Date: String):DailyCommissionModel?

    @Insert
        (onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMonthlyCommission (MonthlyCommission: PeriodicCommissionModel)

    @Query("SELECT * FROM `Monthly Commission` WHERE startDate =:startDate  AND  endDate =:endDate")
    fun getLastMonthCommission(startDate: String, endDate: String) :Flow<PeriodicCommissionModel?>

}