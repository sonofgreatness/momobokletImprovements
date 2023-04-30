package com.example.momobooklet_by_sm.managefiles.staggeredgridlayout

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.util.classes.ReportType
import java.io.File
import java.net.URLConnection


/*******************************************
 * Adapter to show a simple grid of reports
 *******************************************/
class ReportsAdapter internal constructor(val activity: Activity) : RecyclerView.Adapter<ReportsViewHolder>() {


    private var reportList: MutableList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsViewHolder {
        var layoutId = R.layout.staggered_reportcard_first
        if (viewType == 1) {
            layoutId = R.layout.staggered_reportcard_second
        } else if (viewType == 2) {
            layoutId = R.layout.staggered_reportcard_third
        }
        val layoutView = LayoutInflater.from(parent.context)
                                    .inflate(layoutId
                                                    , parent, false)
        return ReportsViewHolder(layoutView)
    }
    
    override fun onBindViewHolder(holder: ReportsViewHolder, position: Int) {
        if (holder.position < reportList.size)
        {
            val file = reportList[position]
            holder.reportTitle.text = file.replaceAfter(".", " ").replace(".","")
            if(file.contains(".csv"))
                  holder.reportImage.setImageResource(R.drawable.csv_mark)
            else
                holder.reportImage.setImageResource(R.drawable.pdf_mark_vd)


            holder.shareButton.setOnClickListener{
                shareFile(file)
            }
            holder.deleteButton.setOnClickListener{
                deleteFile(file,position)
                reportList.remove(file)
                notifyDataSetChanged()
            }

            holder.reportImage.setOnClickListener{
                var ext: String
                if (file.contains(".csv"))
                    ext = "csv"
                else
                    ext = "pdf"

                openfile(ext,file)
            }
        }
    }



    /***********************************************
     * shareFile -> Start intent to share file
     **************************************************/
    private fun shareFile(fileName: String) {


        val filePath: File = File(activity.application.filesDir, "files")
        val newFile = File(filePath, fileName)


        val uri = FileProvider.getUriForFile(
            this.activity,
            "com.example.momobooklet_by_sm.fileprovider",
            newFile
        )

        val intentShareFile = Intent(Intent.ACTION_SEND)
        intentShareFile.setDataAndType(
            uri,
            URLConnection.guessContentTypeFromName(fileName)
        )
        //Allow sharing apps to read the file Uri
        intentShareFile.clipData = ClipData.newRawUri("",uri)


        intentShareFile.addFlags(
            (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        and Intent.FLAG_ACTIVITY_NEW_TASK
        )

        // from android 7 onwards you can not call startActivity   with an intent
        // without adding this flag 👇
       // intentShareFile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        //Pass the file Uri instead of the path
        intentShareFile.putExtra(
            Intent.EXTRA_STREAM,
            uri
        )
        activity.startActivity(Intent.createChooser(intentShareFile,"Share File"),
                                null)

    }

    /****************************************
     *
     ***************************************/
    private fun openfile(ext:String,fileName: String) {


        val filePath: File = File(activity.application.filesDir, "files")
        val newFile = File(filePath, fileName)

        val uri = FileProvider.getUriForFile(
            this.activity,
            "com.example.momobooklet_by_sm.fileprovider",
            newFile
        )

        val myMime = MimeTypeMap.getSingleton()
        val newIntent = Intent(Intent.ACTION_VIEW)
        val mimeType = myMime.getMimeTypeFromExtension(ext)

        newIntent.setDataAndType(uri, mimeType)
        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            activity.startActivity(newIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, "No handler for this type of file.", Toast.LENGTH_LONG).show()
        }

    }

    /******************************************************
     *deleteFile -> deletes File in internal memory ,
     *                it DOES NOT delete the duplicate
     *                file in external memory
     *****************************************************/
    private fun deleteFile(fileName: String, position: Int){
        activity.applicationContext.deleteFile(fileName)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }


    fun setData(list:MutableList<String>)
    {
        this.reportList = list
        notifyDataSetChanged()
    }
    /********************************************
     * filterByCSV -> Remove all files
     *            that donot end with .csv
     *            in their name
     **********************************************/
    fun filterByCSV()
    {

        val dummyList :  MutableList<String> = ArrayList()
        for(i in reportList)
            if(i.contains(".csv"))
                dummyList.add(i)
        setData(dummyList)
    }


    /********************************************
     * filterByPDF -> Remove all files
     *            that do not end with .pdf
     *            in their name
     **********************************************/
    fun filterByPDF()
    {
        val dummyList :  MutableList<String> = ArrayList()
        for(i in reportList)
            if(i.contains(".pdf"))
                dummyList.add(i)
        setData(dummyList)
    }


    /**
     *
     */
    fun filterByReportTypeList(listoftypes: MutableList<ReportType>)
     {
         val mySuperList : MutableList<String> = ArrayList()

         for (i in listoftypes)
            mySuperList.addAll(filterByReportType(i))
         setData(mySuperList)
     }
    /**************************************************************
     * filterByReportType -> filters list of files by ReportType
     * @param type , ReportType variable select type of file
     *     by comparing if filename starts with type.name
     **************************************************************/
    private fun filterByReportType(type:ReportType) : MutableList<String>
    {
        val dummyList :  MutableList<String> = ArrayList()
        for(i in reportList)
            if(i.startsWith(type.name))
                dummyList.add(i)
        return (dummyList)
    }
}