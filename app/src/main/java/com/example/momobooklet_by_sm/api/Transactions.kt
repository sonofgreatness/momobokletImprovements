package com.example.momobooklet_by_sm.api

import com.example.momobooklet_by_sm.database.local.models.TransactionModel
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * This class will produce a list : List<TransactionModel>
 **/
class Transactions (
    @SerializedName("transactionId")
    private var TransactionId: String? = null,
    private var Date: String? = null,
    private var customerName: String? = null,
    private var customerPin: String? = null,
    private var customerPhone: String? = null,
    private var transactionType: String? = null,
    private var amount: String? = null,
    private var signature: String? = null,
    private var time: String? = null,
    private var agentPhoneNumber: String? = null)
{
    private fun convertTransactionId(TransactionId: String?):String
    {
        return TransactionId?.trim() ?: UUID.randomUUID().toString()
    }

    private fun convertDate():String
    {
        return Date ?: "dd-mm-yyyy"
    }

    private fun convertCustomerName():String
    {
        return this.customerName ?: "NO_CUSTOMER_NAME"
    }

    private fun convertCustomerPhone():String
    {
        return customerPhone?: "NO_CUSTOMER_PHONE"
    }
    private fun convertCustomerPIN():String
    {
        return customerPin?: "NO_CUSTOMER_PIN"
    }
    private fun convertTransactionType(transactionType: String?): Boolean
    {
        return transactionType?.contains("true", true) ?: false
    }
    private fun convertAmount(amount: String?):Float
    {
       // return amount?.toFloat() ?:0F
        return 100.9F
    }
    private fun convertTime(): String
    {
        return time ?:"NO_TIME"
    }
    private fun convertAgentPhoneNumber(): String
    {
        return agentPhoneNumber?:"NO_AGENT_NUMBER"
    }

    fun makeTransactionModel(): TransactionModel
    {
        return TransactionModel(this.convertTransactionId(TransactionId),
                                this.convertDate(),
                                this.convertCustomerName(),
                                this.convertCustomerPIN(),
                                this.convertCustomerPhone(),
                                this.convertTransactionType(transactionType),
                                this.convertAmount(amount),
                                byteArrayOf(0),
                                convertTime(),
                                convertAgentPhoneNumber())
    }


    override fun toString(): String {
        return "Transaction   ".plus(this.TransactionId)
            .plus(this.agentPhoneNumber).plus(this.amount)
    }


}
