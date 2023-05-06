package com.example.momobooklet_by_sm.data.local.models


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Commission_Chart")
data class CommissionModel(
    @PrimaryKey(autoGenerate = true)
    val Row_Id : Int,
    val Min : Double,
    val Max : Double,
    val Type : Boolean,
    val  Commission_Amount: Double,
)

