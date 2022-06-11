package com.example.momobooklet_by_sm.manageuser;

import android.graphics.drawable.Animatable;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.momobooklet_by_sm.R;
import com.example.momobooklet_by_sm.database.model.UserModel;

import java.util.List;

public class UserProfileRecyclerViewAdapter extends RecyclerView.Adapter<UserProfileViewHolder> {
final  public List<UserModel> UserList;
 public UserProfileRecyclerViewAdapter(List<UserModel> userList){

 this.UserList= userList;



}
 @NonNull
 @Override
 public UserProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
  View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_profile_card, parent, false);
  return new UserProfileViewHolder(layoutView);
 }

 @Override
 public void onBindViewHolder(@NonNull UserProfileViewHolder holder, int position) {

  if (position < UserList.size()){
   UserModel object = UserList.get(position);
   holder.UserMoMoName.setText(object.getMoMoName().toString());
   holder.UserPhone.setText(object.getMoMoNumber().toString());
 holder.collapseBtn.setOnClickListener(new View.OnClickListener() {
  @Override
  public void onClick(View v) {

   // INITIALLY VIEWING WIDGET IS NOT VISIBLE->UN-COLLAPSED
   if(holder.container.getVisibility()==View.GONE){
    //CHANGING AFOREMENTIONED WIDGET TO COLLAPSED
    TransitionManager.beginDelayedTransition(holder.cardview);
   holder.container.setVisibility(View.VISIBLE);
   // VISIUAL CUE TO INDICATE TO USER NEW STATE OF WIDGET(COLLAPSED)
   holder.collapseBtn.setImageResource(R.drawable.collapsible_down);}
else if(holder.container.getVisibility()==View.VISIBLE) {
 TransitionManager.beginDelayedTransition(holder.cardview);
 holder.container.setVisibility(View.GONE);
 holder.collapseBtn.setImageResource(R.drawable.collapsible_right);
}

  }
 });

if(!(object.getIsIncontrol()))
 holder.UserIncontrolStatusImagr.setImageResource(R.drawable.newbeeper);
if(object.getIsIncontrol())
holder.UserIncontrolStatusImagr.setImageResource(R.drawable.beeping_radio_btn);
 //
   Animatable animatable = (Animatable) holder.UserIncontrolStatusImagr.getDrawable();
   animatable.start();
   //AnimatedVectorDrawableCompat animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(holder.cardview.getContext(), R.drawable.beeping_radio_btn);
  // holder.UserIncontrolStatusImagr.setImageDrawable(animatedVectorDrawableCompat);


  }
 }

 @Override
 public int getItemCount() {
  return UserList.size();
 }


}








