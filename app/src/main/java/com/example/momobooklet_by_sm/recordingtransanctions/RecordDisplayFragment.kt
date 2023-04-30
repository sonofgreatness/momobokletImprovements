package com.example.momobooklet_by_sm.recordingtransanctions

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.transition.TransitionManager
import com.example.momobooklet_by_sm.MainActivity
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.database.local.models.TransactionModel
import com.example.momobooklet_by_sm.database.local.models.UserModel
import com.example.momobooklet_by_sm.databinding.FragmentRecordDisplayBinding
import com.example.momobooklet_by_sm.databinding.TablefieldsCardBinding
import com.example.momobooklet_by_sm.ui.viewmodels.CommissionViewModel
import com.example.momobooklet_by_sm.ui.viewmodels.TransactionViewModel
import com.example.momobooklet_by_sm.ui.viewmodels.UserViewModel
import com.github.gcacace.signaturepad.views.SignaturePad
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat

import java.util.*

class RecordDisplayFragment : Fragment() {
    private lateinit var mTransactionViewModel: TransactionViewModel
    private lateinit var _cardviewbinding: TablefieldsCardBinding
    private var popupWindow: PopupWindow? = null
    private var signatureCaptured = false
    private lateinit var mCommissionViewModel: CommissionViewModel
    private lateinit var mUserViewModel: UserViewModel
    private var mainUser: UserModel? = null

    private lateinit var _binding: FragmentRecordDisplayBinding
    private val binding get() = _binding
    private val dataObject: TempTransData = TempTransData(
        arguments?.getString("name_key"),
        arguments?.getString("phone_key"),
        arguments?.getString("pin_key"),
        arguments?.getString("amount_key"),
        arguments?.getBoolean("type_key"),
        arguments?.getByteArray("signature_key")
    )


    private fun cardClick(cardviewbinding: TablefieldsCardBinding) {


        cardviewbinding.root.setOnClickListener {


            val value: Boolean = cardviewbinding.linearlayout.isVisible


            if (value) {
                TransitionManager.beginDelayedTransition(cardviewbinding.tablefieldsCardview)
                cardviewbinding.linearlayout.isVisible = false
                cardviewbinding.tablefieldsCardview.isEnabled = false
                cardviewbinding.linearlayout2.isVisible = true
            }
            if (!(value)) {
                TransitionManager.beginDelayedTransition(cardviewbinding.tablefieldsCardview)
                cardviewbinding.linearlayout.isVisible = true
                cardviewbinding.linearlayout2.isVisible = false
            }
            // Change Button OnclickListener

            cardviewbinding.changeDataBtn.setOnClickListener {
                // If text field is null do not change data
                val test: Int = cardviewbinding.changeDataEditTxt.text.toString().length

                if (test != 0) {
                    cardviewbinding.textfieldCardText.setText(cardviewbinding.changeDataEditTxt.text.toString())

                    TransitionManager.beginDelayedTransition(cardviewbinding.tablefieldsCardview)
                    cardviewbinding.linearlayout.isVisible = true
                    cardviewbinding.linearlayout2.isVisible = false

                    // enable card

                    cardviewbinding.tablefieldsCardview.isEnabled = true
                }

                if (test == 0) {
                    TransitionManager.beginDelayedTransition(cardviewbinding.tablefieldsCardview)
                    cardviewbinding.linearlayout.isVisible = true
                    cardviewbinding.linearlayout2.isVisible = false
                    // enable card
                    cardviewbinding.tablefieldsCardview.isEnabled = true
                }
            }

        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentRecordDisplayBinding.inflate(inflater, container, false)
        _cardviewbinding = TablefieldsCardBinding.inflate(inflater, container, false)

        //initialize TransactionviewModel

        //STEP 1
        mTransactionViewModel = (activity as MainActivity).mTransactionViewModel
        mCommissionViewModel = (activity as MainActivity).mCommissionViewModel
        mUserViewModel = (activity as MainActivity).mUserViewModel

        setmainUser()


        val customView: View = inflater.inflate(R.layout.signaturepad_home, null)
        // signature pad variable to be used by other methods
        val pad: SignaturePad = customView.findViewById(R.id.SignId)


        //SETTING SIGNATUREPAD RESPONSES TO BEING SIGNED
        pad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {}
            override fun onSigned() {
                val imageView = customView.findViewById<ImageView>(R.id.signaturepad_preview)
                imageView.visibility = View.VISIBLE
                val signature = pad.signatureBitmap
                val stream = ByteArrayOutputStream()
                signature.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                imageView.setImageBitmap(signature)
                signatureCaptured = true
            }

            override fun onClear() {
            }
        })

