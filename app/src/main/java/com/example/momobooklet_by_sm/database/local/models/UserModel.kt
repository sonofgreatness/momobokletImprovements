package com.example.momobooklet_by_sm.database.local.models


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName= "User_Accounts")
data class UserModel(
    // val id :Int,
    val MoMoName:String,
    @PrimaryKey
    @ColumnInfo(name = "MoMoNumber")
    val MoMoNumber:String,
    val AgentEmail:String,
    val AgentPassword:String,
    val IsIncontrol:Boolean,
    val IsRemoteRegistered:Boolean,): Parcelable





