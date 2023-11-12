package com.free.momobooklet_by_sm.data.dto


import com.google.gson.annotations.SerializedName

data class BackUpDetailsDtoItem(
    @SerializedName("backupId")
    val backupId: String,
    @SerializedName("time")
    val time: String,
    @SerializedName("user_id")
    val userId: String
)