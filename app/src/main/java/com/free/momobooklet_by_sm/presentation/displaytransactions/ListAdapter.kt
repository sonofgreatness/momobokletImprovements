package com.free.momobooklet_by_sm.presentation.displaytransactions

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.BackgroundColorSpan
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.free.momobooklet_by_sm.R
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.data.local.models.TransactionModel


class ListAdapter(val queryText: String?, val app:Application) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    val backgroundColorSpan = app.getColor(R.color.Secondary)



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
        setBackgroundSpanForDateNameAndPIN(holder, currentItem)
        setBackgroundSpanForTransactionType(currentItem, holder)
        setBackgroundSpanForDatePhoneAndAmount(holder, currentItem)

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


    }

    private fun setBackgroundSpanForDateNameAndPIN(
        holder: MyViewHolder,
        currentItem: TransactionModel
    ) {
        if (queryText.isNullOrEmpty()) {
            populateDateNameAndPin(holder, currentItem)
        }
        else
        {
            populateDateNameAndPin(holder, currentItem)
            applyBackgroundSpan(holder.itemView.findViewById(R.id.date_id))
            applyBackgroundSpan(holder.itemView.findViewById(R.id.name_id))
            applyBackgroundSpan(holder.itemView.findViewById(R.id.pin_id))
        }
    }

    private fun populateDateNameAndPin(
        holder: MyViewHolder,
        currentItem: TransactionModel
    ) {
        holder.itemView.findViewById<TextView>(R.id.date_id).text = currentItem.Date
        holder.itemView.findViewById<TextView>(R.id.name_id).text = currentItem.C_Name
        holder.itemView.findViewById<TextView>(R.id.pin_id).text = currentItem.C_ID
    }


    private fun setBackgroundSpanForTransactionType(
        currentItem: TransactionModel,
        holder: MyViewHolder
    ) {
        if (queryText.isNullOrEmpty())
        populateTransactionType(currentItem, holder)
        else
        {
            populateTransactionType(currentItem,holder)
            if(queryText.lowercase().contains(Constants.BUY_IDENTIFIER))
                highlightSellTransactions(currentItem, holder)
            if(queryText.lowercase().contains(Constants.SELL_IDENTIFIER))
                highlightBuyTransactions(currentItem, holder)

        }
    }



    private fun populateTransactionType(
        currentItem: TransactionModel,
        holder: MyViewHolder
    ) {
        if (currentItem.Transaction_type)
            holder.itemView.findViewById<TextView>(R.id.s_t_transactiont_type_id).text =
                app.getText(R.string.buy)
        if (!currentItem.Transaction_type)
            holder.itemView.findViewById<TextView>(R.id.s_t_transactiont_type_id).text =
                app.getString(R.string.sell)
    }

    private fun highlightSellTransactions(currentItem: TransactionModel, holder: MyViewHolder, ) {
        if (!currentItem.Transaction_type)
            applyBackgroundSpanForBuy(holder.itemView.findViewById<TextView>(R.id.s_t_transactiont_type_id))
    }

    private fun applyBackgroundSpanForBuy(prose: TextView?) {

        val queryText = app.getText(R.string.buy)
        val raw: Spannable = SpannableString(app.getText(R.string.buy))
        val spans = raw.getSpans(
            0,
            raw.length,
            BackgroundColorSpan::class.java
        )
        for (span in spans) {
            raw.removeSpan(span)
        }
        var index = TextUtils.indexOf(raw, queryText)
        while (index >= 0) {
            raw.setSpan(
                BackgroundColorSpan(backgroundColorSpan), index, index
                        + queryText!!.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            index = TextUtils.indexOf(raw, queryText, index + queryText.length)
        }
        prose!!.text = raw
    }




    private fun highlightBuyTransactions(currentItem: TransactionModel, holder: ListAdapter.MyViewHolder) {
        if (currentItem.Transaction_type)
            applyBackgroundSpanForSell(holder.itemView.findViewById<TextView>(R.id.s_t_transactiont_type_id))
    }

    private fun applyBackgroundSpanForSell(prose: TextView) {


        val raw: Spannable = SpannableString(prose.text)
        val queryText = app.getText(R.string.buy)
        val spans = raw.getSpans(
            0,
            raw.length,
            BackgroundColorSpan::class.java
        )
        for (span in spans) {
            raw.removeSpan(span)
        }


        var index = TextUtils.indexOf(raw, queryText)
        while (index >= 0) {
            raw.setSpan(
                BackgroundColorSpan(backgroundColorSpan), index, index
                        + queryText!!.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            index = TextUtils.indexOf(raw, queryText, index + queryText.length)
        }
        prose.text = raw
    }



    private fun setBackgroundSpanForDatePhoneAndAmount(
        holder: MyViewHolder,
        currentItem: TransactionModel
    ) {
        if(queryText.isNullOrEmpty())
        populateDatePhoneAndAmount(holder, currentItem)
        else
        {
            populateDatePhoneAndAmount(holder,currentItem)
            applyBackgroundSpan(holder.itemView.findViewById<TextView>(R.id.s_t_date))
            applyBackgroundSpan(holder.itemView.findViewById<TextView>(R.id.s_t_amount_id))
            applyBackgroundSpan(holder.itemView.findViewById<TextView>(R.id.s_t_phone))
        }
    }

    private fun populateDatePhoneAndAmount(
        holder: MyViewHolder,
        currentItem: TransactionModel
    ) {
        holder.itemView.findViewById<TextView>(R.id.s_t_date).text = currentItem.Time
        holder.itemView.findViewById<TextView>(R.id.s_t_phone).text = currentItem.C_PHONE
        holder.itemView.findViewById<TextView>(R.id.s_t_amount_id).text =
            currentItem.Amount.toString()
    }

    private fun applyBackgroundSpan(prose: TextView) {

        val raw: Spannable = SpannableString(prose.text)
        val spans = raw.getSpans(
            0,
            raw.length,
            BackgroundColorSpan::class.java
        )
        for (span in spans) {
            raw.removeSpan(span)
        }


        var index = TextUtils.indexOf(raw, queryText)
        while (index >= 0) {
            raw.setSpan(
                BackgroundColorSpan(backgroundColorSpan), index, index
                        + queryText!!.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            index = TextUtils.indexOf(raw, queryText, index + queryText.length)
        }
        prose.text = raw
    }

}