package com.free.momobooklet_by_sm.data.dto.backup


import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class BackUpDetailsDtoItem(
    @SerializedName("backup_id")
    private val backupId: String,
    @SerializedName("timestamp")
  private   val timestamp: Long
) {

    fun getBackupId():String
    {
        return backupId
    }
    fun getTimestampInHumanReadableForm(): String {
        return convertTimestampToDateString(timestamp = timestamp)
    }

  private  fun convertTimestampToDateString(timestamp: Long, ): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val date = Date(timestamp)
        return dateFormat.format(date)
    }
}