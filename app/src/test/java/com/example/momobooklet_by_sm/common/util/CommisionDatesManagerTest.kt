package com.example.momobooklet_by_sm.common.util

import android.app.Activity
import org.junit.Assert.assertEquals

import org.junit.Test

class CommisionDatesManagerTest
{
 private val datesManager = CommisionDatesManager(Activity())
    @Test
    fun getyesterdayInt_Test()
    {
        val zer0 =datesManager.getyesterdayInt(0)
        val One = datesManager.getyesterdayInt(1)
        val random = datesManager.getyesterdayInt(31)

        assertEquals(0,zer0)
        assertEquals(0,One)
        assertEquals(30,random)
    }


   /* @Test
    fun getyesterdayString_Test(){
        val NoContest =datesManager.getyesterdayString(10)
        val yeContest = datesManager.getyesterdayString(9)
        val yesContest = datesManager.getyesterdayString(31)
        val dayOne = datesManager.getyesterdayString(0)

        assertEquals("10-02-2023",NoContest)
        assertEquals("09-02-2023",yeContest)
        assertEquals(yesContest,"31-02-2023")
        assertEquals("31-01-2023",dayOne)

    }
    */
    @Test
    fun makeNonLeapYearDict_Test() {
        val dictionary = datesManager.makeNonLeapYearDict()
        assertEquals("31",dictionary["01"])
        assertEquals("28",dictionary["02"])
    }
    @Test
    fun makeLeapYearDict_Test() {
        val dictionary = datesManager.makeLeapYearDict()
        val currentDate = datesManager.generateTodayDate()

        assertEquals("2023",currentDate.substring(6,10))
        assertEquals(2022,currentDate.substring(6,10).toInt() -1)
        assertEquals("02",currentDate.substring(3,5))
        assertEquals("31",dictionary["01"])
        assertEquals("29",dictionary["02"])
    }

    @Test
    fun subtractYearFromDate()
    {
        assertEquals(datesManager.subtractYearFromDate("01-01-1993"), "31-12-1992")
    }


    @Test
    fun getNextDay_Test()
    {
        assertEquals("01-03-2023",datesManager.getNextDay("28-02-2023"))
        assertEquals("01-01-2023",datesManager.getNextDay("31-12-2022"))
        assertEquals("02-03-2023",datesManager.getNextDay("01-03-2023"))
    }

    @Test
    fun getYesterdayStringUltimate_Test()
    {
        assertEquals("20-02-2023",datesManager.getYesterdayStringUltimate())
    }

    @Test
    fun getLastMonthEndDateString_Test()
    {
        val currentdate = datesManager.generateTodayDate()
        val myNum = "10"
        val newU  = myNum.plus(currentdate.substring(2).trim())
        assertEquals(newU, "10-02-2023")
    }



    @Test()
    fun checkifDateIsBeforeToday_Test()
    {
        //checkYear
        assertEquals(true, datesManager.checkifDateIsBeforeToday(1,1,2023))
        assertEquals(true, datesManager.checkifDateIsBeforeToday(1,2,2020))
        assertEquals(false, datesManager.checkifDateIsBeforeToday(1,1,2028))
       //checkMonth
        assertEquals(true, datesManager.checkifDateIsBeforeToday(1,1,2023))
        assertEquals(true, datesManager.checkifDateIsBeforeToday(1,0,2023))
        assertEquals(false, datesManager.checkifDateIsBeforeToday(1,3,2023))
        assertEquals(true, datesManager.checkifDateIsBeforeToday(1,4,2022))
        assertEquals(true, datesManager.checkifDateIsBeforeToday(1,1,2022))
        //checkDay
        assertEquals(true, datesManager.checkifDateIsBeforeToday(1,1,2023))
        assertEquals(true, datesManager.checkifDateIsBeforeToday(21,0,2023))
        assertEquals(false, datesManager.checkifDateIsBeforeToday(21,0,2028))
    }

    @Test
    fun convertDatePickerIntToMyDate_Test()
    {
        assertEquals("02-02-2023", datesManager.convertDatePickerIntToMyDate(2,1,2023))
    }

    @Test
    fun getFirstDayNextMonth_Test(){
        val janNextYear = datesManager.getFirstDayNextMonth("01-12-2023")
        val decNow = datesManager.getFirstDayNextMonth("01-11-2023")

        assertEquals("01-01-2024",janNextYear)
        assertEquals("01-12-2023",decNow)
    }

