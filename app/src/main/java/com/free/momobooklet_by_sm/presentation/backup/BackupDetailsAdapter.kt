package com.free.momobooklet_by_sm.presentation.backup

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.free.momobooklet_by_sm.R
import com.free.momobooklet_by_sm.domain.use_cases.disaster_recovery.server.BackUpDetails
import com.free.momobooklet_by_sm.presentation.ui.viewmodels.BackupDataBaseViewModel

class BackupDetailsAdapter(val app : Application,
                           val mBackupDataBaseViewModel: BackupDataBaseViewModel,
                          val username :String) : RecyclerView.Adapter<BackupDetailsAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    private val differCallback = object : DiffUtil.ItemCallback<BackUpDetails>() {

        override fun areItemsTheSame(
            oldItem: BackUpDetails,
            newItem: BackUpDetails
        ): Boolean {
            return oldItem.backupdId == newItem.backupdId
        }

        override fun areContentsTheSame(
            oldItem: BackUpDetails,
            newItem: BackUpDetails
        ): Boolean {
            return oldItem == newItem
        }


    }


    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackupDetailsAdapter.MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.custom_row_backupdetails, parent, false)
        )
    }




    override fun onBindViewHolder(holder: BackupDetailsAdapter.MyViewHolder, position: Int) {
        val currentItem =  differ.currentList[position]
        holder.itemView.
        findViewById<TextView>(
            R.id.backup_details_date_id).text = currentItem.time

        holder.itemView.setOnClickListener {
            processBackupRequest(currentItem.backupdId)

        }
    }


    private fun processBackupRequest(backupdId: String) {
        Toast.makeText(app.applicationContext,"id : $-+backupdId", Toast.LENGTH_SHORT).show()
        mBackupDataBaseViewModel.downloadBackupFromServer(app,username, backupdId)

    }

    override fun getItemCount(): Int {
       return  differ.currentList.size
    }

}