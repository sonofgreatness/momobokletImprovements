package com.example.momobooklet_by_sm

import org.junit.Test
import org.junit.Assert.*
import java.text.SimpleDateFormat


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun stringToInt() {
        val string = "01"
        val stringInt = string.toInt()
        assertEquals(1, stringInt)
    }

    @Test
    fun convertStringTodate(){
        val testString = "2023-05-27T22:00:00.000Z"
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = formatter.parse(testString)
        val finalformaterr = SimpleDateFormat("dd-MM-yyyy")
         val testDateString =  finalformaterr.format(currentDate)
        assertEquals(testDateString, "27-05-2023")
    }

}

