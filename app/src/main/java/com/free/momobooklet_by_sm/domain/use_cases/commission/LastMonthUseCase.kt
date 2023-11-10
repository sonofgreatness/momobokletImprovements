package com.free.momobooklet_by_sm.domain.use_cases.commission

import com.free.momobooklet_by_sm.data.local.models.PeriodicCommissionModel
import com.free.momobooklet_by_sm.domain.repositories.CommissionDatesManagerRepository
import com.free.momobooklet_by_sm.domain.repositories.CommissionRepository
import com.free.momobooklet_by_sm.domain.repositories.TransactionRepository
import com.free.momobooklet_by_sm.domain.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class LastMonthUseCase
@Inject constructor(
    val userRepository: UserRepository,
    val transactionRepository: TransactionRepository,
    val commissionRepository: CommissionRepository,
    val datesManagerRepository: CommissionDatesManagerRepository)
{


    /***********************************************************************
     * gets The current user in control,
     *      gets all  dates[] transactions for user in control,
     *      calculates commission using above transactions
     *      IF DailyCommissionModel-> Today Does not exist
     *                     add it in  database
     *      IF  DailyCommissionModel-> Today Exists
     *                       update  it in database
     **********************************************************************/


    operator fun invoke() : Flow<PeriodicCommissionModel>  = flow{

        Timber.d("LaStMonth")

        userRepository.readActiveuser().collect{
            try{
                val mainUser = it[0]
                val momoNumber = mainUser.MoMoNumber.trim()
                val dates = datesManagerRepository.getLastMonthDates_()
                val commissionChart = commissionRepository.getCommissionChart()

                try{

                    /********************************************************************
                     * go through list of dates find sum of commission amounts
                     *            and  number of transactions
                     *****************************************************************/
                      var commission_amount = 0.0
                      var number_of_transactions  = 0

                     for (date in dates) {
                         Timber.d("Create Monthly Commission  ->{$date}")
                         transactionRepository.getDailyTransactions(date, momoNumber)
                             .let{ listOfTransactions ->
                                 Timber.d("Selected List of Transactions -> ${listOfTransactions.size}")
                                 commissionChart.let { commissionChart ->
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
                                     }//commission_chart loop
                                     commission_amount += sum
                                     number_of_transactions+= listOfTransactions.size
                                 }//commission_chart
                             }//getTransaction(date)
                     }//for date in dates


                    val startDate = dates[0].trim()
                    val endDate = dates[dates.size -1].trim()
                    Timber.d("Create Monthly Commission  ->")
                    val new_PeriodicModel = PeriodicCommissionModel(
                        0,
                        startDate,
                        endDate,
                        number_of_transactions,
                        commission_amount,
                        momoNumber)


                    commissionRepository.getPeriodicCommissionModel(startDate,endDate, momoNumber).let {monthlyCommission ->
                        try {
                            if (monthlyCommission!=null)
                            {
                                commissionRepository.updatePeriodicCommissionModel(new_PeriodicModel)
                            }
                            else
                            {
                                commissionRepository.addMonthlyCommission(new_PeriodicModel)
                            }
                        }
                        catch (ex : Exception)
                        {
                            Timber.d("get Monthly Commission Model failed -> ${ex.message}")
                        }
                        finally {
                            emit(new_PeriodicModel)
                            Timber.d("Emitted LaStMonth")
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