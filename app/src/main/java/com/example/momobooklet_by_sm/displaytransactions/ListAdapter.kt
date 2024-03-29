package com.example.momobooklet_by_sm.displaytransactions

import android.os.Bundle
import android.os.Build
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.database.model.TransactionModel


class ListAdapter : RecyclerView.Adapter<com.example.momobooklet_by_sm.displaytransactions.ListAdapter.MyViewHolder>() {

    private var userList = emptyList<TransactionModel>()
   class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {}



     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder{
         return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false))
     }

    override fun getItemCount(): Int {
        return userList.size
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]
 holder.itemView.findViewById<TextView>(R.id.date_id).text=currentItem.Date
        holder.itemView.findViewById<TextView>(R.id.name_id).text= currentItem.C_Name
        holder.itemView.findViewById<TextView>(R.id.pin_id).text=currentItem.C_ID
        if(currentItem.Transaction_type==true)
            holder.itemView.findViewById<TextView>(R.id.s_t_transactiont_type_id).text="Buys MoMo"
        if (currentItem.Transaction_type==false)
   holder.itemView.findViewById<TextView>(R.id.s_t_transactiont_type_id).text="Sells MoMo"
        holder.itemView.findViewById<TextView>(R.id.s_t_amount_id).text=currentItem.Amount.toString()
        // Get ByteArray from transaction Transaction Model obect convert to Bitmap
        val bitmap:Bitmap=BitmapFactory.decodeByteArray(currentItem.Signature,0,currentItem.Signature.size)

        holder.itemView.findViewById<ImageView>(R.id.s_t_imageview_id).setImageBitmap(bitmap)



        holder.itemView.findViewById<ImageView>(R.id.s_t_button_id).setOnClickListener(View.OnClickListener {



            if (holder.itemView.findViewById<LinearLayout>(R.id.layout_change).visibility==View.VISIBLE){


                //CHANGING AFOREMENTIONED WIDGET TO COLLAPSED
                TransitionManager.beginDelayedTransition(holder.itemView.findViewById(R.id.constrainView))

                holder.itemView.findViewById<LinearLayout>(R.id.layout_change).visibility = View.GONE
                holder.itemView.findViewById<ImageView>(R.id.s_t_button_id).setImageResource(R.drawable.collapsible_down_blue)


            }else if(holder.itemView.findViewById<LinearLayout>(R.id.layout_change).visibility==View.GONE){



                TransitionManager.beginDelayedTransition(holder.itemView.findViewById(R.id.constrainView))

                holder.itemView.findViewById<LinearLayout>(R.id.layout_change).visibility = View.VISIBLE
                holder.itemView.findViewById<ImageView>(R.id.s_t_button_id).setImageResource(R.drawable.collapsible_right_blue)


            }


        })
        holder.itemView.findViewById<TextView>(R.id.s_t_date).text=currentItem.Time




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



    fun setData(transaction: List<TransactionModel>){
        this.userList = transaction
        notifyDataSetChanged()
    }


}