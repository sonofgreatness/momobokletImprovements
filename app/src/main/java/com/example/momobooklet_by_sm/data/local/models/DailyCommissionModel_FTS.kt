package com.example.momobooklet_by_sm.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "DailyCommission_FTS")
@Fts4(contentEntity = DailyCommissionModel::class)
data class DailyCommissionModel_FTS (
    @ColumnInfo(name = "Date")
    val Date :String,
    @ColumnInfo(name = "Number_of_Transactions")
    val Number_of_Transactions:Int,
    @ColumnInfo(name = "Commission_Amount")
    val  Commission_Amount: Double,
)
