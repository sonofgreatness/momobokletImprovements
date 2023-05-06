package com.example.momobooklet_by_sm.domain.services.csv.adapters

import com.example.momobooklet_by_sm.data.local.models.DailyCommissionModel
import com.example.momobooklet_by_sm.domain.services.csv.Exportable
import com.opencsv.bean.CsvBindByName

data class DailyCommissionModelCSV (
    @CsvBindByName(column = "Date")
    val Date :String,
    @CsvBindByName(column = "Number_of_Transactions")
    val Number_of_Transactions:Int,
    @CsvBindByName(column = "Commission_Amount")
    val  Commission_Amount: Double,
        ): Exportable
fun  List<DailyCommissionModel>.dailyCommissiontoCsv() : List<DailyCommissionModelCSV> =map{
    DailyCommissionModelCSV(Date = it.Date,
                            Number_of_Transactions = it.Number_of_Transactions,
                            Commission_Amount = it.Commission_Amount,
                            )
}
