package com.example.momobooklet_by_sm.database.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4


@Entity(tableName = "RECORD_SHEET_FTS")
@Fts4(contentEntity = TransactionModel::class)

data class TransactionModel_FTS
    (
    @ColumnInfo(name ="Transaction_ID")
    val Transaction_ID: String,

    @ColumnInfo(name ="Date")
    val Date: String,
    @ColumnInfo(name ="C_Name")
    val C_Name:String,
    @ColumnInfo(name ="C_ID")
    val C_ID: String,
    @ColumnInfo(name ="C_PHONE")
    val C_PHONE:String,
    @ColumnInfo(name ="Transaction_type")
    val Transaction_type: String,
    @ColumnInfo(name ="Amount")
    val Amount :String,
    @ColumnInfo(name ="Signature")
    val Signature:String,
    @ColumnInfo(name ="Time")
    val Time :String,
    @ColumnInfo(name ="AgentPhoneNumber")
    val AgentPhoneNumber: String,
)
