package com.free.momobooklet_by_sm.data.local.daos

import androidx.room.*
import com.free.momobooklet_by_sm.data.local.models.CommissionModel
import com.free.momobooklet_by_sm.data.local.models.DailyCommissionModel
import com.free.momobooklet_by_sm.data.local.models.PeriodicCommissionModel
import kotlinx.coroutines.flow.Flow


@Dao
interface CommissionDao {
    @Query("SELECT *  FROM Commission_Chart")
    suspend fun getCommissionChart() : List<CommissionModel>


   @Update
    suspend fun updateDailyCommission(dailyCommissionModel: DailyCommissionModel)

    @Update
    suspend fun updatePeriodicCommissionModel(periodicCommissionModel: PeriodicCommissionModel)


    @Query("SELECT * FROM `Monthly Commission` WHERE startDate =:startDate " +
            "AND  endDate =:endDate AND MoMoNumber =:momoNumber")
    suspend  fun getPeriodicCommissionModel(startDate: String, endDate: String,momoNumber: String) :PeriodicCommissionModel?

    @Insert
        (onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDayCommission (commission: DailyCommissionModel)
    /**************************************************************
    * Retrieves The Commission Amount
    *   For A  particular Day (Date)
    ***************************************************************/
    @Query("SELECT * FROM `Daily Commission` WHERE Date =:Date AND MoMoNumber =:momoNumber")
    fun  getDaysCommission(Date: String, momoNumber: String) :Flow<DailyCommissionModel?>

    /*****************************************************
    * Retrieves a CommissionModel corresponding
    *by using it's primaryKey (Date)
    * @return  : Returns null if record matches the key
                    else returns CommissionModel i.e record
    *******************************************************/
    @Query("SELECT * FROM `Daily Commission`  WHERE Date =:Date AND MoMoNumber =:momoNumber")
    suspend fun  getDailyCommissionModel(Date: String, momoNumber: String):DailyCommissionModel?

    @Insert
        (onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMonthlyCommission (MonthlyCommission: PeriodicCommissionModel)

    @Query("SELECT * FROM `Monthly Commission` WHERE startDate =:startDate " +
            "AND  endDate =:endDate AND MoMoNumber =:momoNumber")
    fun getLastMonthCommission(startDate: String, endDate: String,momoNumber: String) :Flow<PeriodicCommissionModel?>

}