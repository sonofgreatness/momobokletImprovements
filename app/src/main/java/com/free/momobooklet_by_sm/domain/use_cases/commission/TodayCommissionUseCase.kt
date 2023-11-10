package com.free.momobooklet_by_sm.domain.use_cases.commission

import com.free.momobooklet_by_sm.data.local.models.DailyCommissionModel
import com.free.momobooklet_by_sm.domain.repositories.CommissionDatesManagerRepository
import com.free.momobooklet_by_sm.domain.repositories.CommissionRepository
import com.free.momobooklet_by_sm.domain.repositories.TransactionRepository
import com.free.momobooklet_by_sm.domain.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class TodayCommissionUseCase
@Inject constructor (val userRepository: UserRepository,
                     val transactionRepository: TransactionRepository,
                     val commissionRepository: CommissionRepository,
                     val datesManagerRepository: CommissionDatesManagerRepository)
{

    /***********************************************************************
     * gets The current user in control,
     *      gets all  todays transactions for user in control,
     *      calculates commission using above transactions
     *      IF DailyCommissionModel-> Today Does not exist
     *                     add it in  database
     *      IF  DailyCommissionModel-> Today Exists
     *                       update  it in database
     **********************************************************************/

    operator  fun invoke(): Flow<DailyCommissionModel> = flow{

        userRepository.readActiveuser().collect{
            try{
                val mainUser = it[0]
                val momoNumber = mainUser.MoMoNumber.trim()
                val date = datesManagerRepository.generateTodayDate().trim()
                val commissionChart = commissionRepository.getCommissionChart()

                try{
                    transactionRepository.getTodaysTransactions(date, momoNumber)
                         .collect{listOfTransactions ->
                             Timber.d("Selected List of Transactions -> ${listOfTransactions.size}")
                             commissionChart.let {commissionChart->
                                 var sum = 0.0

                                 for (commissionModel in commissionChart) {
                                     for (transaction in listOfTransactions) {
                                         val amount_condition: Boolean =
                                             (transaction.Amount >= commissionModel.Min && transaction.Amount <= commissionModel.Max)
                                         val typeCondition: Boolean =
                                             (transaction.Transaction_type == commissionModel.Type)
                                         if (amount_condition && typeCondition) {
                                             Timber.d("Update Commission Called, Amount -> : ${commissionModel.Commission_Amount}")
                                             sum = sum.plus(commissionModel.Commission_Amount)
                                         }
                                     }
                                 }
                                 val  new_dailyCommission = DailyCommissionModel(
                                    date,
                                    listOfTransactions.size,
                                    sum,momoNumber)

                                 commissionRepository.getDailyCommissionModel(date,momoNumber).let{dailyCommission->
                                    try {
                                        if (dailyCommission!=null)
                                        {
                                            commissionRepository.updateDailyCommission(new_dailyCommission)
                                        }
                                        else
                                        {
                                            commissionRepository.addDayCommission(new_dailyCommission)
                                        }
                                    }
                                    catch (ex : Exception)
                                    {
                                        Timber.d("get Daily Commission Model failed -> ${ex.message}")
                                    }
                                    finally {
                                        emit(new_dailyCommission)
                                    }
                                 }

                             }
                         }
                }
                catch (ex:Exception)
                {
                    Timber.d("get transactions failed -> ${ex.message}")
                }


            }catch (ex:Exception)
            {
                Timber.d("get active user failed -> ${ex.message}")
            }
        }
    }
}