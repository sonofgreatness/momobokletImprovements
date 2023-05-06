package com.example.momobooklet_by_sm.data.local.repositories

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.momobooklet_by_sm.common.util.Constants.Companion.MONTHLY_STARTDATE_FILENAME
import com.example.momobooklet_by_sm.domain.repositories.CommissionDatesManagerRepository
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.StrictMath.abs
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class CommisionDatesManagerImpl  @Inject constructor (val activity: Application):CommissionDatesManagerRepository{
    private val myDictionary: Map<String, String>
    private val yesterdayInt: Int

    init {
        val localDate: LocalDate = LocalDate.now()
        val isLeapYear: Boolean = localDate.isLeapYear

        myDictionary = if (isLeapYear)
            makeLeapYearDict()
        else
            makeNonLeapYearDict()
        yesterdayInt = setYesterdayInt()
    }

    override fun getTriMonthDates(startDate: String):List<String>
    {
        val listOfDateStrings: MutableList<String> = ArrayList()
        val firstMonthDates = getMonthDates_(startDate)
        val size = firstMonthDates.size

        val secondMonthStart = getNextDay(getMonthDates_(startDate)[size - 1])
        val secondMonthDates = getMonthDates_(secondMonthStart)

        val thirdMonthStart = getNextDay(secondMonthDates[size - 1])
        val thirdMonthDates =getMonthDates_(thirdMonthStart)

         return (firstMonthDates + secondMonthDates + thirdMonthDates)

    }
    /*************************************************************************
     * getMonthDates_ -> gets the 30 chronological date strings starting
     *                  from a selected date
     *@param startDate ,String the date from which to start
     * @return Returns  the dates as List<String>
     ****************************************************************************/
    override fun getMonthDates_(startDate: String): List<String>
    {

        val startDateDayString = startDate.substring(0, 2)
        val startDateDay = startDateDayString.toInt()
        val currentMonth = startDate.substring(3, 5)

        val endOfMonthDayString: String = myDictionary[currentMonth].toString()
        val endOfMonthDay: Int = endOfMonthDayString.toInt()


        if ((startDateDay + 29) <= endOfMonthDay)
            return getMonthDate_Normal(startDate)
        else
        {
            return getMonthDate_Abnormal(startDate)
        }
    }


    /******************************************************************
     * getMonthDate_Normal -> gets list of dates, 30 chronological
     *                              days from  a startdate , all the
     *                              days fall in the same month
     *@param startDate , String , the date-String from which to begin
     *                              counting
     *@return Returns -> List<String>,  a list of dates
     *****************************************************************/
    override fun getMonthDate_Normal(startDate: String): List<String> {

        val listOfDateStrings: MutableList<String> = ArrayList()

        val startDateInt = startDate.substring(0,2).toInt()
        val endDateInt = startDateInt  + 29
        for (i in startDateInt..endDateInt)
        {
            val newAddition = convertIntTodateString(i).plus(startDate.substring(2).trim())
            listOfDateStrings.add(newAddition.trim())
        }
        return  listOfDateStrings
    }


    /**************************************************************
     *   * getMonthDate_Abnormal -> gets list of dates, 30 chronological
     *                              days from  a start Date  , days
     *                              fall between 2  or 3 consecutive months
     *@param startDate , String , the date-String from which to begin
     *                              counting
     *@return Returns -> List<String>,  a list of dates
     ***************************************************************/
    override fun getMonthDate_Abnormal(startDate: String): List<String> {

        val listOfDateStrings: MutableList<String> = ArrayList()

        val startDateDayString = startDate.substring(0, 2)
        val startDateDay = startDateDayString.toInt()
        val currentMonth = startDate.substring(3, 5)

        val endOfMonthDayString: String = myDictionary[currentMonth].toString()
        val endOfMonthDay: Int = endOfMonthDayString.toInt()

        val month2StartDate = getFirstDayNextMonth(startDate)
        val month2EndDayString:String = myDictionary[month2StartDate.substring(3,5)].toString()
        val month2EndDateDay = month2EndDayString.toInt()
        val month3StartDate = getFirstDayNextMonth(month2StartDate)

        val useful = startDateDay + 29 - endOfMonthDay
        if(useful <=  month2EndDateDay)// get this whole month + days in previous month
        {
            for(i in startDateDay..endOfMonthDay)
            {
                val newAddition = convertIntTodateString(i).plus(startDate.substring(2).trim())
                listOfDateStrings.add(newAddition.trim())
            }

            for (i in 1 ..abs(useful))// second month
            {
                val newAddition = convertIntTodateString(i).plus(month2StartDate.substring(2).trim())
                listOfDateStrings.add(newAddition.trim())
            }
        }
        else
        {

            for(i in startDateDay..endOfMonthDay)
            {
                val newAddition = convertIntTodateString(i).plus(startDate.substring(2).trim())
                listOfDateStrings.add(newAddition.trim())
            }
            for (i in 1 ..month2EndDateDay)// second month
            {
                val newAddition = convertIntTodateString(i).plus(month2StartDate.substring(2).trim())
                listOfDateStrings.add(newAddition.trim())
            }
            for (i in 1..abs(useful-month2EndDateDay))//
            {
                val newAddition = convertIntTodateString(i).plus(month3StartDate.substring(2).trim())
                listOfDateStrings.add(newAddition.trim())
            }
        }
    return  listOfDateStrings
    }

    /*******************************************************************
     * getWeekDates_  -> gets the dates for a week starting
     *                  from a supplied date
     *@param startDate ->   the date to start calculaing 7 days from
     *@return : Returns a List<String>   , list of dates
     ****************************************************************/
     override fun getWeekDates_(startDate: String): List<String> {

        val startDateDayString = startDate.substring(0, 2)
        val startDateDay = startDateDayString.toInt()
        val currentMonth = startDate.substring(3, 5)
        val endOfMonthDayString: String = myDictionary[currentMonth].toString()
        val endOfMonthDay: Int = endOfMonthDayString.toInt()

        if ((startDateDay + 6) <= endOfMonthDay)
            return getWeekDate_Normal(startDate)
        else {
            val diff = endOfMonthDay - (startDateDay + 6)
            return getWeekDate_Abnormal(startDate, kotlin.math.abs(diff),endOfMonthDay)
        }
    }

    /*******************************************************************
     * getWeekDate_Normal ->generates list of chronological
     *                      days that make a  week and all
     *                      fall in the same month
     *@param startDate , String : date from which the week start counting
     *@return List<String> : Returns a list of dates , size = 7
     ***********************************************************************/
    override fun getWeekDate_Normal(startDate: String): List<String> {
        val listOfDateStrings: MutableList<String> = ArrayList()

        val startDateInt = startDate.substring(0,2).toInt()
        val endDateInt = startDateInt  + 6
        for (i in startDateInt..endDateInt)
        {
            val newAddition = convertIntTodateString(i).plus(startDate.substring(2).trim())
            listOfDateStrings.add(newAddition.trim())
        }
        return  listOfDateStrings
    }


    /*******************************************************************
     * getWeekDate_AbNormal ->generates list of chronological
     *                      days that make a  week ,where not
     *                       all days fall in the same month
     *@param startDate , String : date from which the week start counting
     *@return List<String> : Returns a list of dates , size = 7
     ***********************************************************************/
    override fun getWeekDate_Abnormal(startDate: String, diff: Int,lastDyOfThisMonthInt: Int): List<String> {
        val listOfDateStrings: MutableList<String> = ArrayList()
        val firstDayNextMonth = getFirstDayNextMonth(startDate)
        val startDateInt = startDate.substring(0,2).toInt()
        //get this month dates
        for (i in startDateInt..lastDyOfThisMonthInt)
        {
            val newAddition = convertIntTodateString(i).plus(startDate.substring(2).trim())
            listOfDateStrings.add(newAddition.trim())
        }
        //get next month dates
        for (i in 1..diff)
        {

            val newAddition = convertIntTodateString(i).plus(firstDayNextMonth.substring(2).trim())
            listOfDateStrings.add(newAddition.trim())
        }

        return listOfDateStrings
    }

    /*******************************************************************
     * getWeekDates_  -> gets the dates for two weeks starting
     *                  from a supplied date
     *@param startDate ->   the date to start calculating 14 days from
     *@return : Returns a List<String>   , list of dates
     ****************************************************************/
     override fun getBiWeeklyDates_(startDate: String):List<String>
    {
        val startDateDayString = startDate.substring(0, 2)
        val startDateDay = startDateDayString.toInt()
        val currentMonth = startDate.substring(3, 5)
        val endOfMonthDayString: String = myDictionary[currentMonth].toString()
        val endOfMonthDay: Int = endOfMonthDayString.toInt()

        if ((startDateDay + 13) <= endOfMonthDay)
            return getBiWeekDate_Normal(startDate)
        else {
            val diff = endOfMonthDay - (startDateDay + 13)
            return getBiWeekDate_Abnormal(startDate, kotlin.math.abs(diff),endOfMonthDay)
        }
    }



    /*******************************************************************
     * getBiWeekDate_Normal ->generates list of chronological
     *                      days that make two weeks and all
     *                      fall in the same month
     *@param startDate , String : date from which the week start counting
     *@return List<String> : Returns a list of dates , size = 14
     ***********************************************************************/
    override fun getBiWeekDate_Normal(startDate: String): List<String> {
        val listOfDateStrings: MutableList<String> = ArrayList()

        val startDateInt = startDate.substring(0,2).toInt()
        val endDateInt = startDateInt  + 13
        for (i in startDateInt..endDateInt)
        {
            val newAddition = convertIntTodateString(i).plus(startDate.substring(2).trim())
            listOfDateStrings.add(newAddition.trim())
        }
        return  listOfDateStrings
    }





    /*******************************************************************
     * getBiWeekDate_AbNormal ->generates list of chronological
     *                      days that make 2 weeks ,where not
     *                       all days fall in the same month
     *@param startDate , String : date from which the week start counting
     *@return List<String> : Returns a list of dates , size = 14
     ***********************************************************************/
    override fun getBiWeekDate_Abnormal(startDate: String, diff: Int,lastDyOfThisMonthInt: Int): List<String> {

        val listOfDateStrings: MutableList<String> = ArrayList()
        val firstDayNextMonth = getFirstDayNextMonth(startDate)
        val startDateInt = startDate.substring(0, 2).toInt()
        //get this month dates
        for (i in startDateInt..lastDyOfThisMonthInt) {
            val newAddition = convertIntTodateString(i).plus(startDate.substring(2).trim())
            listOfDateStrings.add(newAddition.trim())
        }
        //get next month dates
        for (i in 1..diff) {

            val newAddition = convertIntTodateString(i).plus(firstDayNextMonth.substring(2).trim())
            listOfDateStrings.add(newAddition.trim())
        }

        return listOfDateStrings
    }

    /*****************************************************************
     * getFirstDayNextMonth - gets the string value of the first day
     *                      of the nextmonth
     *@param Date, String ->  The date to startFrom
     * @return : Returns a string
     *********************************************************/

      override fun getFirstDayNextMonth(Date: String): String {
        val currenthMonthString = Date.substring(3,5)
        val currenthMonthInt = currenthMonthString.toInt()
        val currenthYearString = Date.substring(6,10)
        val currenthYearInt = currenthYearString.toInt()
        val nexMonthInt= currenthMonthInt + 1

        if(nexMonthInt <= 12) //same Year
        {
            return "01".plus("-")
                        .plus(convertIntTodateString(nexMonthInt))
                            .plus("-").plus(currenthYearString)
        }
        else // next year
        {
            return "01".plus("-")
                         .plus("01").plus("-")
                .plus((currenthYearInt + 1).toString())
        }


    }

    /*********************************************************************
     * getLastMonthDates_s -> gets  list  of dates to  use when
     *                      calculating commission,
     *
     * @return : returns list of dates
     *********************************************************************/
    override  fun getLastMonthDates_(): MutableList<String> {
        val currentDate = generateTodayDate().substring(0, 2).toInt()
        val startDate = getLastMonthStartDate(activity).substring(0, 2).toInt()
        return if (currentDate > startDate)
            generateLastMonthDates(activity)
        else
            generateThatMonthDates(activity)
    }


    /*********************************************************************
     * getThisMonthDates_s -> gets  list  of dates to  use when
     *                      calculating commission,
     * @return : returns list of dates
     ***********************************************************************/
     override  fun getThisMonthDates_(): MutableList<String> {
        val listOfDateStrings: MutableList<String> = ArrayList()
        val currentDate = generateTodayDate()
        val lastMonthStartDate = getLastMonthStartDate(activity)
        val lastMonthEndDate = getLastMonthEndDateString()
        val lastMonthInt = lastMonthEndDate.substring(0,2).toInt()

        val startString = getThatOtherMonthStartDate(activity)
        val startInt = startString.substring(0, 2).toInt()
        val endInt = currentDate.substring(0, 2).toInt()



        if (endInt > startInt) {
            for (i in startInt until endInt) {
                val newAddition = convertIntTodateString(i).plus(currentDate.substring(2).trim())
                listOfDateStrings.add(newAddition.trim())
            }
            return listOfDateStrings
        } else{// Add last month Dates to List
            for(i in startInt ..lastMonthInt)
            {
                val newAddition = convertIntTodateString(i).plus(lastMonthStartDate.substring(2).trim())
                listOfDateStrings.add(newAddition.trim())
            }
            // add this month dates
            for (i in 1 until endInt) {
                val newAddition = convertIntTodateString(i).plus(currentDate.substring(2).trim())
                listOfDateStrings.add(newAddition.trim())
            }
            return  listOfDateStrings
        }
    }

    /***************************************************************************
     *generateThatMonthDates  - gets dates to use when calculating month
     *                          before last  month' commisssion
     *  @param activity -> Application scope is necessary to read File
     *                              File  is read to get start date
     * @return  : Returns list of dates (MutableList<String> of form dd-MM-YYYY)
     ***********************************************************************/
  override fun generateThatMonthDates(activity: Application) : MutableList<String>
    {
        val listOfDateStrings: MutableList<String> = ArrayList()
        val currentDate = getLastMonthStartDate(activity)
        val startString =  getThatOtherMonthStartDate(activity)
        val endString  = getThatOtherMonthEndDateString()
        val startInt = startString.substring(0,2).toInt()
        val endInt =endString.substring(0,2).toInt()
        for ( i in startInt..endInt)
        {
            val newAddition = convertIntTodateString(i).plus(startString.substring(2).trim())
            listOfDateStrings.add(newAddition.trim())
        }
        for (i in 1 until startInt)
        {
            val newAddition = convertIntTodateString(i).plus(currentDate.substring(2).trim())
            listOfDateStrings.add(newAddition.trim())

        }
        return listOfDateStrings
    }

    /***************************************************************************
     *generateLastMonthDates  - gets dates to use when calculating last month's
     *                          commisssion
     *  @param activity -> Application scope is necessary to read File
     *                              File  is read to get start date
     * @return  : Returns list of dates (MutableList<String> of form dd-MM-YYYY)
     ***********************************************************************/
  override fun generateLastMonthDates(activity: Application) : MutableList<String>
    {
        val listOfDateStrings: MutableList<String> = ArrayList()
        val currentDate = generateTodayDate()
        val startString =  getLastMonthStartDate(activity)
        val endString  = getLastMonthEndDateString()
        val startInt = startString.substring(0,2).toInt()
        val endInt =endString.substring(0,2).toInt()
        for ( i in startInt..endInt)
        {
            val newAddition = convertIntTodateString(i).plus(startString.substring(2).trim())
            listOfDateStrings.add(newAddition.trim())
        }
        for (i in 1 until startInt)
        {
            val newAddition = convertIntTodateString(i).plus(currentDate.substring(2).trim())
            listOfDateStrings.add(newAddition.trim())

        }
        return listOfDateStrings
    }
    /********************************************************************************
     * getThatOtherMonthStartDate - Get String value of date
     *                                  on which month commission begins counting
     *  reads  File (ManageMonthlyStartDate.txt) to get Monthly start date in
     *          integer form , converts it to dd-MM-YYYY string
     * @return date string
     ***********************************************************************************/

   override fun getThatOtherMonthStartDate (activity: Application) : String
    {

        val stringBuilder: StringBuilder = StringBuilder()
        val currentYear = generateTodayDate().substring(6,10)
        val exitingYear = currentYear.toInt() - 1
        val exitingYearString = exitingYear.toString()
        val lastMonthInt = getLastMonthIntValue() - 1 // move two months back


        var text: String? = null
        val fileInputStream: FileInputStream? = activity.openFileInput(MONTHLY_STARTDATE_FILENAME)

        val inputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader = BufferedReader(inputStreamReader)

        while ({ text = bufferedReader.readLine(); text }() != null) {
            stringBuilder.append(text)
        }
        val dayInt = stringBuilder.toString().toInt()
        return if (lastMonthInt == 0)
            convertIntTodateString(dayInt).plus("-").plus("12").plus("-")
                .plus(exitingYearString)
        else
            return  convertIntTodateString(dayInt).plus("-").plus(convertIntTodateString(lastMonthInt))
                .plus("-").plus(currentYear)
    }

    /*************************************************************************
     * reads File (ManageMonthlyStartDate.txt) to get Monthly start date in
     *              integer form, converts it to  dd-MM-YYYY string
     ***************************************************************************/
     override fun getLastMonthStartDate():String
    {
        return getLastMonthStartDate(activity)
    }
    /*************************************************************************
     * reads File (ManageMonthlyStartDate.txt) to get Monthly start date in
     *              integer form, converts it to  dd-MM-YYYY string
     ***************************************************************************/
    override fun getLastMonthStartDate(activity: Application): String {
        val fileInputStream: FileInputStream?
        val stringBuilder: StringBuilder = StringBuilder()
        val currentYear = generateTodayDate().substring(6,10)
        val exitingYear = currentYear.toInt() - 1
        val exitingYearString = exitingYear.toString()
        val lastMonthInt = getLastMonthIntValue()


        var text: String? = null
        fileInputStream = activity.openFileInput(MONTHLY_STARTDATE_FILENAME)

            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

        while ({ text = bufferedReader.readLine(); text }() != null) {
            stringBuilder.append(text)
        }
        val dayInt = stringBuilder.toString().toInt()
        return if (lastMonthInt == 0)
            convertIntTodateString(dayInt).plus("-").plus("12").plus("-")
                .plus(exitingYearString)
        else
          return  convertIntTodateString(dayInt).plus("-").plus(convertIntTodateString(lastMonthInt))
                .plus("-").plus(currentYear)
    }

    /*************************************************************************
     * reads File (ManageMonthlyStartDate.txt) to get Monthly start date in
     *              integer form, converts it to  dd-MM-YYYY string
     ***************************************************************************/
    override fun getMonthStartDate(activity: Application): String {

        val fileInputStream: FileInputStream?
        val stringBuilder: StringBuilder = StringBuilder()
        val currentYear = generateTodayDate().substring(6,10)
        val exitingYear = currentYear.toInt() - 1
        val exitingYearString = exitingYear.toString()
        val lastMonthInt = getLastMonthIntValue()


        var text: String? = null
        fileInputStream = activity.openFileInput(MONTHLY_STARTDATE_FILENAME)

        val inputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader = BufferedReader(inputStreamReader)

        while ({ text = bufferedReader.readLine(); text }() != null) {
            stringBuilder.append(text)
        }
        val dayInt = stringBuilder.toString().toInt()
        return if (lastMonthInt == 0)
            convertIntTodateString(dayInt).plus("-").plus("12").plus("-")
                .plus(exitingYearString)
        else
            return  convertIntTodateString(dayInt).plus("-").plus(convertIntTodateString(lastMonthInt))
                .plus("-").plus(currentYear)
    }


    /**************************************************
     *gets the previous - 1 month's last day in the form
     *          dd-MM-YY
     **************************************************/
  override   fun getThatOtherMonthEndDateString(): String{
        val currentYear = generateTodayDate().substring(6,10)
        val lastMonthInt = getLastMonthIntValue() - 1 // move two months back
        val fullEndDateString: String
        fullEndDateString = if (lastMonthInt!=0)
            myDictionary[convertIntTodateString(lastMonthInt)].toString()
                .plus("-").plus(convertIntTodateString(lastMonthInt))
                .plus("-").plus(currentYear)
        else
            subtractYearFromDate(generateTodayDate())
        return fullEndDateString
    }

    /**************************************************
     *gets the previous months last day in the form
     *          dd-MM-YY
     **************************************************/
  override  fun getLastMonthEndDateString(): String{
        val currentYear = generateTodayDate().substring(6,10)
        val lastMonthInt = getLastMonthIntValue()
        val fullEndDateString: String
        fullEndDateString = if (lastMonthInt!=0)
            myDictionary[convertIntTodateString(lastMonthInt)].toString()
                .plus("-").plus(convertIntTodateString(lastMonthInt))
                .plus("-").plus(currentYear)
        else
            subtractYearFromDate(generateTodayDate())
        return fullEndDateString
    }

    /*********************************************************
     * gets The integer value of previous month
     *         1 - jan , 2 - Feb  ..... 0 means This is a new year
     *
     *******************************************************/
   override fun getLastMonthIntValue(): Int{
            val currentDate = generateTodayDate()
        val currentMonth = currentDate.substring(3,5)
        val currentMonthInt = currentMonth.toInt()
        return currentMonthInt - 1
        }



    /*******************************************************
     * returns yesterday's date as a string
     *
     *******************************************************/
     override fun getYesterdayStringUltimate(): String
    {
        return getyesterdayString(yesterdayInt)

    }
    /*******************************************************************
     *       generates yesterday's date
     *                                      and returns string value
     *  @param yesterdayInt:-> yesterday day portion of date as int
     *                            = 0 if today is first day of new month
     ********************************************************************/
   override fun getyesterdayString(yesterdayInt: Int): String {

        val currentDate = generateTodayDate()
        val currentYear = currentDate.substring(6,10)
        val currentMonth = currentDate.substring(3,5)
        val currentMonthInt = currentMonth.toInt()
        val lastMonthString  = convertIntTodateString(currentMonthInt -1)

        return when(yesterdayInt){

            0 -> {
                //get this months Month String
                if (currentMonth == "01")
                {
                    return subtractYearFromDate(currentDate)
                }
                else{
                    return myDictionary[lastMonthString].plus("-").plus(lastMonthString).plus("-").plus(currentYear)}
            }
            else -> {
                convertIntTodateString(yesterdayInt).plus("-").plus(currentMonth).plus("-").plus(currentYear)
            }
        }
    }


   override fun subtractYearFromDate(currentDate: String): String {
        val currentYear = currentDate.substring(6, 10).toInt()
        val exitingYear = (currentYear - 1).toString()
        return "31".plus("-12-").plus(exitingYear)
    }


    /*************************************************************************
     *returns 0 on first day of the month
     *    otherwise returns Integer value of yesterdays' day portion of date
     *              string
     ************************************************************************/
    override fun getyesterdayInt(dayInt: Int): Int {
        if (dayInt > 0)
            return dayInt - 1
        return 0
    }

    override  fun setYesterdayInt(): Int {
        val currentDate = generateTodayDate()
        return currentDate.substring(0,2).toInt() - 1
    }


    @SuppressLint("SimpleDateFormat")
  override  fun generateTodayDate() : String
    {
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = sdf.format(Date())
        return currentDate.trim()
    }

    /************************************************
     * getNextDay -> gets the String value of the day
     *                  after a given date (Day)
     *@param  today , String a date in the format
     *                  dd-MM-YYYY
     *@return Returns a date in string form
     *                  format dd-MM-YYYY
     **************************************************/
   override fun getNextDay(today  :String): String {
        val todayDayString = today.substring(0, 2)
        val monthString = today.substring(3, 5)
        val yearString = today.substring(6, 10)
        val todayMonthEndDay: String = myDictionary[monthString].toString()

        if (todayDayString == todayMonthEndDay) //might need a new year , definitely need a new month
        {
            var monthValue = monthString.toInt()
            var yearValue = yearString.toInt()
            monthValue += 1
            return if (monthValue > 12)
                "01".plus("-")
                    .plus("01").plus("-")
                    .plus((yearValue + 1).toString())
            else
                "01".plus("-")
                    .plus(convertIntTodateString(monthValue)).plus("-")
                    .plus(yearString)
        }
        else
        return convertIntTodateString(todayDayString.toInt() + 1).plus("-")
            .plus(monthString).plus("-")
            .plus(yearString)
    }


    /***************************************************************************
     *convertDatePickerIntToMyDate -> converts  date from
     *                             Date Dialogue Picker to dd-MM-YYYY compliant string
     *@return : Returns  date string
     *********************************************************************/
    override  fun convertDatePickerIntToMyDate(dayOfMonth: Int, month: Int, year: Int): String
    {
        val day = convertIntTodateString(dayOfMonth)
        val month_ = convertIntTodateString(month + 1)
        val year_ = convertIntTodateString(year)

        return day.plus("-").plus(month_).plus("-").plus(year_)
    }
    /************************************************
     * Turns  int to string or the format [0-9]{2}
     *
     **************************************************/
    override fun convertIntTodateString(tobeConverted  :Int): String {

        if(tobeConverted < 10)
        {
            val placeholderZero = "0"
            return placeholderZero.plus(tobeConverted)
        }
        return tobeConverted.toString()
    }

    /***************************************************************
     * generate dictionary for  non leap year
     *
     ****************************************************************/
    override  fun makeNonLeapYearDict(): Map<String, String> {
        return mapOf(
            "01" to "31", "02" to "28", "03" to "30", "04" to "31",
            "05" to "31", "06" to "30", "07" to "31", "08" to "31",
            "09" to "30", "10" to "31", "11" to "30", "12" to "31")
    }
    /**************************************************************************
     *generate dictionary for leap year
     *
     **************************************************************************/
    override  fun makeLeapYearDict(): Map<String, String> {
        return mapOf(
            "01" to "31", "02" to "29", "03" to "30", "04" to "31",
            "05" to "31", "06" to "30", "07" to "31", "08" to "31",
            "09" to "30", "10" to "31", "11" to "30", "12" to "31"
        )
    }


    /***********************************************************
     * checkifDateIsBeforeToday()
     * @return : returns true if date is  before today
     **************************************************************/
    override fun checkifDateIsBeforeToday(dayOfMonth: Int, month: Int, year: Int): Boolean
    {

        val c: Calendar = Calendar.getInstance()
        val mYear: Int = c.get(Calendar.YEAR) // current year
        val mMonth: Int = c.get(Calendar.MONTH) // current month
        val mDay: Int = c.get(Calendar.DAY_OF_MONTH) // current day

        var returnBool = false
        if (year <= mYear)
        {
            when(year){
                mYear->{
                    if (month <= mMonth)
                    {
                        when (month)
                        {
                            mMonth->{
                                if (dayOfMonth <= mDay)
                                     return true
                            }
                            else ->{
                               return true
                            }
                        }
                    }
                }
                else->{
                    return true
                }
            }
        }

        return returnBool
    }
}