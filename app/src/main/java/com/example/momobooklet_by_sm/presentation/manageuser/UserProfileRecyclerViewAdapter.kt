package com.example.momobooklet_by_sm.presentation.manageuser

import com.example.momobooklet_by_sm.data.local.models.UserModel
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.View
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.SnapHelper
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.UserViewModel
import com.google.android.material.snackbar.Snackbar

class UserProfileRecyclerViewAdapter(val mUserViewModel:UserViewModel) :
    RecyclerView.Adapter<UserProfileRecyclerViewAdapter.MyViewHolderr>() {


    private var removePosition : Int = 0
    private lateinit  var removeUserModel: UserModel
    inner class MyViewHolderr(itemView: View): RecyclerView.ViewHolder(itemView)

    private val  differCallback= object : DiffUtil.ItemCallback<UserModel>(){
        override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return   oldItem.MoMoNumber==newItem.MoMoNumber
        }

        override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel):Boolean {
            return oldItem==newItem
        }


    }
    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderr {
        return MyViewHolderr(LayoutInflater.from(parent.context).inflate(R.layout.user_profile_card, parent, false))
    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolderr, position: Int) {
        if (position < differ.currentList.size) {
             val curretItem = differ.currentList[position]
            holder.itemView.findViewById<TextView>(R.id.CardHead).text=curretItem.MoMoName
            holder.itemView.findViewById<TextView>(R.id.CardSubHead).text=curretItem.MoMoNumber
            if (!curretItem.IsIncontrol) holder.itemView.findViewById<ImageView>(R.id.Incontrol_status).setImageResource(R.drawable.circle_darkgray_fill)
            if (curretItem.IsIncontrol) holder.itemView.findViewById<ImageView>(R.id.Incontrol_status).setImageResource(R.drawable.beeping_radio_btn)

        }
    }

    fun removeItem(viewHolder: MyViewHolderr) {

        val newList = ArrayList<UserModel>()
        removePosition = viewHolder.absoluteAdapterPosition
        removeUserModel  = differ.currentList[removePosition]
        newList.addAll(differ.currentList)
        newList.removeAt(viewHolder.absoluteAdapterPosition)
        differ.submitList(newList)
        mUserViewModel.deleteUser(removeUserModel)


        Snackbar.make(viewHolder.itemView,"${removeUserModel.MoMoName} : ${removeUserModel.MoMoNumber}  deleted", Snackbar.LENGTH_LONG).setAction("UNDO"){
            newList.add(removePosition,removeUserModel)
            differ.submitList(newList)
            mUserViewModel.addUser(removeUserModel)
          notifyItemInserted(removePosition)
        }.show()
    }



    private fun deleteUser(user: UserModel)
    {
        mUserViewModel.deleteUser(user)
    }
}