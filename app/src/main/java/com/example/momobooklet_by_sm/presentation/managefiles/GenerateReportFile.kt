package com.example.momobooklet_by_sm.presentation.managefiles

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.momobooklet_by_sm.MainActivity
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.databinding.FragmentGenerateReportFileBinding
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.CommissionViewModel
import com.example.momobooklet_by_sm.common.util.classes.FileFormatType
import com.example.momobooklet_by_sm.common.util.classes.ReportType
import java.util.*


class GenerateReportFile : Fragment() {

    private lateinit var  _binding: FragmentGenerateReportFileBinding
    private  val binding get() = _binding
    private var fileFormat :FileFormatType? = null
    private var listOfReportTypes: MutableList<ReportType> =ArrayList()
    private  lateinit var  mCommissionViewModel: CommissionViewModel
    private   var startDate: String? = null



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGenerateReportFileBinding.inflate(inflater,container,false)
        mCommissionViewModel = (activity as MainActivity).mCommissionViewModel
        (activity as MainActivity).getFileManagementPermissions()
        setupOnClickListeners()
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupOnClickListeners() {

        binding.customUpicon.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_generateReportFile_to_managePDFsLandingFragment2)
        }

        binding.createBtn.setOnClickListener{
            trackGenerateReport()
            createReportFromInputs()
         }

        binding.radioGroup.pickDateBtnCustomradiogroup.setOnClickListener{
            getStartDate()
        }
    }

    /*********************************************************************************
     * trackGenerateReport()  -> check Network State , if connected
     *                          send mixpanel data
     ***********************************************************************************/
    private fun trackGenerateReport() {

       // if ((activity as MainActivity).myIsConnected)
        //    (activity as MainActivity).mMixpanel.people.increment("generate file called", 1.0)

    }

    /***********************************************************************************
     * createReportFromInputs()  -> creates report File(s)  by  using
     *                          data selected by user in RadioGroup, ChipGroup
     *                          and selected Date
     **********************************************************************************/
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("LogNotTimber")
    private fun createReportFromInputs(){

        if (listOfReportTypes.isNotEmpty())
                listOfReportTypes.clear()

            collectReportTypeDateFromChips()
            if (listOfReportTypes.isNotEmpty()) {
                collectFileFormatFromRadioButtons()
                if (fileFormat != null) {

                    if (startDate != null) {
                        discriminateReportTypesByFileFormat()
                        for (type in listOfReportTypes)
                            mCommissionViewModel.writeReport(startDate!!, type)
                        Toast.makeText(
                            requireContext(),
                            "File Write SuccessFul",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please Pick A  Start Date",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please Specify File Format",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                // Do animations in these else statemets
                Toast.makeText(requireContext(), "Please Select Report Type", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    /*************************************************************************************
     * collectReportTypeDateFromChips - translates  choice made by user into list
     *                                of ReportType, i.e
     *                                if the 3Month chip is checked  a 3MONTH ReportType
     *                                is added to the list , an empty list signifies
     *                                no chip is checked
     **************************************************************************************/

    private fun collectReportTypeDateFromChips()
    {
        binding.chipGroup.apply{

            if(weeklyChip.isChecked)
                listOfReportTypes.add(ReportType.WEEKLY)
            if(biweeklyChip.isChecked)
                listOfReportTypes.add(ReportType.BIWEEKLY)
            if(monthlyChip.isChecked)
                listOfReportTypes.add(ReportType.MONTHLY)
            if(trimonthChip.isChecked)
                listOfReportTypes.add(ReportType.TRIMONTHLY)

        }
    }

    /*********************************************************************************
     *collectFileFormatFromRadioButtons  - translates  choice made by user
     *                                of FileFormatType  choices are PDF, CSV
     *                                and BOTH default choice is PDF
     *
     *******************************************************************************/
    private fun collectFileFormatFromRadioButtons()
    {
        binding.radioGroup.apply {
            fileFormat = when {
                csvRadioBtn.isChecked -> FileFormatType.CSV
                pdfRadioBtn.isChecked -> FileFormatType.PDF
                bothRadioBtn.isChecked -> FileFormatType. BOTH
                else ->{
                    null
                }
            }

        }
    }

    /*******************************************************************
     * getStartDate() ->  shows  DatePicker Dialogue  and  converts date
     *                  picked by user to string of the the form dd-MM-YYYY
     **********************************************************************/
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStartDate() {
        showDateAlertDialogue()

    }


    /*********************************************************
     * showAlertDialogue() -> shows DatePickerDialogue
     *                         set on Today's Date
     **********************************************************/
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDateAlertDialogue() {

        val c: Calendar = Calendar.getInstance()
        val mYear: Int = c.get(Calendar.YEAR) // current year
        val mMonth: Int = c.get(Calendar.MONTH) // current month
        val mDay: Int = c.get(Calendar.DAY_OF_MONTH) // current day

        DatePickerDialog (requireContext(),
            { view, year, month, dayOfMonth ->
                startDate = getDatePicked(dayOfMonth,month,year)
                binding.radioGroup.showdateCustomradiogroup
                    .text = startDate
            },
            mYear,
            mMonth,
            mDay)
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDatePicked(dayOfMonth: Int, month: Int, year: Int):String {
        return mCommissionViewModel.datesManager
            .convertDatePickerIntToMyDate(dayOfMonth,month,year)
    }
    /*******************************************************************************
     * discriminateReportTypesByFileFormat()
     *                          -> updates the listofReportTypes variable using the
     *                          fileFormat variable
     *                          if fileFormat = PDF, listofReport types should have
     *                          ReportTypes of ordinal < 4 exclusively
     *
     *                          if fileFormat = CSV listofReport types should have
     *                          ReportTypes of ordinal > 4 exclusively
     *
     *
     *                        if fileFormat = BOTH listofReport types should have
     *                        ReportTypes of ordinals 0-7
     *******************************************************************************/
    private fun discriminateReportTypesByFileFormat() {
        when(fileFormat){
            FileFormatType.CSV->{
                //change list to contain attributes with ordinals > 4
                incrementOrdinalsinListofReportTypes(listOfReportTypes)
            }
            FileFormatType.BOTH->{
                //add corresponding Types with ordinal = ordinal + 4
                addSisterOrdinalsToListofReportTypes(listOfReportTypes)
            }
        }
    }


    /*****************************************************************************************
     * incrementOrdinalsinListofReportTypes
     *   transforms a List<ReportType> by adding 3 to each ordinal
     *
     ******************************************************************************************/
      fun incrementOrdinalsinListofReportTypes(listOfReportTypes: MutableList<ReportType>) {

        var originalOrdinals: MutableList<Int> = ArrayList()

        //get ordinals and store them in  above  variable
        for (type in listOfReportTypes)
              originalOrdinals.add(type.ordinal)
        //clear list
        listOfReportTypes.clear()
        //add New elements
        for (ord in originalOrdinals)
            listOfReportTypes.add(mapOrdinalToInstance(ord + 4))
    }

    /**************************************************************************************
     * addSisterOrdinalsToListofReportTypes ->
     *                                      add new ReportType variables in list
     *                                      ex: if list has a single variable
     *                                          [WEEKLY]
     *                                      the updated list will contain
     *                                      [WEEKLY,WEEKLY_1]
     *****************************************************************************************/
    fun addSisterOrdinalsToListofReportTypes(listOfReportTypes: MutableList<ReportType>) {

        var originalOrdinals: MutableList<Int> = ArrayList()

        //get ordinals and store them in  above  variable
        for (type in listOfReportTypes)
            originalOrdinals.add(type.ordinal)

        for (ord in  originalOrdinals)
            listOfReportTypes.add(mapOrdinalToInstance(ord + 4))
    }

    /****************************************************************
     * mapOrdinalToInstance -> creates a ReportType
     *@param ordinal :Int , the ordinal value of the ReportType
     *                          to be Created ,  % 7 is
     *                used to make sure this number falls
     *                in the range(0,7)
     *@return  Returns   ReportType variable
     * *****************************************************************/

    private fun mapOrdinalToInstance(ordinal : Int) : ReportType
    {
        val pure_ordinal = ordinal % 8
        return when(pure_ordinal) {
            0 ->{ ReportType.WEEKLY }
            1 ->{ ReportType.BIWEEKLY }
            2 ->{ ReportType.MONTHLY }
            3 ->{ ReportType.TRIMONTHLY }
            4 ->{ ReportType.WEEKLY_1 }
            5 ->{ ReportType.BIWEEKLY_1 }
            6->{ ReportType.MONTHLY_1 }
            else->{ ReportType.TRIMONTHLY_1 }
        }
    }

}