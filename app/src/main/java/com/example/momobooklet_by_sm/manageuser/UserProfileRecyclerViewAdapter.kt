package com.example.momobooklet_by_sm.manageuser

import android.transition.TransitionManager
import com.example.momobooklet_by_sm.database.local.models.UserModel
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.View
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.ui.viewmodels.UserViewModel

class UserProfileRecyclerViewAdapter(val mUserViewModel:UserViewModel) :
    RecyclerView.Adapter<UserProfileRecyclerViewAdapter.MyViewHolderr>() {



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

           /*
            holder.itemView.findViewById<ImageView>(R.id.collapse_arrow).setOnClickListener {
                // INITIALLY VIEWING WIDGET IS NOT VISIBLE->UN-COLLAPSED
                if (holder.itemView.findViewById<RelativeLayout>(R.id.user_profile_relatLayout).visibility == View.GONE) {
                    //CHANGING AFOREMENTIONED WIDGET TO COLLAPSED
                    TransitionManager.beginDelayedTransition(holder.itemView.findViewById(R.id.cardview))
                    holder.itemView.findViewById<RelativeLayout>(R.id.user_profile_relatLayout).visibility   = View.VISIBLE
                    // VISIUAL CUE TO INDICATE TO USER NEW STATE OF WIDGET(COLLAPSED)
                    holder.itemView.findViewById<ImageView>(R.id.collapse_arrow).setImageResource(R.drawable.collapsible_down)
                } else if (holder.itemView.findViewById<RelativeLayout>(R.id.user_profile_relatLayout).visibility == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(holder.itemView.findViewById(R.id.cardview))
                    holder.itemView.findViewById<RelativeLayout>(R.id.user_profile_relatLayout).visibility     = View.GONE
                    holder.itemView.findViewById<ImageView>(R.id.collapse_arrow) .setImageResource(R.drawable.collapsible_right)
                }
            }
            */



            if (!curretItem.IsIncontrol) holder.itemView.findViewById<ImageView>(R.id.Incontrol_status).setImageResource(R.drawable.circle_teal_fill)
            if (curretItem.IsIncontrol) holder.itemView.findViewById<ImageView>(R.id.Incontrol_status).setImageResource(R.drawable.beeping_radio_btn)
            /*
            val animatable = holder.itemView.findViewById<ImageView>(R.id.Incontrol_status).drawable as Animatable
            animatable.start()
             */
            //AnimatedVectorDrawableCompat animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(holder.cardview.getContext(), R.drawable.beeping_radio_btn);
            // holder.UserIncontrolStatusImagr.setImageDrawable(animatedVectorDrawableCompat);

        /* On Click Listeners */
           /* holder.itemView.findViewById<ImageView>(R.id.delete_icon).setOnClickListener {
                mUserViewModel.deleteUser(curretItem)
            }*/
        }
    }
}