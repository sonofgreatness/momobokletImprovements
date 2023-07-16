package com.example.momobooklet_by_sm.presentation.recordingtransanctions

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import androidx.navigation.Navigation.findNavController

import android.widget.PopupWindow
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View

import com.example.momobooklet_by_sm.R
import com.github.gcacace.signaturepad.views.SignaturePad
import android.widget.ImageView
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory

import android.view.Gravity
import android.widget.Toast
import android.text.TextUtils
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.momobooklet_by_sm.MainActivity
import com.example.momobooklet_by_sm.databinding.FragmentMainTransactorBinding
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.UserViewModel
import com.example.momobooklet_by_sm.common.util.Constants
import com.google.android.material.textfield.TextInputEditText

class MainTransactorFragment : Fragment() {
    private var binding: FragmentMainTransactorBinding? = null
    private var popupWindow: PopupWindow? = null
    private var signatureCaptured = false
    val positionAnim : ValueAnimator = ObjectAnimator.ofInt(this, "wordPosition", 0, 4)
    private var strings:Array<String> = arrayOf("submitting","submitting.", "submitting..", "submitting...")
    var position : Int = 0
    lateinit var  view_to_animate : View


    private lateinit  var mUserViewModel: UserViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_main_transactor, container, false);
        val mBundle = Bundle()
        binding =
            FragmentMainTransactorBinding.inflate(
                inflater,
                container,
                false
            )

        mUserViewModel = (activity as MainActivity).mUserViewModel

        view_to_animate = binding!!.recordtransactBtn


        positionAnim.duration = 1500
        positionAnim.repeatCount = ValueAnimator.INFINITE
        positionAnim.repeatMode = ValueAnimator.RESTART
        positionAnim.start()

        // back arrow in the appbar OnclickListener
        val customView = inflater.inflate(R.layout.signaturepad_home, null)
        // signature pad variable to be used by other methods
        val pad: SignaturePad = customView.findViewById(R.id.SignId)
        //SETTING SIGNATUREPAD RESPONSES TO BEING SIGNED
        pad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {}
            override fun onSigned() {
                val imageView =
                    customView.findViewById<ImageView>(R.id.signaturepad_preview)
                imageView.visibility = View.VISIBLE
                val signature = pad.signatureBitmap
                val stream = ByteArrayOutputStream()
                signature.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                imageView.setImageBitmap(signature)
                signatureCaptured = true
                //  Toast.makeText(getContext(),"Signed", Toast.LENGTH_SHORT).show();
            }

            override fun onClear() {
                //Toast.makeText(getContext(),"Cleared", Toast.LENGTH_SHORT).show();
            }
        })
        binding!!.recordtransactBtn.setOnClickListener {
            setUpRecordTransactionOnClick(customView, pad)
        }
        // Define dialer and record buttons action :
        //DIALER
        customView.findViewById<View>(R.id.dialer_button)
            .setOnClickListener {
                setUpDialerOnClick(pad, mBundle)
            }
        //RECORD BUTTON
        customView.findViewById<View>(R.id.recordtransact_btn2)
            .setOnClickListener {
                setUpRecordbtn2OnClick(pad, mBundle)
            }
        //SET onChekChangedListener for switch
        binding!!.transactiontypeId.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) buttonView.setText(R.string.sell)
            if (isChecked) buttonView.setText(R.string.buy)
        }


        return binding!!.root
    }

     fun setWordPosition(position : Int){
         this.position  = position
         (view_to_animate as TextView).setText(strings[position])
    }
    fun getWordPosition() : Int{

        return(position)
    }

    private fun setUpRecordbtn2OnClick(
        pad: SignaturePad,
        mBundle: Bundle
    ) {
        popupWindow!!.dismiss()
        val name = binding!!.customernameId.text.toString()
        val pin = binding!!.customerpinId.text.toString()
        val phone = binding!!.customerphoneId.text.toString()
        val amount = binding!!.transactionamountId.text.toString()
        val type = binding!!.transactiontypeId.isChecked
        val signature_1 = pad.signatureBitmap
        val stream_1 = ByteArrayOutputStream()
        signature_1.compress(Bitmap.CompressFormat.PNG, 100, stream_1)
        val byteArray_1 = stream_1.toByteArray()

        // DATA TO BE PASSED TO RECORD DISPLAY FRAGMENT
        mBundle.putString("name_key", name)
        mBundle.putString("pin_key", pin)
        mBundle.putString("phone_key", phone)
        mBundle.putString("amount_key", amount)
        mBundle.putBoolean("type_key", type)
        mBundle.putByteArray("signature_key", byteArray_1)
        mBundle.putBoolean("phone_dial_key", false)
        // PASS DATA TO RECORD DISPLAY FRAGMENT USING BUNDLE
        TransactionRecorded(mBundle)
    }

    private fun setUpDialerOnClick(
        pad: SignaturePad,
        mBundle: Bundle
    ) {
        popupWindow!!.dismiss()
        val name = binding!!.customernameId.text.toString()
        val pin = binding!!.customerpinId.text.toString()
        val phone = binding!!.customerphoneId.text.toString()
        val amount = binding!!.transactionamountId.text.toString()
        val type = binding!!.transactiontypeId.isChecked
        val signature_1 = pad.signatureBitmap
        val stream_1 = ByteArrayOutputStream()
        signature_1.compress(Bitmap.CompressFormat.PNG, 100, stream_1)
        val byteArray_1 = stream_1.toByteArray()
        mBundle.putString("name_key", name)
        mBundle.putString("pin_key", pin)
        mBundle.putString("phone_key", phone)
        mBundle.putString("amount_key", amount)
        mBundle.putBoolean("type_key", type)
        mBundle.putByteArray("signature_key", byteArray_1)
        mBundle.putBoolean("phone_dial_key", true)
        TransactionRecorded(mBundle)
    }

    private fun setUpRecordTransactionOnClick(
        customView: View,
        pad: SignaturePad
    ) {
        val checker = Validator(
            binding!!.customernameId.text.toString(),
            binding!!.customerpinId.text.toString(),
            binding!!.customerphoneId.text.toString(),
            binding!!.transactionamountId.text.toString()
        )
        if (!checker) {

            //check if pin is valid
            if (isPinValid(binding!!.customerpinId)) {
                if (isPhoneValid(binding!!.customerphoneId)) {
                    popupWindow = PopupWindow(
                        customView,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    // disable views beneath pop up window
                    disableViewsbelowPopUpWindow()
                    //display the popup window
                    popupWindow!!.showAtLocation(
                        binding!!.mainTransactorLayout,
                        Gravity.CENTER,
                        0,
                        0
                    )
                    //Make sure signature pad is responsive along with input buttons:
                    customView.findViewById<View>(R.id.transact_final_action)
                        .visibility = View.GONE
                    customView.findViewById<View>(R.id.accept_signature).isEnabled = true
                    customView.findViewById<View>(R.id.clear_signaturepad).isEnabled = true
                    customView.findViewById<View>(R.id.SignId).isEnabled = true
                    customView.findViewById<View>(R.id.accept_signature).visibility = View.VISIBLE
                    customView.findViewById<View>(R.id.clear_signaturepad).visibility = View.VISIBLE

                    setUpPopwindowCanceller(customView)

                    customView.findViewById<View>(R.id.clear_signaturepad)
                        .setOnClickListener {
                            pad.clear()
                            signatureCaptured = false
                        }
                    customView.findViewById<View>(R.id.accept_signature)
                        .setOnClickListener { //CHECK IF USER HAS SIGNED , PROMP THEM TO IF THEY HAVEN'T
                            if (!signatureCaptured) Toast.makeText(
                                context,
                                "Please Draw on Pad to Enter Signature",
                                Toast.LENGTH_SHORT
                            ).show()


                            // Show Dialer Button , show record transaction button , Invalidate signature pad activities
                            if (signatureCaptured) {
                                customView.findViewById<View>(R.id.transact_final_action).visibility =
                                    View.VISIBLE
                                customView.findViewById<View>(R.id.accept_signature).isEnabled =
                                    false
                                customView.findViewById<View>(R.id.clear_signaturepad).isEnabled =
                                    false
                                customView.findViewById<View>(R.id.SignId).isEnabled =
                                    false
                                customView.findViewById<View>(R.id.accept_signature).visibility =
                                    View.INVISIBLE
                                customView.findViewById<View>(R.id.clear_signaturepad).visibility =
                                    View.INVISIBLE
                            }
                        }
                } else Toast.makeText(
                    context,
                    "Fill in Valid Phone Number ",
                    Toast.LENGTH_SHORT
                ).show()
            } else Toast.makeText(context, "Fill in Valid Pin ", Toast.LENGTH_SHORT)
                .show()
        } else if (checker) Toast.makeText(
            context,
            "Please fill in all fields ",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setUpPopwindowCanceller(customView: View) {
        customView.findViewById<View>(R.id.popup_canceller)
            .setOnClickListener {
                popupWindow!!.dismiss()
                binding!!.customernameId.isEnabled = true
                binding!!.customerphoneId.isEnabled = true
                binding!!.customerpinId.isEnabled = true
                binding!!.transactionamountId.isEnabled = true
                binding!!.transactiontypeId.isEnabled = true
                binding!!.recordtransactBtn.isEnabled = true
            }
    }

    private fun disableViewsbelowPopUpWindow() {
        // could have put these in a group in the layout file then setEnabled(false ) only the group
        binding!!.customernameId.isEnabled = false
        binding!!.customerphoneId.isEnabled = false
        binding!!.customerpinId.isEnabled = false
        binding!!.transactionamountId.isEnabled = false
        binding!!.transactiontypeId.isEnabled = false
        binding!!.recordtransactBtn.isEnabled = false
    }


    private fun TransactionRecorded(mBundle: Bundle?) {
        findNavController(binding!!.root).navigate(
            R.id.action_mainTransactorFragment_to_recordDisplayFragment,
            mBundle
        )
    }

    private fun Validator(name: String?, PIN: String?, phone: String?, amount: String?): Boolean {
        val first = !TextUtils.isEmpty(name) && !TextUtils.isEmpty(PIN)
        val second = !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(amount)
        return !(first && second)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun isPinValid(text: TextInputEditText?): Boolean {
        return text != null && text.length() == Constants.CHARACTER_COUNT_PIN
    }

    private fun isPhoneValid(text: TextInputEditText?): Boolean {
        return text != null && text.length() == Constants.CHARACTER_COUNT_PHONE
    }


    override fun onStop() {
        positionAnim.cancel()
        super.onStop()
    }

    override fun onDestroy() {
        positionAnim.cancel()
        super.onDestroy()
    }

}
