package com.example.momobooklet_by_sm.manageuser;

import android.os.ParcelUuid;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.momobooklet_by_sm.R;

public class UserProfileViewHolder extends RecyclerView.ViewHolder {
public ImageView UserProfileImage;
public ImageView UserIncontrolStatusImagr ;
public ImageView collapseBtn ;
public Button EditAccount_btn ;
public Button MakeMaster_btn;
public RelativeLayout container;

public TextView  UserMoMoName;
public TextView UserPhone;
public CardView cardview;




    public UserProfileViewHolder(@NonNull View itemView) {
        super(itemView);

UserProfileImage= itemView.findViewById(R.id.user_profile_image);
UserIncontrolStatusImagr=itemView.findViewById(R.id.Incontrol_status);
UserMoMoName=itemView.findViewById(R.id.CardHead);
UserPhone=itemView.findViewById(R.id.CardSubHead);
container=itemView.findViewById(R.id.user_profile_relatLayout);
collapseBtn= itemView.findViewById(R.id.collapse_arrow);
EditAccount_btn=itemView.findViewById(R.id.user_profile_edit_btn);
MakeMaster_btn= itemView.findViewById(R.id.user_profile_give_control_btn);
cardview=itemView.findViewById(R.id.cardview);


    }
}