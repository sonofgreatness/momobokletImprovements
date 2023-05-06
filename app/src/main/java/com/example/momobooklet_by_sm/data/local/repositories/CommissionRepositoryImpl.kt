package com.example.momobooklet_by_sm.data.local.repositories

import com.example.momobooklet_by_sm.data.local.daos.CommissionDao
import com.example.momobooklet_by_sm.data.local.models.DailyCommissionModel
import com.example.momobooklet_by_sm.data.local.models.PeriodicCommissionModel
import com.example.momobooklet_by_sm.domain.repositories.CommissionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class CommissionRepositoryImpl  @Inject constructor
 (private val CommissionDao:CommissionDao):CommissionRepository
{
 override suspend fun getCommissionChart () = CommissionDao.getCommissionChart()

 override suspend fun  addDayCommission(DailyModel:DailyCommissionModel)
                                            = CommissionDao.addDayCommission(DailyModel)

 override fun getDaysCommission(Date: String): Flow<DailyCommissionModel?> {
        return CommissionDao.getDaysCommission(Date)
 }


 override suspend fun  addMonthlyCommission (MonthlyCommission: PeriodicCommissionModel)
                                       = CommissionDao.addMonthlyCommission(MonthlyCommission)

 override fun getLastMonthCommission(startDate: String, endDate: String) :Flow<PeriodicCommissionModel?>
 {
  return CommissionDao.getLastMonthCommission(startDate,endDate)
 }

 override suspend fun  getDailyCommissionModel(Date: String) = CommissionDao.getDailyCommissionModel(Date)


}

