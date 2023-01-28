package com.example.momobooklet_by_sm.util

import java.text.SimpleDateFormat
import java.util.*

class CommisionDatesManager
{

    /*********************************
    * returns today's date as a string
    ***********************************/
    fun generateTodayDate() : String
    {
        val sdf = SimpleDateFormat("dd-MM-yyyy ")
        val currentDate = sdf.format(Date())

        return currentDate
    }



    fun generateMonthStartDate(): String {
        TODO()
    }
        fun generateMonthEndDate(): String {
            TODO()
        }

}