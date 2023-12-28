package com.free.momobooklet_by_sm.data.dto.transaction


import com.google.gson.annotations.SerializedName

data class UploadTransactionsDtoItem(
    @SerializedName("AgentPhoneNumber")
    val agentPhoneNumber: String,
    @SerializedName("Amount")
    val amount: Double,
    @SerializedName("C_ID")
    val cID: String,
    @SerializedName("C_Name")
    val cName: String,
    @SerializedName("C_PHONE")
    val cPHONE: String,
    @SerializedName("Date")
    val date: String,
    @SerializedName("Signature")
    val signature: List<Int>,
    @SerializedName("Time")
    val time: String,
    @SerializedName("timestamp")
    val timestamp: Long,
    @SerializedName("Transaction_ID")
    val transactionID: String,
    @SerializedName("Transaction_type")
    val transactionType: Boolean
){
    fun createTransactionRequest():TransactionRequest
    {
        return  TransactionRequest(
            transactionId = transactionID,
            transactionType =  transactionType,
            timestamp =  timestamp,
            time =  time,
            customerId = cID,
            customerName = cName,
            customerPhone =  cPHONE,
            amount = amount.toFloat(),
            signature = signature as ArrayList<Int>,
            username = agentPhoneNumber
        )
    }
}