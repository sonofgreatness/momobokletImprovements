package com.example.momobooklet_by_sm.recordingtransanctions;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.io.ByteArrayOutputStream;

import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.momobooklet_by_sm.MainActivity;
import com.example.momobooklet_by_sm.R;
import com.example.momobooklet_by_sm.databinding.FragmentMainTransactorBinding;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.material.textfield.TextInputLayout;


public class MainTransactorFragment extends Fragment {

private FragmentMainTransactorBinding binding;
PopupWindow popupWindow;
     private Boolean signatureCaptured= false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_main_transactor, container, false);

        Bundle mBundle = new Bundle();




        binding = binding.inflate(inflater, container, false);
         // back arrow in the appbar OnclickListener

       Toolbar toolbar = (Toolbar) binding.appBar;
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
              Intent i =new Intent(getActivity(),MainActivity.class);
           startActivity(i);
            }
        });





        View customView = inflater.inflate(R.layout.signaturepad_home,null);
// signature pad variable to be used by other methods
        SignaturePad pad =customView.findViewById(R.id.SignId);

        //SETTING SIGNATUREPAD RESPONSES TO BEING SIGNED
pad.setOnSignedListener(new SignaturePad.OnSignedListener() {
    @Override
    public void onStartSigning() {

    }

    @Override
    public void onSigned() {
        ImageView imageView=customView.findViewById(R.id.signaturepad_preview);
        imageView.setVisibility(View.VISIBLE);




        Bitmap signature=pad.getSignatureBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        signature.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        imageView.setImageBitmap(signature);

        signatureCaptured=true;
      //  Toast.makeText(getContext(),"Signed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClear() {
        //Toast.makeText(getContext(),"Cleared", Toast.LENGTH_SHORT).show();
    }
});
        binding.recordtransactBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        boolean checker=Validator(binding.customernameId.getText().toString(),binding.customerpinId.getText().toString(),binding.customerphoneId.getText().toString(),binding.transactionamountId.getText().toString());

        if(!checker) {

            popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // disable views beneath pop up window


            // could have put these in a group in the layout file then setEnabled(false ) only the group

            binding.customernameId.setEnabled(false);
            binding.appBar.setEnabled(false);
            binding.customerphoneId.setEnabled(false);
            binding.customerpinId.setEnabled(false);
            binding.transactionamountId.setEnabled(false);
            binding.transactiontypeId.setEnabled(false);
            binding.recordtransactBtn.setEnabled(false);


            //display the popup window
            popupWindow.showAtLocation(binding.mainTransactorLayout, Gravity.CENTER, 0, 0);

// Make sure signature pad is responsive along with input buttons:

            customView.findViewById(R.id.transact_final_action).setVisibility(View.GONE);
            customView.findViewById(R.id.accept_signature).setEnabled(true);
            customView.findViewById(R.id.clear_signaturepad).setEnabled(true);
            customView.findViewById(R.id.SignId).setEnabled(true);
            customView.findViewById(R.id.accept_signature).setVisibility(View.VISIBLE);
            customView.findViewById(R.id.clear_signaturepad).setVisibility(View.VISIBLE);

            customView.findViewById(R.id.popup_canceller).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    popupWindow.dismiss();
                    binding.customernameId.setEnabled(true);
                    binding.customerphoneId.setEnabled(true);
                    binding.customerpinId.setEnabled(true);
                    binding.transactionamountId.setEnabled(true);
                    binding.transactiontypeId.setEnabled(true);
                    binding.recordtransactBtn.setEnabled(true);

                    binding.appBar.setEnabled(true);


                }
            });

            customView.findViewById(R.id.clear_signaturepad).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    pad.clear();
                    signatureCaptured=false;
                }
            });


            customView.findViewById(R.id.accept_signature).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//CHECK IF USER HAS SIGNED , PROMP THEM TO IF THEY HAVEN'T

                    if (!(signatureCaptured))
                        Toast.makeText(getContext(), "Please Draw on Pad to Enter Signature", Toast.LENGTH_SHORT).show();


