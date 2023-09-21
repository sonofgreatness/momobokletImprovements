package com.example.momobooklet_by_sm.presentation.manageuser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.common.util.Constants
import com.example.momobooklet_by_sm.data.local.models.UserModel
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.UserViewModel
import com.google.android.material.snackbar.Snackbar

class UserProfileRecyclerViewAdapter(val mUserViewModel:UserViewModel, val fragment: Fragment) :
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

            holder.itemView.setOnClickListener {
                setUpItemOnClick(curretItem)
            }

        }
    }

     /****************************************************************************
      *removeItem -> removes a UserModel user    from  RecyclerView list
      *            on swipe (Right or Left),
      *              reverses this action   if user  clicks on snackbar that appears
      *
      *              The user is also  added /removed from the database
      *                                respectively
      *********************************************************************************/
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

        }.show()
    }



    /*************************************************************************
     ***********************************************************************/

    private fun setUpItemOnClick(user: UserModel)
    {
        val mBundle = convertUserModelToBundle(user)
        fragment.findNavController().navigate(R.id.action_userAccountsFragment_to_editUserAccountFragment,mBundle)
    }
    private fun convertUserModelToBundle(user : UserModel): Bundle {

        val mBundle  = Bundle()

        mBundle.putString(Constants.PHONE_NUMBER_KEY, user.MoMoNumber)
        mBundle.putString(Constants.MOMO_NAME_KEY, user.MoMoName)
        mBundle.putString(Constants.MOMO_EMAIL_KEY, user.AgentEmail)
        mBundle.putString(Constants.MOMO_PASSWORD_KEY, user.AgentPassword)
        mBundle.putBoolean(Constants.MOMO_CONTROL_STATUS_KEY, user.IsIncontrol)
        mBundle.putBoolean(Constants.MOMO_REGISTRATION_STATUS_KEY, user.IsRemoteRegistered)
        mBundle.putString(Constants.MOMO_FIREBASEVERIFICATION_KEY, user.FireBaseVerificationId)
        return  mBundle

    }

}