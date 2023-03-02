package com.example.momobooklet_by_sm.managefiles.staggeredgridlayout

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.momobooklet_by_sm.R

class ReportsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
    var reportImage: ImageView= itemView.findViewById(R.id.report_image)
    var reportTitle: TextView = itemView.findViewById(R.id.report_title)
    var deleteButton: ImageButton = itemView.findViewById(R.id.report_delete_btn)
    var shareButton : ImageButton  = itemView.findViewById(R.id.report_share_btn)
}