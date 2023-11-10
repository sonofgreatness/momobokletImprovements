package com.free.momobooklet_by_sm.domain.repositories
import android.app.Application

interface CommissionDatesManagerRepository {
    fun getTriMonthDates(startDate: String):List<String>

    fun getMonthDates_(startDate: String): List<String>
    fun getMonthDate_Normal(startDate: String): List<String>

    fun getMonthDate_Abnormal(startDate: String): List<String>
    fun getWeekDates_(startDate: String): List<String>

    fun getWeekDate_Normal(startDate: String): List<String>
    fun getWeekDate_Abnormal(startDate: String, diff: Int,lastDyOfThisMonthInt: Int): List<String>

    fun getBiWeeklyDates_(startDate: String):List<String>
    fun getBiWeekDate_Normal(startDate: String): List<String>

    fun getBiWeekDate_Abnormal(startDate: String, diff: Int,lastDyOfThisMonthInt: Int): List<String>
    fun getFirstDayNextMonth(Date: String): String

    fun getLastMonthDates_(): MutableList<String>
    fun getThisMonthDates_(): MutableList<String>

    fun generateThatMonthDates(activity: Application) : MutableList<String>
    fun generateLastMonthDates(activity: Application) : MutableList<String>
    fun getThatOtherMonthStartDate (activity: Application) : String

    fun getLastMonthStartDate():String
    fun getLastMonthStartDate(activity: Application): String

    fun getMonthStartDate(activity: Application): String
    fun getThatOtherMonthEndDateString(): String

    fun getLastMonthEndDateString(): String
    fun getLastMonthIntValue(): Int

    fun getYesterdayStringUltimate(): String
    fun getyesterdayString(yesterdayInt: Int): String
    fun subtractYearFromDate(currentDate: String): String

    fun getyesterdayInt(dayInt: Int): Int
    fun setYesterdayInt(): Int

    fun generateTodayDate() : String
    fun getNextDay(today  :String): String

    fun convertDatePickerIntToMyDate(dayOfMonth: Int, month: Int, year: Int): String
    fun convertIntTodateString(tobeConverted  :Int): String
    fun makeNonLeapYearDict(): Map<String, String>

    fun makeLeapYearDict(): Map<String, String>
    fun checkifDateIsBeforeToday(dayOfMonth: Int, month: Int, year: Int): Boolean
}