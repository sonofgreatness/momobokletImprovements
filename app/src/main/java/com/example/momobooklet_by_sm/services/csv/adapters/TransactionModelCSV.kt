package com.example.momobooklet_by_sm.services.csv.adapters

import androidx.room.ColumnInfo
import com.example.momobooklet_by_sm.database.local.models.TransactionModel
import com.example.momobooklet_by_sm.services.csv.Exportable
import com.opencsv.bean.CsvBindByName
import java.sql.Time

data class TransactionModelCSV (
        @CsvBindByName(column = "Transaction_ID")
        val Transaction_ID: String,
        @CsvBindByName(column = "Date")
        val Date: String,
        @CsvBindByName(column = "C_Name")
        val C_Name:String,
        @CsvBindByName(column="C_ID")
        val C_ID: String,

        @CsvBindByName(column = "C_PHONE")
        val C_PHONE:String,

        @CsvBindByName(column = "Transaction_type")
        val Transaction_type: Boolean,

        @CsvBindByName(column = "Amount")
        val Amount :Float,

        @CsvBindByName(column = "Time")
        val Time :String,

        ):Exportable
fun List<TransactionModel>.toCsv() : List<TransactionModelCSV> = map {
        TransactionModelCSV(
                Transaction_ID = it.Transaction_ID,
                Date = it.Date.toString(),
                C_Name = it.C_Name,
                C_ID = it.C_ID,
                C_PHONE = it.C_PHONE,
                Transaction_type = it.Transaction_type,
                Amount = it.Amount,
                Time = it.Time.toString(),
                )
}