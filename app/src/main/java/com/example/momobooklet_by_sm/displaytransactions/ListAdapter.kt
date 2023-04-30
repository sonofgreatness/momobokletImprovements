package com.example.momobooklet_by_sm.displaytransactions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.database.local.models.TransactionModel


class ListAdapter : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<TransactionModel>() {
        override fun areItemsTheSame(
            oldItem: TransactionModel,
            newItem: TransactionModel
        ): Boolean {
            return oldItem.Transaction_ID == newItem.Transaction_ID
        }

        override fun areContentsTheSame(
            oldItem: TransactionModel,
            newItem: TransactionModel
        ): Boolean {
            return oldItem == newItem
        }


    }


    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = differ.currentList[position]
        holder.itemView.findViewById<TextView>(R.id.date_id).text = currentItem.Date
        holder.itemView.findViewById<TextView>(R.id.name_id).text = currentItem.C_Name
        holder.itemView.findViewById<TextView>(R.id.pin_id).text = currentItem.C_ID
        if (currentItem.Transaction_type == true)
            holder.itemView.findViewById<TextView>(R.id.s_t_transactiont_type_id).text = "Buys MoMo"
        if (currentItem.Transaction_type == false)
            holder.itemView.findViewById<TextView>(R.id.s_t_transactiont_type_id).text =
                "Sells MoMo"
        holder.itemView.findViewById<TextView>(R.id.s_t_amount_id).text =
            currentItem.Amount.toString()
        // Get ByteArray from transaction Transaction Model obect convert to Bitmap

        if (currentItem.Signature.size > 2)
        {

        val bitmap: Bitmap =
        BitmapFactory.decodeByteArray(currentItem.Signature, 0, currentItem.Signature.size)
        holder.itemView.findViewById<ImageView>(R.id.s_t_imageview_id).setImageBitmap(bitmap)
        }
        else
            holder.itemView.findViewById<ImageView>(R.id.s_t_imageview_id).setImageResource(R.drawable.remove)

        holder.itemView.findViewById<ImageView>(R.id.s_t_button_id).setOnClickListener(View.OnClickListener {

            if (holder.itemView.findViewById<LinearLayout>(R.id.layout_change).visibility==View.VISIBLE){
                //CHANGING AFOREMENTIONED WIDGET TO COLLAPSED
                TransitionManager.beginDelayedTransition(holder.itemView.findViewById(R.id.constrainView))

                holder.itemView.findViewById<LinearLayout>(R.id.layout_change).visibility = View.GONE
                holder.itemView.findViewById<ImageView>(R.id.s_t_button_id).setImageResource(R.drawable.collapsible_down_blue)
                holder.itemView.findViewById<LinearLayout>(R.id.s_t_phone_layout).visibility = View.GONE

            }else if(holder.itemView.findViewById<LinearLayout>(R.id.layout_change).visibility==View.GONE){
                TransitionManager.beginDelayedTransition(holder.itemView.findViewById(R.id.constrainView))
                holder.itemView.findViewById<LinearLayout>(R.id.layout_change).visibility = View.VISIBLE
                holder.itemView.findViewById<ImageView>(R.id.s_t_button_id).setImageResource(R.drawable.collapsible_right_blue)
                holder.itemView.findViewById<LinearLayout>(R.id.s_t_phone_layout).visibility = View.VISIBLE

            }

        })
        holder.itemView.findViewById<TextView>(R.id.s_t_date).text=currentItem.Time.toString()
        holder.itemView.findViewById<TextView>(R.id.s_t_phone).text=currentItem.C_PHONE

/*
        holder.itemView.id_txt.text = currentItem.id.toString()
        holder.itemView.firstName_txt.text = currentItem.firstName
        holder.itemView.lastName_txt.text = currentItem.lastName
        holder.itemView.age_txt.text = currentItem.age.toString()

        holder.itemView.rowLayout.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }
        */
    }

}