package com.example.momobooklet_by_sm.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Monthly Commission")
data class PeriodicCommissionModel(
    @PrimaryKey(autoGenerate = true)
    val Row_Id: Int = 0,
    val startDate :String,
    val endDate: String,
    val Number_of_Transactions:Int,
    val  Commission_Amount: Double,
)