        //Make Dialer button invisible in popUpWindow
        customView.findViewById<Button>(R.id.dialer_button).visibility = View.INVISIBLE
        //CHange Text in Record button To Save
        customView.findViewById<Button>(R.id.recordtransact_btn2).setText("SAVE")


        // define OnclickListener for accept button
        customView.findViewById<View>(R.id.accept_signature)
            .setOnClickListener { //CHECK IF USER HAS SIGNED , PROMPT THEM TO IF THEY HAVEN'T
                if (!signatureCaptured) Toast.makeText(
                    context,
                    "Please Draw on Pad to Enter Signature",
                    Toast.LENGTH_SHORT
                ).show()


// Show Dialer Button , show record transaction button , Invalidate signature pad activities
                if (signatureCaptured) {
                    customView.findViewById<View>(R.id.transact_final_action).visibility =
                        View.VISIBLE
                    customView.findViewById<View>(R.id.accept_signature).isEnabled = false
                    customView.findViewById<View>(R.id.clear_signaturepad).isEnabled = false
                    customView.findViewById<View>(R.id.SignId).isEnabled = false
                    customView.findViewById<View>(R.id.accept_signature).visibility = View.INVISIBLE
                    customView.findViewById<View>(R.id.clear_signaturepad).visibility =
                        View.INVISIBLE
                    customView.findViewById<View>(R.id.recordtransact_btn2).visibility =
                        View.VISIBLE


                }
            }


        // define OnclickListener for cancelsignature button

        customView.findViewById<View>(R.id.clear_signaturepad).setOnClickListener {
            pad.clear()
            signatureCaptured = false
        }


        // define OnClicklistener for save Button
        customView.findViewById<Button>(R.id.recordtransact_btn2)
            .setOnClickListener {

                // 1 set bitmap  in pad  to signature_check imageView
                // 2 dismiss popUpWindow

                //  1
                val signature_1 = pad.signatureBitmap
                // for user visualization
                binding.signatureCheck.setImageBitmap(signature_1)
                // for database use

                val bos = ByteArrayOutputStream()
                signature_1.compress(Bitmap.CompressFormat.PNG, 100, bos)
                val bArray = bos.toByteArray()
                dataObject.customersiganature = bArray


                //2
                customView.findViewById<View>(R.id.accept_signature).isEnabled = true
                customView.findViewById<View>(R.id.clear_signaturepad).isEnabled = true
                customView.findViewById<View>(R.id.SignId).isEnabled = true
                customView.findViewById<View>(R.id.accept_signature).visibility = View.VISIBLE
                customView.findViewById<View>(R.id.clear_signaturepad).visibility =
                    View.VISIBLE
                customView.findViewById<View>(R.id.recordtransact_btn2).visibility = View.INVISIBLE
                popupWindow?.dismiss()

            }

//define pop up canceller
        customView.findViewById<ImageView>(R.id.popup_canceller)
            .setOnClickListener(View.OnClickListener {

                customView.findViewById<View>(R.id.accept_signature).isEnabled = true
                customView.findViewById<View>(R.id.clear_signaturepad).isEnabled = true
                customView.findViewById<View>(R.id.SignId).isEnabled = true
                customView.findViewById<View>(R.id.accept_signature).visibility = View.VISIBLE
                customView.findViewById<View>(R.id.clear_signaturepad).visibility =
                    View.VISIBLE
                customView.findViewById<View>(R.id.recordtransact_btn2).visibility = View.INVISIBLE


                binding.appBarCheck.isEnabled = true
                binding.typeCheck.tablefieldsCardview.isEnabled = true
                binding.amountCheck.tablefieldsCardview.isEnabled = true
                binding.phoneCheck.tablefieldsCardview.isEnabled = true
                binding.pinCheck.tablefieldsCardview.isEnabled = true
                binding.checkSignatureLayout.isEnabled = true
                binding.transactCheckBtn.isEnabled = true
                binding.checkSignatureLayout.isEnabled = true
                popupWindow?.dismiss()
            })

