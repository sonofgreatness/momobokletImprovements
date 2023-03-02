package com.example.momobooklet_by_sm.database.local.repositories

import com.example.momobooklet_by_sm.database.local.daos.CommissionDao
import com.example.momobooklet_by_sm.database.local.models.DailyCommissionModel
import com.example.momobooklet_by_sm.database.local.models.PeriodicCommissionModel
import kotlinx.coroutines.flow.Flow


class CommissionRepository (private val CommissionDao:CommissionDao)
{
 suspend fun getCommissionChart () = CommissionDao.getCommissionChart()

 suspend fun  addDayCommission(DailyModel:DailyCommissionModel)
                                            = CommissionDao.addDayCommission(DailyModel)

  fun  getDaysCommission(Date: String): Flow<DailyCommissionModel?>
  {

          return CommissionDao.getCommissionAmount(Date)
  }
 suspend fun  addMonthlyCommission (MonthlyCommission: PeriodicCommissionModel)
                                       = CommissionDao.addMonthlyCommission(MonthlyCommission)

 fun getLastMonthCommission(startDate: String, endDate: String) :Flow<PeriodicCommissionModel?>
 {
  return CommissionDao.getLastMonthCommission(startDate,endDate)
 }

 suspend fun  getDailyCommissionModel(Date: String) = CommissionDao.getDailyCommissionModel(Date)


}

