package com.free.momobooklet_by_sm.domain.managefiles.pdf.models

import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.data.local.models.DailyCommissionModel
import com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.Exportable
import com.free.momobooklet_by_sm.common.util.Constants.Companion.DAILYCOMMISSIONCOLUMNTITLE_1
import com.free.momobooklet_by_sm.common.util.Constants.Companion.DAILYCOMMISSIONCOLUMNTITLE_2
import com.free.momobooklet_by_sm.common.util.Constants.Companion.DAILYCOMMISSIONCOLUMNTITLE_3
import com.wwdablu.soumya.simplypdf.composers.properties.TextProperties
import com.wwdablu.soumya.simplypdf.composers.properties.cell.Cell
import com.wwdablu.soumya.simplypdf.composers.properties.cell.TextCell
import java.util.*



/***************************************************************************
 * This class handles operations to do with
 *      Converting List of DailyCommissionModel to LinkedList..LinkedList..Cell>
 *       To write PDF Tables using simplyPdf library
 ****************************************************************************/

class DailyCommissionModelPDFManager(
    private val commissions: List<DailyCommissionModel>
    ) : Exportable {



    private val textProperties: TextProperties = TextProperties().apply {
        textSize = Constants.PDF_TABLE_TEXT_SIZE
        textColor = Constants.PDF_TABLE_TEXT_COLOR
    }

   private  val headerTextProperties : TextProperties  = TextProperties().apply {
        this.textSize = Constants.PDF_TABLE_TEXT_HEADER_SIZE
        textColor = Constants.PDF_TABLE_HEADER_TEXT_COLOR
    }



    val returnList: LinkedList<LinkedList<Cell>> = LinkedList<LinkedList<Cell>>()
    private var column1_width: Int = 0
    private var column2_width: Int = 0
    private var column3_width: Int = 0


    /** +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * tableRowsmaker -> Creates a LinkedList<LinkedList<Cell>>
     *                          to be used as PDF table rows
     * @param commissions  -> list of DailyCommissionModels to make rows
     *                            each model make a single row
     * @return : returns  LinkedList<LinkedList<Cell>>
     *           each CommissionModel attribute is converted to TextCell
     *
     * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    fun tableRowsMaker(): LinkedList<LinkedList<Cell>> {

        val commissions: List<DailyCommissionModel> = commissions

        val titleRow =  LinkedList<Cell>().apply {
            add(TextCell(DAILYCOMMISSIONCOLUMNTITLE_1, headerTextProperties, column1_width))
            add(TextCell(DAILYCOMMISSIONCOLUMNTITLE_2, headerTextProperties, column2_width))
            add(TextCell(DAILYCOMMISSIONCOLUMNTITLE_3, headerTextProperties, column3_width))
        }
        returnList.add(titleRow)

        for (commission in commissions)
            addCommission(commission)

        return returnList
    }

    /**++++++++++++++++++++++++++++++++++++++++
     *converts a CommissionModel to  a
     *  simplyPdf Table ready row
     *******************************************/
    private fun addCommission(commission: DailyCommissionModel) {

        val row = LinkedList<Cell>().apply {
            add(TextCell(commission.Date, textProperties, column1_width))
            add(
                TextCell(
                    commission.Number_of_Transactions.toString(),
                    textProperties,
                    column2_width
                )
            )
            add(TextCell(commission.Commission_Amount.toString(), textProperties, column3_width))
        }
        returnList.add(row)
    }


    /*=================================================================
      SETTER FUNCTIONS
  ===================================================================*/
    fun setColumn1Width(width : Int) {
        column1_width = width
    }
    fun setColumn2Width(width : Int) {
        column2_width = width
    }
    fun setColumn3Width(width : Int) {
        column3_width = width
    }

}