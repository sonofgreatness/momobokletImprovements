package com.free.momobooklet_by_sm.data.dto

import com.free.momobooklet_by_sm.data.local.models.TransactionModel

data class BackUpTransactionModelDTOItem(
    val AgentPhoneNumber: String,
    val Amount: Double,
    val C_ID: String,
    val C_Name: String,
    val C_PHONE: String,
    val Date: String,
    val Signature: List<Int>,
    val Time: String,
    val Transaction_ID: String,
    val Transaction_type: Boolean
) {
    fun makeTransactionModel(): TransactionModel {
        return TransactionModel(
            Transaction_ID = Transaction_ID,
            Date = Date,
            Transaction_type = Transaction_type,
            Time = Time,
            C_Name =C_Name ,
            C_ID= C_ID,
            Amount = Amount.toFloat(),
            C_PHONE =  C_PHONE,
            AgentPhoneNumber = AgentPhoneNumber,
            Signature = listToByteArray(Signature)
        )
    }
    private fun listToByteArray(list: List<Int>) : ByteArray{
        val array = ByteArray(list.size)
        for ((i, element) in list.withIndex())
        {
            array[i] = element.toByte()
        }
        return array
    }
}