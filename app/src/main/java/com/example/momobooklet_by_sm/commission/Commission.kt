package com.example.momobooklet_by_sm.commission

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.momobooklet_by_sm.MainActivity
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.database.local.models.UserModel
import com.example.momobooklet_by_sm.databinding.FragmentCommissionBinding
import com.example.momobooklet_by_sm.manageuser.RegisterFragment
import com.example.momobooklet_by_sm.ui.viewmodels.CommissionViewModel
import com.example.momobooklet_by_sm.ui.viewmodels.TransactionViewModel
import com.example.momobooklet_by_sm.ui.viewmodels.UserViewModel
import com.example.momobooklet_by_sm.util.CommisionDatesManager
import com.example.momobooklet_by_sm.util.Constants
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class Commission : Fragment() {

    private lateinit var _binding : FragmentCommissionBinding
    private val binding get() = _binding
    private lateinit var rootView:View
    private lateinit var  mTransactionViewModel:TransactionViewModel
    private lateinit var  mUserViewModel: UserViewModel
    private lateinit var  mCommissionViewModel:CommissionViewModel
    private var mainUser: UserModel? = null
    @RequiresApi(Build.VERSION_CODES.O)
    private lateinit var datesManager :CommisionDatesManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
         _binding = FragmentCommissionBinding.inflate(inflater,container,false)
        rootView = binding.root

        mTransactionViewModel = (activity as MainActivity).mTransactionViewModel
        mUserViewModel = (activity as MainActivity).mUserViewModel
        mCommissionViewModel = (activity as MainActivity).mCommissionViewModel
        datesManager = CommisionDatesManager(activity as MainActivity)
        setUpAllViews()

            return rootView
        }




    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpAllViews()
    {


            setupdailyCommissionView()
            setupLastMonthView()
            setupCompanyNameView()
            setupBottomSheet()
            binding.bottomsheetCustomdailyCommissionCard
                .root.visibility = View.GONE


    }





    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupLastMonthView() {
        val strings = datesManager.getLastMonthDates_()
        val size = strings.size
        val numberofTransactionsView =binding.monthlyCommissionCardEditable
                                             .monthlyNumberofTransactionsCard.root

        binding.monthlyCommissionCardEditable.apply {
            monthlyCommissionTitleTextBig
                .text = getString(R.string.last_month_s_commission)

            monthlyCommissionTitleText
                .text = strings[0].plus(" to ").plus(strings[size - 1])

            mCommissionViewModel.monthlyCommission
                .observe(viewLifecycleOwner) {
                    val df = DecimalFormat("##.##")
                    df.roundingMode = RoundingMode.DOWN
                    monthlyCommissionText.text = (df.format(it.Commission_Amount)).toString().plus(" SZL ")


                    monthlyNumberofTransactionsCard
                        .numberofTransactionsTxt
                        .text = getString(R.string.number_of_transactions)
                                        .plus(" ").plus(it.Number_of_Transactions.toString())
                }
        }


        binding.monthlyCommissionCardEditable.root.setOnClickListener{
            toggleVisibility_likeMagic(numberofTransactionsView)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupBottomSheet() {

        BottomSheetBehavior.from(binding.standardBottomSheet).apply{
            peekHeight = 100
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        setupbottomsheetThisMonthView()
        setupbottomsheetLastMonthView()
        setupbottomsheetTodayView()
        setupbotomsheetYesterdayView()
        setupbottomsheet_change_startdate_button()
        setupbottomsheet_choose_my_day_button()
        setupbottomsheet_change_start_date_button()
    }

    private  fun setupbottomsheet_change_start_date_button()
    {
        binding.modifyMonthlyStartdateBtn.setOnClickListener{
        showChangeStartDateDialogue()

        }
    }


    private fun showChangeStartDateDialogue() {

       AlertDialog.
                Builder(requireContext())
           .setPositiveButton("Done", DialogInterface.OnClickListener { dialog, which ->
                                                                    dialog.dismiss()})
           .setTitle("SELECT YOUR MONTHLY START DATE")
           .setSingleChoiceItems(arrayOf("3rd ","4th", "5th", "6th","7th"), 0,
                                    DialogInterface.OnClickListener { dialog, which ->
                                          writeStartDateInFile(which + 3)
                                    })
           .create()
           .show()

    }

    /******************************************************************************
     * writeStartDateInFile -> writes  data to file () "ManageMonthlyStartDate.txt"
     *                                overwrites what ever is in file
     * @param starDate : Int , the data to be written in file ,
     *                          is expected to have length = 1
     ***************************************************************************/
    private fun writeStartDateInFile(starDate: Int)
    {
        val FileToCheck = File(requireActivity().application.filesDir.path.plus("/").plus(Constants.MONTHLY_STARTDATE_FILENAME))
        if(FileToCheck.exists())
            writeData(FileToCheck,starDate.toString())
    }
    /***********************************************************************
     * writeData  -> Writes Data passed  in File ManageMonthlyStartDate.txt
     *                  throws an exception if failed to Open file
     *                       ,  failed to write to file or to Close
     *
     * @param  the File in  which to write data
     *@param starDate String , The data to write in File
     ************************************************************************/
    private fun writeData(FileToCheck: File, starDate: String) {

            try{

                val fos: FileOutputStream = requireActivity().openFileOutput(Constants.MONTHLY_STARTDATE_FILENAME, Context.MODE_PRIVATE)
                fos.write(starDate.toByteArray())
                fos.close()
            }catch (ex: Exception)
            {
                Log.d("FailedToWriteManageStartDateFile : -> ", "${ex.message} :::::: \t ${ex.stackTrace}")
            }
            finally {
                    Toast.makeText(requireContext(), "startDate Edited Successfully", Toast.LENGTH_SHORT).show()
            }
        }


    private fun setupbottomsheet_choose_my_day_button() {
        binding.seeCustomDayCommissionBtn.setOnClickListener{
            showDateAlertDialogue()
        }
    }

    /*********************************************************
     * showAlertDialogue() -> shows DatePickerDialogue
     *                         set on Today's Date
     **********************************************************/
    private fun showDateAlertDialogue() {

        val c: Calendar = Calendar.getInstance()
        val mYear: Int = c.get(Calendar.YEAR) // current year
        val mMonth: Int = c.get(Calendar.MONTH) // current month
        val mDay: Int = c.get(Calendar.DAY_OF_MONTH) // current day

        Log.d("numbers here", "${mDay} ${mMonth} ${mYear}")

        DatePickerDialog (requireContext(),
            { view, year, month, dayOfMonth ->

                if (datesManager.checkifDateIsBeforeToday(dayOfMonth,month,year)) {
                     setCustomDailyCommission(dayOfMonth,month,year)
                    assignCommissionModelToCardView(getDatePicked(dayOfMonth, month, year))
                }else
                    Toast.makeText(requireContext(),"Please Choose Day in The Past", Toast.LENGTH_SHORT).show()
            },
            mYear,
            mMonth,
        mDay)
            .show()
    }



    /************************************************************************************
     * assignCommissionModelToCardView - makes custom day commissioncard  visible and
     *                                  populates  it's  views with DailyCommissionModel
     *                                  attributes
     *@param dailyCommission , The DailyCommissionModel from a user decided date
     *                               we take the date , Amount and  # of transactions To pass
     *                               into custom Day card's TextViews
     *@param datePiked , The date picked by the user,since dailCommission?
     *                     we still need to display the date picked by the user
     ***********************************************************************************/
    private fun assignCommissionModelToCardView(
        datePicked: String
    ){


            binding.bottomsheetCustomdailyCommissionCard
                                        .root.visibility = View.VISIBLE

            binding.bottomsheetCustomdailyCommissionCard.apply {
            commissionAnalysisTitleText.text = datePicked
            commissionAnalysisTitleTextBig.text = getString(R.string.custom_day)

           mCommissionViewModel.customDayCommission.observe(viewLifecycleOwner){
               dailyCommissionText
                   .text = mCommissionViewModel
                   .makeADouble_ACurrency_String(it?.Commission_Amount)
           }
        }
    }


    /*************************************************************
     * getDailyCommission gets Daily Commission for specific date
     * @param dayOfMonth , Int : the day part of date (dd-MM-YYYY)
     * @param month,  Int : the month part of date (dd-MM-YYYY)
     * @param year , Int : the year part of date (dd-MM-YYYY)
     *
     * @return : Returns DailyCommissionModel of date  from database
     **************************************************************/
    private fun setCustomDailyCommission(dayOfMonth: Int, month: Int, year: Int) {
               val date = getDatePicked(dayOfMonth, month, year)
                    mCommissionViewModel.setCustomDaysCommissionModel(date)
    }

    private fun getDatePicked(dayOfMonth: Int, month: Int, year: Int):String {
            return datesManager.convertDatePickerIntToMyDate(dayOfMonth,month,year)
    }

    private fun setupbottomsheet_change_startdate_button() {
        Log.d("yes:", "yes")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupbotomsheetYesterdayView() {
        var numberofTransactions = 0
        val numberofTransactionsView =binding.dailyCommissionCardBottomsheetYesterday
            .dayNumberofTransactionsCard
            .root



        binding.dailyCommissionCardBottomsheetYesterday
            .commissionAnalysisTitleText
            .text = datesManager.getYesterdayStringUltimate()

        binding.dailyCommissionCardBottomsheetYesterday
                        .commissionAnalysisTitleTextBig.text = getString(R.string.yesterdays_commission)


        mCommissionViewModel.yesterDayCommission.observe(viewLifecycleOwner){
            if(it!= null) {

                binding.dailyCommissionCardBottomsheetYesterday
                    .dailyCommissionText.text = it.Commission_Amount.toString().plus(" SZL")
                numberofTransactions = it.Number_of_Transactions


            }else {
                binding.dailyCommissionCardBottomsheetYesterday
                    .dailyCommissionText.text = "0.0".plus(" SZL")
            }
            binding.dailyCommissionCardBottomsheetYesterday
                .dayNumberofTransactionsCard
                .numberofTransactionsTxt
                .text = getString(R.string.number_of_transactions).plus(" ")
                .plus(numberofTransactions.toString())
        }

        binding.dailyCommissionCardBottomsheetYesterday
            .dailyCommissionText.setOnClickListener{
                toggleVisibility_likeMagic(numberofTransactionsView)
            }
    }

    private fun setupbottomsheetTodayView() {
        var numberofTransactions = 0
        val numberofTransactionsView =binding.dailyCommissionCardBottomsheetToday
            .dayNumberofTransactionsCard
            .root

        binding.dailyCommissionCardBottomsheetToday
                .commissionAnalysisTitleText
                    .text = datesManager.generateTodayDate()

        mCommissionViewModel
            .dailyCommission
                .observe(viewLifecycleOwner){
            if(it!= null) {
                binding.dailyCommissionCardBottomsheetToday
                    .dailyCommissionText.text = it.Commission_Amount.toString().plus(" SZL")
                    numberofTransactions = it.Number_of_Transactions
            }else
                binding.dailyCommissionCardBottomsheetToday
                        .dailyCommissionText.text = "0.0 SZL"

                    binding.dailyCommissionCardBottomsheetToday
                        .dayNumberofTransactionsCard
                        .numberofTransactionsTxt
                        .text = getString(R.string.number_of_transactions).plus(" ")
                        .plus(numberofTransactions.toString())
        }

        binding.dailyCommissionCardBottomsheetToday
            .dailyCommissionText.setOnClickListener{
                toggleVisibility_likeMagic(numberofTransactionsView)
            }
    }

    private fun setupbottomsheetThisMonthView() {
            /* This is Last Month Commission*/

        val strings = datesManager.getLastMonthDates_()
        val size = strings.size

        val numberofTransactionsView =binding.bottomSheetThismonthCommission
            .monthlyNumberofTransactionsCard
            .root


        binding.bottomSheetThismonthCommission.apply {

            monthlyCommissionTitleTextBig
                .text = getString(R.string.last_month_s_commission)
                monthlyCommissionTitleText
                    .text = strings[0].plus(" to ").plus(strings[size -1])

                mCommissionViewModel.monthlyCommission
                .observe(viewLifecycleOwner) {
                    val df = DecimalFormat("##.###")
                    df.roundingMode = RoundingMode.DOWN
                    monthlyCommissionText.text = (df.format(it.Commission_Amount)).toString().plus(" SZL ")

                    monthlyNumberofTransactionsCard
                        .numberofTransactionsTxt
                        .text = getString(R.string.number_of_transactions)
                        .plus(" ").plus(it.Number_of_Transactions.toString())
                }
        }

        binding.bottomSheetThismonthCommission.root.setOnClickListener{
                toggleVisibility_likeMagic(numberofTransactionsView)
            }

    }

    private fun setupbottomsheetLastMonthView() {
        /*This Month*/
        val strings = datesManager.getThisMonthDates_()
        Log.d("Fuckery","${strings}")
        val size = strings.size

        val numberofTransactionsView =binding.bottomSheetLastmonthCommission
            .monthlyNumberofTransactionsCard
            .root


        binding.bottomSheetLastmonthCommission.apply {
            monthlyCommissionTitleTextBig
            .text = getString(R.string.this_months_commission)

            monthlyCommissionTitleText
            .text = strings[0].plus(" to ").plus(strings[size - 1])

            mCommissionViewModel.thisMonthCommission
                                    .observe(viewLifecycleOwner){
                                        val df = DecimalFormat("##.###")
                                        df.roundingMode = RoundingMode.DOWN
                                        monthlyCommissionText.text = (df.format(it.Commission_Amount)).toString().plus(" SZL ")

                                        monthlyNumberofTransactionsCard
                                            .numberofTransactionsTxt
                                            .text = getString(R.string.number_of_transactions)
                                            .plus(" ").plus(it.Number_of_Transactions.toString())

                                    }

        }
        binding.bottomSheetLastmonthCommission.root.setOnClickListener{
            toggleVisibility_likeMagic(numberofTransactionsView)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupdailyCommissionView()
    {

        val numberofTransactionsView =binding.dailyCommissionCard
                                                .dayNumberofTransactionsCard
                                                    .root


        var numberofTransactions = 0
        binding.dailyCommissionCard.commissionAnalysisTitleText.text = datesManager.generateTodayDate()

        mCommissionViewModel.dailyCommission.observe(viewLifecycleOwner) {
            if(it!= null) {
                binding.dailyCommissionCard.dailyCommissionText.text =
                    it.Commission_Amount.toString().plus("  SZL")
                        numberofTransactions = it.Number_of_Transactions
            }else
                binding.dailyCommissionCard.dailyCommissionText.text = "0.00 SZL"
            binding.dailyCommissionCard
                .dayNumberofTransactionsCard
                .numberofTransactionsTxt
                .text = getString(R.string.number_of_transactions).plus(" ")
                .plus(numberofTransactions.toString())
        }

        binding.dailyCommissionCard
            .dailyCommissionText.setOnClickListener{
                toggleVisibility_likeMagic(numberofTransactionsView)
            }

    }

   private fun setupCompanyNameView() {
        mUserViewModel._userInControl.observe(viewLifecycleOwner){
            binding.companyName.companyPhoneText.text = it[0].MoMoNumber
            binding.companyName.companyNameText.text = it[0].MoMoName
        }
    }

    /**************************************************************
     * toggleVisibility_likeMagic - calls toggleVisibility on
     *                              view , waits for 2 seconds
     *                              calls toggleVisibilty
     *********************************************************************/

    private fun toggleVisibility_likeMagic(view : View) {
        lifecycleScope.launch {
            toggleVisibility(view)
            delay(3000)
            toggleVisibility(view)
        }
    }

    /**********************************************************************
     * toggleVisibility() - changes the visibility of a view
     *                 if View is View.VISIBLE , it is changed to View.GONE
     *@param : View , View to be toggled
     ************************************************************************/
    private fun toggleVisibility(view : View)
    {
        if (view.visibility == View.VISIBLE)
            view.visibility = View.GONE
        else
            view.visibility = View.VISIBLE
    }

}