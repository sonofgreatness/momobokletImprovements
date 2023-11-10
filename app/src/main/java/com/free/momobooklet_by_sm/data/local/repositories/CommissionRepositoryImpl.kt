package com.free.momobooklet_by_sm.data.local.repositories

import com.free.momobooklet_by_sm.data.local.daos.CommissionDao
import com.free.momobooklet_by_sm.data.local.models.DailyCommissionModel
import com.free.momobooklet_by_sm.data.local.models.PeriodicCommissionModel
import com.free.momobooklet_by_sm.domain.repositories.CommissionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class CommissionRepositoryImpl  @Inject constructor
 (private val CommissionDao:CommissionDao):CommissionRepository {
 override suspend fun getCommissionChart() = CommissionDao.getCommissionChart()

 override suspend fun addDayCommission(commission: DailyCommissionModel) =
  CommissionDao.addDayCommission(commission)

 override fun getDaysCommission(Date: String, momoNumber: String): Flow<DailyCommissionModel?> {
  return CommissionDao.getDaysCommission(Date, momoNumber)
 }


 override suspend fun addMonthlyCommission(MonthlyCommission: PeriodicCommissionModel) =
  CommissionDao.addMonthlyCommission(MonthlyCommission)

 override fun getLastMonthCommission(
  startDate: String,
  endDate: String,
  momoNumber: String
 ): Flow<PeriodicCommissionModel?> {
  return CommissionDao.getLastMonthCommission(startDate, endDate, momoNumber)
 }

 override suspend fun getPeriodicCommissionModel(
  startDate: String,
  endDate: String,
  momoNumber: String
 ): PeriodicCommissionModel?  = CommissionDao.getPeriodicCommissionModel(startDate,endDate,momoNumber)

 override suspend fun getDailyCommissionModel(Date: String, momoNumber: String) =
  CommissionDao.getDailyCommissionModel(Date, momoNumber)


 override suspend fun updateDailyCommission(dailyCommissionModel: DailyCommissionModel) =
  CommissionDao.updateDailyCommission(dailyCommissionModel)

 override suspend fun updatePeriodicCommissionModel(periodicCommissionModel: PeriodicCommissionModel) =
  CommissionDao.updatePeriodicCommissionModel(periodicCommissionModel)

}