package com.example.momobooklet_by_sm.database.local.repositories

import com.example.momobooklet_by_sm.database.local.daos.CommissionDao
import com.example.momobooklet_by_sm.database.local.models.DailyCommissionModel
import kotlinx.coroutines.flow.Flow

class CommissionRepository (private val CommissionDao:CommissionDao)
{
 suspend fun getCommissionChart () = CommissionDao.getCommissionChart()

 suspend fun  addDayCommission(DailyModel:DailyCommissionModel)
                                            = CommissionDao.addDayCommission(DailyModel)

  fun  getDaysCommission(Date:String): Flow<Double>
  {

          return CommissionDao.getCommissionAmount(Date)
  }

 suspend fun  getDaysCommissionModel(Date: String) = CommissionDao.getDailyCommissionModel(Date)
 }
