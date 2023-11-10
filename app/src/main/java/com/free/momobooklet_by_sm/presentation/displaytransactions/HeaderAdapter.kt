package com.free.momobooklet_by_sm.presentation.displaytransactions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.free.momobooklet_by_sm.R

class HeaderAdapter : RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder>()
{

    class HeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        return HeaderViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.custom_row_header, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 1
    }


}