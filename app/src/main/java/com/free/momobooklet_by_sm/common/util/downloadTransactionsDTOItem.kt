package com.free.momobooklet_by_sm.common.util


import com.google.gson.annotations.SerializedName

data class downloadTransactionsDTOItem(
    @SerializedName("agentPhoneNumber")
    val agentPhoneNumber: Int,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("customerName")
    val customerName: String,
    @SerializedName("customerPhone")
    val customerPhone: String,
    @SerializedName("customerPin")
    val customerPin: Long,
    @SerializedName("date")
    val date: String,
    @SerializedName("signature")
    val signature: String,
    @SerializedName("time")
    val time: String,
    @SerializedName("transactionId")
    val transactionId: String,
    @SerializedName("transactionType")
    val transactionType: Boolean
)