// Show Dialer Button , show record transaction button , Invalidate signature pad activities
                    if (signatureCaptured) {
                        customView.findViewById(R.id.transact_final_action).setVisibility(View.VISIBLE);
                        customView.findViewById(R.id.accept_signature).setEnabled(false);
                        customView.findViewById(R.id.clear_signaturepad).setEnabled(false);
                        customView.findViewById(R.id.SignId).setEnabled(false);


                        customView.findViewById(R.id.accept_signature).setVisibility(View.INVISIBLE);
                        customView.findViewById(R.id.clear_signaturepad).setVisibility(View.INVISIBLE);
                    }


                }

            });
            //
        }if(checker)
            Toast.makeText(getContext(),"Please fill in all fields ",Toast.LENGTH_SHORT).show();




    }
});


        // Define dialer and record buttons action :


        //DIALER
        customView.findViewById(R.id.dialer_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

                String name =binding.customernameId.getText().toString();
                String pin= binding.customerpinId.getText().toString();
                String phone=binding.customerphoneId.getText().toString();
                String amount=binding.transactionamountId.getText().toString();
                boolean type= binding.transactiontypeId.isChecked();
                Bitmap signature_1=pad.getSignatureBitmap();

                ByteArrayOutputStream stream_1 = new ByteArrayOutputStream();
                signature_1.compress(Bitmap.CompressFormat.PNG, 100, stream_1);
                byte[] byteArray_1 = stream_1.toByteArray();




                mBundle.putString("name_key",name);
                mBundle.putString("pin_key",pin);
                mBundle.putString("phone_key",phone);
                mBundle.putString("amount_key",amount);
                mBundle.putBoolean("type_key",type);
                mBundle.putByteArray("signature_key",byteArray_1);


                TransactionRecorded(mBundle);


            }
        });



        //RECORD BUTTON
        customView.findViewById(R.id.recordtransact_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

           popupWindow.dismiss();

                String name =binding.customernameId.getText().toString();
                String pin= binding.customerpinId.getText().toString();
                String phone=binding.customerphoneId.getText().toString();
                String amount=binding.transactionamountId.getText().toString();
                boolean type= binding.transactiontypeId.isChecked();
                Bitmap signature_1=pad.getSignatureBitmap();

                ByteArrayOutputStream stream_1 = new ByteArrayOutputStream();
                signature_1.compress(Bitmap.CompressFormat.PNG, 100, stream_1);
                byte[] byteArray_1 = stream_1.toByteArray();




                mBundle.putString("name_key",name);
                mBundle.putString("pin_key",pin);
                mBundle.putString("phone_key",phone);
                mBundle.putString("amount_key",amount);
                mBundle.putBoolean("type_key",type);
                mBundle.putByteArray("signature_key",byteArray_1);


// DATA TO BE PASSED TO RECORD DISPLAY FRAGMENT

                mBundle.putString("name_key",name);
                mBundle.putString("pin_key",pin);
                mBundle.putString("phone_key",phone);
                mBundle.putString("amount_key",amount);
                mBundle.putBoolean("type_key",type);
                mBundle.putByteArray("signature_key",byteArray_1);

// PASS DATA TO RECORD DISPLAY FRAGMENT USING BUNDLE
                TransactionRecorded(mBundle);
            }


        });



        //SET onChekChangedListener for switch

        binding.transactiontypeId.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!(isChecked))
                    buttonView.setText(R.string.sell);
                if(isChecked)
                    buttonView.setText(R.string.buy);
            }
        });





        //VALIDATE USER INPUT

        //set error message for phoneEditText
    //    binding.inputCustomerphoneId.setError(getString(R.string.error_phone));
//set error message for pinEditText
    //    binding.inputCustomerpin.setError(getString(R.string.error_pin));










        View view = binding.getRoot();

        return view;

    }

public void TransactionRecorded( Bundle mBundle){

 Navigation.findNavController(binding.getRoot()).navigate(R.id.action_mainTransactorFragment_to_recordDisplayFragment,mBundle);


}
public boolean Validator(String name, String PIN,String phone,String amount){


       boolean first= (!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(PIN));
                boolean second=(!TextUtils.isEmpty(phone)&&!TextUtils.isEmpty(amount));

return!(first&&second);



}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

private  boolean isPinValid(Editable text){
        return text!= null &&text.length()==13;

}

private boolean isPhoneValid(Editable text){
  return text !=null &&text.length()==8 ;

}

}