    @Test
    fun getWeekDates_Test()
    {
      val myTestList = listOf("01-01-2023","02-01-2023","03-01-2023"
                                       ,"04-01-2023","05-01-2023","06-01-2023"
                                       ,"07-01-2023")

      val myOverlappingList = listOf("29-01-2023","30-01-2023","31-01-2023"
                                             ,"01-02-2023","02-02-2023","03-02-2023"
                                             ,"04-02-2023")

       val myAnnualOverlapList = listOf("29-12-2023","30-12-2023","31-12-2023"
           ,"01-01-2024","02-01-2024","03-01-2024"
           ,"04-01-2024")

        assertEquals(datesManager.getWeekDates_("01-01-2023") , myTestList)
        assertEquals(datesManager.getWeekDates_("29-01-2023"),myOverlappingList)
        assertEquals(datesManager.getWeekDates_("01-01-2023").size, 7)
        assertEquals(datesManager.getWeekDates_("29-12-2023"),myAnnualOverlapList)
    }


    @Test
    fun getBiWeeklyDates_Test()
    {
        val myTestList = listOf("01-01-2023","02-01-2023","03-01-2023"
                                ,"04-01-2023","05-01-2023","06-01-2023"
                                ,"07-01-2023","08-01-2023","09-01-2023"
                                ,"10-01-2023","11-01-2023", "12-01-2023","13-01-2023"
                                ,"14-01-2023")

        val myOverlappinglist = listOf("29-01-2023","30-01-2023","31-01-2023"
            ,"01-02-2023","02-02-2023","03-02-2023"
            ,"04-02-2023","05-02-2023","06-02-2023"
            ,"07-02-2023","08-02-2023", "09-02-2023","10-02-2023"
            ,"11-02-2023")

        val myAnnualOverlapList = listOf("29-12-2023","30-12-2023","31-12-2023"
            ,"01-01-2024","02-01-2024","03-01-2024"
            ,"04-01-2024","05-01-2024","06-01-2024"
            ,"07-01-2024","08-01-2024","09-01-2024"
            ,"10-01-2024","11-01-2024")

        assertEquals(datesManager.getBiWeeklyDates_("01-01-2023"), myTestList)
        assertEquals(datesManager.getBiWeeklyDates_("29-01-2023"), myOverlappinglist)
        assertEquals(datesManager.getBiWeeklyDates_("29-12-2023"),myAnnualOverlapList)

    }

    @Test
    fun getMonthDates_()
    {

        val myAnnualOverlapList = listOf("31-12-2023", "01-01-2024", "02-01-2024", "03-01-2024",
            "04-01-2024", "05-01-2024", "06-01-2024", "07-01-2024", "08-01-2024",
            "09-01-2024", "10-01-2024", "11-01-2024", "12-01-2024", "13-01-2024", "14-01-2024",
            "15-01-2024", "16-01-2024", "17-01-2024", "18-01-2024", "19-01-2024", "20-01-2024",
            "21-01-2024", "22-01-2024", "23-01-2024", "24-01-2024", "25-01-2024", "26-01-2024",
            "27-01-2024", "28-01-2024", "29-01-2024")

        assertEquals(myAnnualOverlapList, datesManager.getMonthDates_("31-12-2023"))
        assertEquals(30,datesManager.getMonthDates_("31-12-2023").size)

    }

    @Test
    fun getTriMonthDates_Test()
    {

        val myAnnualOverlapList = listOf("31-12-2023", "01-01-2024", "02-01-2024", "03-01-2024",
            "04-01-2024", "05-01-2024", "06-01-2024", "07-01-2024", "08-01-2024",
            "09-01-2024", "10-01-2024", "11-01-2024", "12-01-2024", "13-01-2024", "14-01-2024",
            "15-01-2024", "16-01-2024", "17-01-2024", "18-01-2024", "19-01-2024", "20-01-2024",
            "21-01-2024", "22-01-2024", "23-01-2024", "24-01-2024", "25-01-2024", "26-01-2024",
            "27-01-2024", "28-01-2024", "29-01-2024")

        //assertEquals(myAnnualOverlapList, datesManager.getTriMonthDates("31-12-2023"))
        assertEquals(90,datesManager.getTriMonthDates("31-12-2023").size)

    }




}