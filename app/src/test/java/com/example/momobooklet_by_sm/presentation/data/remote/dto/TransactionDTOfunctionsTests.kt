package com.example.momobooklet_by_sm.presentation.data.remote.dto

import com.free.momobooklet_by_sm.common.util.Constants
import org.junit.Assert
import org.junit.Test
import java.text.SimpleDateFormat

class TransactionDTOfunctionsTests {


    @Test
    fun `Test makeDateStringFromISO8601String` (){

        val testString = "2023-05-27T22:00:00.000Z"
        val testDateString = makeDateStringFromISO8601String(testString)
        Assert.assertEquals(testDateString, "27-05-2023")
    }

   private  fun makeDateStringFromISO8601String(testString: String): String? {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = formatter.parse(testString)
        val finalformaterr = SimpleDateFormat(Constants.DATE_DATE_PATTERN)
        val testDateString = finalformaterr.format(currentDate)
        return testDateString
    }

    @Test
    fun `Test makeTimeStringFromISO8601String`(){
        val testString = "2023-05-27T22:00:00.000Z"
        val testTimeString = makeTimeStringFromISO8601String(testString)
        Assert.assertEquals(testTimeString, "27-05-2023 10:00:00 PM")

    }

    private fun makeTimeStringFromISO8601String(testString: String): String?
    {

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val currentDate = formatter.parse(testString)
        val finalformaterr = SimpleDateFormat(Constants.TIME_DATE_PATTERN)
        val testDateString = finalformaterr.format(currentDate)
        return testDateString
    }

}