        val view = binding.root


        // OnClickListener for each card

        //CARD 1
        cardClick(binding.pinCheck)
        binding.pinCheck.textfieldCardText.setText(arguments?.getString("pin_key"))
//set suffix to card
        binding.pinCheck.textfieldCardText.prefix = "PIN"
        // customize  cardEditText
        binding.pinCheck.changeDataEditTxt.inputType = InputType.TYPE_CLASS_NUMBER
        binding.pinCheck.changeDataInputLayout.hint = getString(R.string.pin_check_hint)


        //CARD2
        cardClick(binding.nameCheck)
        binding.nameCheck.textfieldCardText.setText(arguments?.getString("name_key"))
        //  set suffix to card
        binding.nameCheck.textfieldCardText.prefix = "NAME"
        //Customize EditText
        binding.nameCheck.changeDataEditTxt.inputType = InputType.TYPE_CLASS_TEXT
        binding.nameCheck.changeDataInputLayout.hint = getString(R.string.name_check_hint)


        // CARD 3
        cardClick(binding.phoneCheck)
        binding.phoneCheck.textfieldCardText.setText(arguments?.getString("phone_key"))
        // set suffix
        binding.phoneCheck.textfieldCardText.prefix = "PHONE"
//customize EditText
        binding.phoneCheck.changeDataEditTxt.inputType = InputType.TYPE_CLASS_PHONE
        binding.phoneCheck.changeDataInputLayout.hint = getString(R.string.phone_check_hint)


        // CARD 4
        cardClick(binding.amountCheck)
        binding.amountCheck.textfieldCardText.setText(arguments?.getString("amount_key"))
        //set suffix to card
        binding.amountCheck.textfieldCardText.prefix = "AMOUNT"
//customize EditText
        binding.amountCheck.changeDataEditTxt.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        binding.amountCheck.changeDataInputLayout.hint = getString(R.string.amount_check_hint)


        // CARD 5
        cardClick(binding.typeCheck)
        if (arguments?.getBoolean("type_key") == true)
            binding.typeCheck.textfieldCardText.setText(getString(R.string.buy))
        if (arguments?.getBoolean("type_key") == false)
            binding.typeCheck.textfieldCardText.setText(getString(R.string.sell))
        //set suffix to card
        binding.typeCheck.textfieldCardText.prefix = "TRANSACTION TYPE"

        //cusomize EditText
        binding.typeCheck.changeDataInputLayout.hint =
            getString(R.string.transaction_type_check_hint)
        binding.typeCheck.changeDataEditTxt.inputType = InputType.TYPE_CLASS_TEXT


// signatureCheck
        val byteArray: ByteArray? = arguments?.getByteArray("signature_key")
        byteArray!!
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        binding.signatureCheck.setImageBitmap(bitmap)


//set on click listener for signature layout . must enable user to "correct" signature


