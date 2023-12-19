package com.free.momobooklet_by_sm.data.dto.transaction

import java.sql.Timestamp

data class TransactionRequest
    (
    private  val  transactionId :String,
    private val  timestamp : Timestamp?,
    private val time : String,
    private val  customerName :String,
    private val  customerId :String,
    private val  customerPhone :String,
    private val   transactionType :Boolean,
    private val   amount :Float,
    val   username : String,
    private val   signature : ArrayList<Int>,
)
