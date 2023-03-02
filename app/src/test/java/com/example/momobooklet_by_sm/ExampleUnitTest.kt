package com.example.momobooklet_by_sm

import org.junit.Test

import org.junit.Assert.*

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
}
