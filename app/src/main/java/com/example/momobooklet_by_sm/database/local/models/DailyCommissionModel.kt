package com.example.momobooklet_by_sm.database.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Daily Commission")
data class DailyCommissionModel(
    @PrimaryKey
    val Date :String,
    val Number_of_Transactions:Int,
    val  Commission_Amount: Double,
)
