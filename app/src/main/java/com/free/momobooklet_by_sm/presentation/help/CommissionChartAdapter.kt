package com.free.momobooklet_by_sm.presentation.help

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.free.momobooklet_by_sm.R
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.data.local.models.CommissionModel


class CommissionChartAdapter : RecyclerView.Adapter<CommissionChartAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<CommissionModel>() {
        override fun areItemsTheSame(
            oldItem: CommissionModel,
            newItem: CommissionModel
        ): Boolean {
            return oldItem.Row_Id == newItem.Row_Id
        }
        override fun areContentsTheSame(
            oldItem: CommissionModel,
            newItem: CommissionModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.commission_chart_row, parent, false)
        )

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        holder.itemView.findViewById<TextView>(R.id.commission_row_min).text = currentItem.Min.toString()
        holder.itemView.findViewById<TextView>(R.id.commission_row_max).text = currentItem.Max.toString()
        holder.itemView.findViewById<TextView>(R.id.commission_row_amount).text =
            currentItem.Commission_Amount.toString()

        if (currentItem.Type)
        {
            holder.itemView.findViewById<TextView>(R.id.commission_row_type).text = Constants.BUY_IDENTIFIER


        }
        else
        {
            holder.itemView.findViewById<TextView>(R.id.commission_row_type).text = Constants.SELL_IDENTIFIER
        }

    }

    override fun getItemCount(): Int {
        return  differ.currentList.size
    }

}