        binding.checkSignatureLayout.setOnClickListener {
            //call a pop up window
            popupWindow = PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            //display the popup window
            popupWindow!!.showAtLocation(binding.parentRecordTransacts, Gravity.CENTER, 0, 0)
        }
        setUpcardAnimations()
        setUpRecordData_btn_Onclick()
        setUpNavigation()
        return view
    }

    private fun setmainUser() {

         if (mUserViewModel._readAllData.value?.size!= 0)
             mainUser = mUserViewModel._readAllData.value?.get(0)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpRecordData_btn_Onclick() {
        binding.transactCheckBtn.setOnClickListener {

            if (mainUser!= null) {
                addTransaction(mainUser!!.MoMoNumber)
                Toast.makeText(requireContext(), "Transaction Added", Toast.LENGTH_SHORT).show()

                if (arguments?.getBoolean("phone_dial_key")!!) {
                    val number = Uri.parse(
                        createDialerString(
                            arguments?.getBoolean("type_check")!!,
                            arguments?.getString("phone_key")!!,
                            arguments?.getString("amount_key")!!
                        )
                    )
                    val callIntent = Intent(Intent.ACTION_DIAL, number)
                    startActivity(callIntent)
                }
                it!!.findNavController()
                    .navigate(R.id.action_recordDisplayFragment_to_mainTransactorFragment)
            }
            else {

                informativeMoveToRegistration()
            }
        }
    }

    /**************************************************************
     * informativeMoveToRegistration() -> Either User Table is
     *          empty or no row has the attribute "IsinControl"
     *          set to positive  , let user know this the navigate
     *          to Registration Fragment
     *
     **************************************************************/
    private fun informativeMoveToRegistration() {

        //Do More Here
        Toast.makeText(requireContext(), "User Account Needed ,\n Add One ",
            Toast.LENGTH_SHORT).show()
        binding.root.findNavController()
            .navigate(R.id.action_recordDisplayFragment_to_userAccountsFragment)
    }



    /*************************************************
     * setUpNavigation -> makes up navigate to
     *                      mainTransactor Fragment
     **********************************************/
    private fun setUpNavigation() {
        binding.appBarCheck.setNavigationOnClickListener {
            it.findNavController()
                .navigate(R.id.action_recordDisplayFragment_to_mainTransactorFragment)
        }
    }


    /************************************************************
     * setUpcardAnimations -> scales cards so they
     *                          provide user clue that cards
     *                          are clickable
     ************************************************************/
    private fun setUpcardAnimations() {

        //Animate card
        AnimatorInflater.loadAnimator(requireContext(), R.animator.property_animator)
            .apply {
                setTarget(binding.nameCheck.tablefieldsCardview)
                start()
            } as AnimatorSet

        //animate card
        AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.property_animator_4_secs_later
        )
            .apply {
                setTarget(binding.amountCheck.tablefieldsCardview)
                start()
            } as AnimatorSet


        AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.property_animator_7_secs_later
        )
            .apply {
                setTarget(binding.checkSignatureLayout)
                start()
            } as AnimatorSet
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun addTransaction(phoneNumber: String) {
        val customername: String = binding.nameCheck.textfieldCardText.text.toString()
        val customerphone: String = binding.phoneCheck.textfieldCardText.text.toString()
        val customerpin: String = binding.pinCheck.textfieldCardText.text.toString()
        val transactionamount: String = binding.amountCheck.textfieldCardText.text.toString()

        val transactiontype: String = binding.typeCheck.textfieldCardText.text.toString()
        val dbtransactiontype: Boolean
        val sdf = SimpleDateFormat("dd-MM-yyyy ")
        val currentDate = sdf.format(Date())

        val sd1f = SimpleDateFormat("dd-MM-yyyy hh:mm:ss a")
        val currentTime = sd1f.format(Date())



        dbtransactiontype = transactiontype.startsWith("B", ignoreCase = true)

        dataObject.customersiganature
        dataObject.customername = customername
        dataObject.customerphone = customerphone
        dataObject.customerpin = customerpin

        dataObject.transactionamount = transactionamount
        dataObject.transactiontype = dbtransactiontype
        dataObject.customersiganature = byteArrayMaker()

        val transaction = TransactionModel(
            UUID.randomUUID().toString(), currentDate.trim(),
            dataObject.customername,
            dataObject.customerpin,
            dataObject.customerphone,
            dataObject.transactiontype,
            dataObject.transactionamount.toFloat(),
            dataObject.customersiganature,
            currentTime.trim(),
            phoneNumber
        )
        mTransactionViewModel.addTransaction(transaction)
        mCommissionViewModel.calculateThenUpdateDailyCommission()


    }

    /******************************************************
     * byteArrayMaker -> gets Bitmap of signature  and
     *                  converts it to a byteArray
     * @return : Returns byteArray of Image (Signature)
     *******************************************************/
    private fun byteArrayMaker(): ByteArray? {

        val imageView = binding.signatureCheck
        val bitmap: Bitmap =
            imageView.drawable.toBitmap(imageView.width, imageView.height, Bitmap.Config.RGB_565)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        return stream.toByteArray()
    }

    private fun createDialerString(type: Boolean, cust_phone: String, amount: String): String {
        return if (type)//buy momo
            "tel:*007*1*$cust_phone*$amount#"
        else
            "tel:*007*2*$cust_phone*$amount#"// sells Momo
        // check momo codes and update this string accordingly
    }
}


