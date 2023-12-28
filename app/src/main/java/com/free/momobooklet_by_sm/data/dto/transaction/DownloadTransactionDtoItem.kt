package com.free.momobooklet_by_sm.data.dto.transaction


import com.free.momobooklet_by_sm.data.local.models.TransactionModel
import com.google.gson.annotations.SerializedName
import timber.log.Timber
import java.sql.Timestamp
import java.util.ArrayList

data class DownloadTransactionDtoItem(
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("customerId")
    val customerId: String,
    @SerializedName("customerName")
    val customerName: String,
    @SerializedName("customerPhone")
    val customerPhone: String,
    @SerializedName("signature")
    val signature: List<Int>?,
    @SerializedName("time")
    val time: String?,
    @SerializedName("timestamp")
    val timestamp: Timestamp,
    @SerializedName("transactionId")
    val transactionId: String,
    @SerializedName("transactionType")
    val transactionType: Boolean,
    @SerializedName("username")
    val username: String
){

    fun createTransaction(): TransactionModel{
        var mySignature  : ByteArray
        if(signature != null) {
            mySignature = ByteArray(signature.size)

            for ((i, byte) in signature.withIndex()) {
                Timber.d("iterating through size =>${signature.size}    : => $i")
                mySignature[i] = byte.toByte()
            }
        }
        else
        {
            Timber.d("iterating through size 2222 =>")
            mySignature = ByteArray(1)
        }

        return TransactionModel(
            Transaction_ID = transactionId,
            Transaction_type = transactionType,
            Time = time.toString(),
            AgentPhoneNumber = username,
            C_Name = customerName,
            C_ID= customerId,
            C_PHONE = customerPhone,
            Date =  time?.substring(0,10)?.trim().toString(),
            Amount = amount.toFloat(),
           Signature = mySignature,
            timestamp =  timestamp.time

        )

    }
}