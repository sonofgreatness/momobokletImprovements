package com.free.momobooklet_by_sm.data.dto

import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.data.local.models.TransactionModel
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

/**
 * This class will produce a list : List<TransactionModel>
 **/
class Transactions (
    @SerializedName("transactionId")
    private var TransactionId: String? = null,
    @SerializedName("date")
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


         return    makeDateStringFromISO8601String(Date!!)!!.trim()

    }
    private  fun makeDateStringFromISO8601String(testString: String): String? {

        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val currentDate = formatter.parse(testString)
            val finalformaterr = SimpleDateFormat("dd-MM-yyyy")
            val testDateString = finalformaterr.format(currentDate)
            testDateString
        }catch (ex: Exception) {
            ""
        }

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
        return amount?.toFloat() ?:0F
    }
    private fun convertTime(): String
    {
        return makeTimeStringFromISO8601String(Date!!)!!.trim()
    }
    private fun makeTimeStringFromISO8601String(testString: String): String? {


        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val currentDate = formatter.parse(testString)
            val finalformaterr = SimpleDateFormat(Constants.TIME_DATE_PATTERN)
            finalformaterr.format(currentDate)
        }catch (ex: Exception) {
            ""
        }


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
            0,
                                byteArrayOf(0),
                                convertTime(),
                                convertAgentPhoneNumber())
    }


    override fun toString(): String {
        return "Transaction   ".plus(this.TransactionId)
            .plus(this.agentPhoneNumber).plus(this.amount)
    }
}
