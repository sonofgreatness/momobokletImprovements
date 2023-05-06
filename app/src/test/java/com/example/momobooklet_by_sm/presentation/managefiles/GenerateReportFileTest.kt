package com.example.momobooklet_by_sm.presentation.managefiles

import com.example.momobooklet_by_sm.common.util.classes.ReportType
import junit.framework.Assert.assertEquals
import org.junit.Test

class GenerateReportFileTest
{
    private val GenerateReportFile =GenerateReportFile()

    @Test
    fun incrementOrdinalsinListofReportTypes_Test()
    {
        val myList  : MutableList<ReportType> = ArrayList()
        myList.add(ReportType.TRIMONTHLY)
        myList.add(ReportType.WEEKLY)
        GenerateReportFile.incrementOrdinalsinListofReportTypes(myList)
        assertEquals(ReportType.TRIMONTHLY_1, myList[0])
        assertEquals(ReportType.WEEKLY_1, myList[1])
        assertEquals(2, myList.size)
    }

    @Test
    fun addSisterOrdinalsToListofReportTypes_Test()
    {
        val myList  : MutableList<ReportType> = ArrayList()
        myList.add(ReportType.TRIMONTHLY)
        myList.add(ReportType.WEEKLY)
        myList.add(ReportType.BIWEEKLY)



        val myOtherList  : MutableList<ReportType> = ArrayList()
        myOtherList.addAll(myList)
        myOtherList.add(ReportType.TRIMONTHLY_1)
        myOtherList.add(ReportType.WEEKLY_1)
        myOtherList.add(ReportType.BIWEEKLY_1)

        GenerateReportFile.addSisterOrdinalsToListofReportTypes(myList)

        assertEquals(myList,myOtherList )
        assertEquals(ReportType.TRIMONTHLY_1, myList[3])
        assertEquals(2 * 3, myList.size)


    }

}