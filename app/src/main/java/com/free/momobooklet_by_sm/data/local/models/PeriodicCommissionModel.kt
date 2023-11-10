package com.free.momobooklet_by_sm.data.local.models

import androidx.room.Entity

@Entity(tableName = "Monthly Commission", primaryKeys = ["Row_Id", "MoMoNumber"])
data class PeriodicCommissionModel(
    val Row_Id: Int = 0,
    val startDate :String,
    val endDate: String,
    val Number_of_Transactions:Int,
    val  Commission_Amount: Double,
    val MoMoNumber:String,
)
