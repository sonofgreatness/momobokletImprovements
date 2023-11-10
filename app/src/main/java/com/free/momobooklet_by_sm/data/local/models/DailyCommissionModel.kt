package com.free.momobooklet_by_sm.data.local.models

import androidx.room.Entity

@Entity(tableName = "Daily Commission", primaryKeys = ["Date", "MoMoNumber"] )
data class DailyCommissionModel(
    val Date :String,
    val Number_of_Transactions:Int,
    val  Commission_Amount: Double,
    val MoMoNumber:String,
)