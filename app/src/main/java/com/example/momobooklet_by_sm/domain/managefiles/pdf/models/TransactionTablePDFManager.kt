package com.example.momobooklet_by_sm.domain.managefiles.pdf.models

import android.graphics.BitmapFactory
import com.example.momobooklet_by_sm.data.local.models.TransactionModel
import com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.Exportable
import com.example.momobooklet_by_sm.common.util.Constants
import com.wwdablu.soumya.simplypdf.composers.properties.ImageProperties
import com.wwdablu.soumya.simplypdf.composers.properties.TextProperties
import com.wwdablu.soumya.simplypdf.composers.properties.cell.Cell
import com.wwdablu.soumya.simplypdf.composers.properties.cell.ImageCell
import com.wwdablu.soumya.simplypdf.composers.properties.cell.TextCell
import java.util.*

/***************************************************************************
 * This class handles operations to do with
 *      Converting List of TransactionModel to LinkedList..LinkedList..Cell>
 *       To write PDF Tables using simplyPdf library
 ****************************************************************************/

class TransactionTablePDFManager(
                        val transactions:List<TransactionModel>,
                        val textProperties: TextProperties, val imageProperties: ImageProperties,
                      ): Exportable {



   val returnList: LinkedList<LinkedList<Cell>> = LinkedList<LinkedList<Cell>>()

    private var  column1_width: Int = 0
    private var  column2_width: Int = 0
    private var  column3_width: Int = 0
    private var  column4_width: Int = 0
    private var  column5_width: Int = 0
    private var  column6_width: Int = 0
    private var  column7_width: Int = 0
    private var  column8_width: Int = 0
    /** +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * tableRowsmaker -> Creates a LinkedList<LinkedList<Cell>>
     *                          to be used as PDF table rows
     * @param transactions  -> list of TransactionModels to make rows
     *                            each model make a single row
     * @return : returns  LinkedList<LinkedList<Cell>>
     *           each TransactionModel attribute is converted to TextCell
     *             and TransactionModel.Signature  ->  ImageCell
     * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
   fun tableRowsMaker(): LinkedList<LinkedList<Cell>> {

        val transactions: List<TransactionModel> = transactions

        val titleRow =  LinkedList<Cell>().apply {
            //add(TextCell(Constants.TRANSACTIONCOLUMNTITLE_1, textProperties, column1_width))
            add(TextCell(Constants.TRANSACTIONCOLUMNTITLE_2, textProperties, column2_width))
            add(TextCell(Constants.TRANSACTIONCOLUMNTITLE_3, textProperties, column3_width))
            add(TextCell(Constants.TRANSACTIONCOLUMNTITLE_4, textProperties, column4_width))
            add(TextCell(Constants.TRANSACTIONCOLUMNTITLE_5, textProperties, column5_width))
            add(TextCell(Constants.TRANSACTIONCOLUMNTITLE_6, textProperties, column6_width))
            add(TextCell(Constants.TRANSACTIONCOLUMNTITLE_7, textProperties, column7_width))
            add(TextCell(Constants.TRANSACTIONCOLUMNTITLE_8, textProperties, column8_width))
        }

        returnList.add(titleRow)

        for (transaction in transactions)
            addTransaction(transaction)
        return returnList
    }

    /**++++++++++++++++++++++++++++++++++++++++
    *converts a TransactionModel to  a
     *  simplyPdf Table ready row
    *******************************************/
    private fun addTransaction(transaction: TransactionModel) {

        var transaction_type: String
        if (transaction.Transaction_type)
            transaction_type = "Buys MoMo"
        else
        transaction_type = "Sells MoMo"

       val row = LinkedList<Cell>().apply{
           // add(TextCell(transaction.Transaction_ID.toString(),textProperties,column1_width))
           add(TextCell(transaction.Date,textProperties,column2_width))
           add(TextCell(transaction.C_Name,textProperties,column3_width))
           add(TextCell(transaction.C_PHONE,textProperties,column4_width))
           add(TextCell(transaction.C_ID,textProperties,column5_width))
           add(TextCell(transaction_type,textProperties,column6_width))
           add(TextCell(transaction.Amount.toString(),textProperties,column7_width))
           add(ImageCell(BitmapFactory.decodeByteArray(transaction.Signature,0,transaction.Signature.size),imageProperties,column8_width))
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
    fun setColumn4Width(width : Int) {
        column4_width = width
    }
    fun setColumn5Width(width : Int) {
        column5_width = width
    }
    fun setColumn6Width(width : Int) {
        column6_width = width
    }
    fun setColumn7Width(width : Int) {
        column7_width = width
    }
    fun setColumn8Width(width : Int) {
        column8_width = width
    }

}

