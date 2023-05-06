package com.example.momobooklet_by_sm.data.local.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
@Parcelize
@Entity(tableName = "BACKUP_METADATA")
data class
BACKUP_METADATA(
                @PrimaryKey
                val TransactionId : String,
                val Previous_size: Int = -1,):Parcelable