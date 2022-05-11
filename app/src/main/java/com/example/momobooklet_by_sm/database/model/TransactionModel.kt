package com.example.momobooklet_by_sm.database.model


import android.os.Parcelable
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import kotlinx.android.parcel.Parcelize

@Parcelize


@Entity(tableName = "RECORDS_SHEET",
    foreignKeys = arrayOf(
        ForeignKey(entity = UserModel::class,
            parentColumns = arrayOf("MoMoNumber"),
            childColumns = arrayOf("AgentPhoneNumber"),
            onDelete = CASCADE)
    )
    )

data class TransactionModel(
    @PrimaryKey(autoGenerate = true)
    val Transaction_ID: Int,
    val Date: String,
    val C_Name:String,
    val C_ID: String,
    val C_PHONE:String,
    val Transaction_type: String,
    val Amount :Float,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB,name="Signature")
    val Signature:ByteArray,
    @ColumnInfo(name = "AgentPhoneNumber", index = true)
    val AgentPhoneNumber: String,
//so you can update database.
): Parcelable





