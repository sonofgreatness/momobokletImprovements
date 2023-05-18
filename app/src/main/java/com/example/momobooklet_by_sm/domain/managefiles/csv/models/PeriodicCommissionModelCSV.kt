package com.example.momobooklet_by_sm.domain.managefiles.csv.models

import com.example.momobooklet_by_sm.data.local.models.PeriodicCommissionModel
import com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.Exportable
import com.opencsv.bean.CsvBindByName

data class PeriodicCommissionModelCSV(
    @CsvBindByName(column = "startDate")
    val startDate :String,
    @CsvBindByName(column = "endDate")
    val endDate: String,
    @CsvBindByName(column = "Number_of_Transactions")
    val Number_of_Transactions:Int,
    @CsvBindByName(column = "Commission_Amount")
    val  Commission_Amount: Double,
        ): Exportable
fun List<PeriodicCommissionModel>.monthlyCommissiontoCsv() : List<PeriodicCommissionModelCSV> = map{
    PeriodicCommissionModelCSV(
        startDate =  it.startDate,
        endDate = it.endDate,
        Number_of_Transactions =  it.Number_of_Transactions,
        Commission_Amount = it.Commission_Amount,
    )
}