package com.free.momobooklet_by_sm.domain.use_cases.commission

import com.free.momobooklet_by_sm.data.local.models.CommissionModel
import com.free.momobooklet_by_sm.data.local.models.DailyCommissionModel
import com.free.momobooklet_by_sm.data.local.models.TransactionModel
import com.free.momobooklet_by_sm.domain.repositories.CommissionRepository
import com.free.momobooklet_by_sm.domain.repositories.TransactionRepository
import com.free.momobooklet_by_sm.domain.repositories.UserRepository
import timber.log.Timber
import javax.inject.Inject

class CustomDayCommissionNonFlow  @Inject constructor (val userRepository: UserRepository,
                                                       val transactionRepository: TransactionRepository,
                                                       val commissionRepository: CommissionRepository
)
{

    /***********************************************************************
     * gets The current user in control,
     *      gets all  day's transactions for user in control,
     *      calculates commission using above transactions
     *      IF DailyCommissionModel-> Today Does not exist
     *                     add it in  database
     *      IF  DailyCommissionModel-> Today Exists
     *                       update  it in database
     **********************************************************************/
    suspend operator fun invoke(date : String) : DailyCommissionModel? {

        userRepository.getActiveUser().let{
            try {

                val mainUser = it[0]
                val momoNumber = mainUser.MoMoNumber.trim()
                val commissionChart = commissionRepository.getCommissionChart()

                try {
                    transactionRepository.getDailyTransactions(date, momoNumber)
                        .let{ listOfTransactions ->
                            Timber.d("Selected List of Transactions -> ${listOfTransactions.size}")

                          return   calculateCommission(
                                commissionChart,
                                listOfTransactions,
                                date,
                                momoNumber
                            )
                        }
                } catch (ex: Exception) {
                    Timber.d("get transactions failed -> ${ex.message}")
                    return  null
                }

            } catch (ex: Exception) {
                Timber.d("get active user failed -> ${ex.message}")
               return  null
            }
        }
    }

    private suspend fun calculateCommission(
        commissionChart: List<CommissionModel>,
        listOfTransactions: List<TransactionModel>,
        date: String,
        momoNumber: String
    ):DailyCommissionModel? {
        lateinit var new_dailyCommission : DailyCommissionModel

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
            }
             new_dailyCommission = DailyCommissionModel(
                date,
                listOfTransactions.size,
                sum, momoNumber
            )

            commissionRepository.getDailyCommissionModel(date, momoNumber)
                .let { dailyCommission ->
                    try {
                        if (dailyCommission != null) {
                            commissionRepository.updateDailyCommission(
                                new_dailyCommission
                            )
                        } else {
                            commissionRepository.addDayCommission(
                                new_dailyCommission
                            )
                        }
                    } catch (ex: Exception) {
                        Timber.d("get Daily Commission Model failed -> ${ex.message}")
                        return null
                    }

                }

        }
        return  new_dailyCommission
    }
}