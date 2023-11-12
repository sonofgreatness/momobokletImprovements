package com.example.momobooklet_by_sm.common.util

import android.util.Log
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.data.local.models.TransactionModel
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileWriter

class UtilityTest {

    fun createBackUpTransactionsFile(sourceList : List<TransactionModel>) : File
    {

        println(" Write Json to File Called")
        val converters = Gson()
        val jsonObject =converters.toJson(sourceList)
        val returnFile = File(Constants.BACKUP_TRANSACTIONS_FILENAME)

        println(" Json Object  -->".plus(jsonObject))
        println(" Json Object to String -->".plus(jsonObject.toString()))
        try{

        val writer =  FileWriter(returnFile)
            writer.write(jsonObject)
            writer.close()

        }
        catch (ex: Exception)
        {
            println("Failed to Write Json to File")
            Log.d("Failed to Write Json to File", "${ex.message}")
        }
        println("file -->".plus(returnFile.absolutePath))
        return  returnFile
    }


    @Test
    fun transactionModel_toJSonConversion() {

        val   transactionModel = TransactionModel("kkj",
            "Date: String",
            "C_Name: String",
            "C_ID: String",
            "C_PHONE: String",
            false,
            10.7F,
            ByteArray(100),
            "Time: String",
            "AgentPhoneNumber: String")


        val   transactionModel2 = TransactionModel("kkj2",
            "Date: String",
            "C_Name: String",
            "C_ID: String",
            "C_PHONE: String",
            true,
            10.7F,
            ByteArray(100),
            "Time: String",
            "AgentPhoneNumber: String")

        val mylist = listOf<TransactionModel>(transactionModel,transactionModel2)
        val converter = Gson()
        val json = converter.toJson(mylist)


        createBackUpTransactionsFile(mylist)
        Assert.assertEquals(4, 2 + 2)
    }




}