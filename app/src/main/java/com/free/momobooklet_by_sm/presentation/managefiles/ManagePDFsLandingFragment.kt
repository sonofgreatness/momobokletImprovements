package com.free.momobooklet_by_sm.presentation.managefiles

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.free.momobooklet_by_sm.R
import com.free.momobooklet_by_sm.databinding.FilesBackdropBinding
import com.free.momobooklet_by_sm.databinding.FragmentManagePDFsLandingBinding
import com.free.momobooklet_by_sm.MainActivity
import com.free.momobooklet_by_sm.presentation.managefiles.staggeredgridlayout.ReportsAdapter
import com.free.momobooklet_by_sm.common.util.classes.ReportType

class ManagePDFsLandingFragment : Fragment() {
    private lateinit var _binding: FragmentManagePDFsLandingBinding
    private  val  binding get()= _binding
    private  lateinit var myAdapter: ReportsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentManagePDFsLandingBinding.inflate(inflater, container, false)
        myAdapter = ReportsAdapter(activity as MainActivity)
        setUpToolBar()
        setUpRecyclerView()



        // Set cut corner background for API 23+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.productGrid.background = context?.getDrawable(R.drawable.clipped_corner_background)
        }

        setupOnClickListeners()

        return binding.root
    }



    private fun setupOnClickListeners() {
        binding.backdrop.generateReportBtn.setOnClickListener{
           it.findNavController().navigate(R.id.action_managePDFsLandingFragment2_to_generateReportFile)
        }
        binding.backdrop.showAllreportsBtn.setOnClickListener{
            setRecyclerViewsListToALL()
        }

        binding.backdrop.showFilteredreportsBtn.setOnClickListener{
            setRecyclerViewsListToALL()
            setRecyclerViewsListToCustomFilters()
        }
    }




    private fun setUpRecyclerView() {
       val  list = getListofInternalFiles()

        binding.recyclerView.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(context, 2, RecyclerView.HORIZONTAL, false)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position % 3 == 2) 2 else 1
            }
        }
        binding.recyclerView.layoutManager = gridLayoutManager
        myAdapter.setData(list)
        binding.recyclerView.adapter = myAdapter
        val largePadding = resources.getDimensionPixelSize(R.dimen.staggered_report_grid_spacing_large)
        val smallPadding = resources.getDimensionPixelSize(R.dimen.staggered_report_grid_spacing_small)
        binding.recyclerView.addItemDecoration(ReportGridItemDecoration(largePadding, smallPadding))
    }

    /*********************************************************
     * getListofInternalFiles()  gets List of filename in
     *  internal memory
     *****************************************************/
    @SuppressLint("LogNotTimber")
    private fun getListofInternalFiles(): MutableList<String> {

        val returnList : MutableList<String> = ArrayList()
         val  contaminatedList = requireActivity().application.fileList()
         for(i in contaminatedList.indices)
         {
             Log.d("contaminated List", "${contaminatedList[i]}")
            if( contaminatedList[i].contains(".csv",true))
                returnList.add(contaminatedList[i])
            if(contaminatedList[i].contains(".pdf",true))
                returnList.add(contaminatedList[i])
         }

        return  returnList
    }

    private fun setUpToolBar()
    {
        // Set up the tool bar
        (activity as AppCompatActivity).setSupportActionBar(binding.appBar)
        binding.appBar.setNavigationOnClickListener(NavigationIconClickListener(
            requireActivity(),
            binding.productGrid,
            AccelerateDecelerateInterpolator(),
            ContextCompat.getDrawable(requireContext(), R.drawable.logo_navigation_icon), // Menu open icon
            ContextCompat.getDrawable(requireContext(), R.drawable.logo_navigation_clear))) // Menu close icon
    }




    /***************************************************
     * get list of csv and pdf files in internal memory
     *    and set them to recyclerView adapter
     *************************************************/
    private fun setRecyclerViewsListToALL() {
        myAdapter.setData(getListofInternalFiles())
        binding.recyclerView.adapter= myAdapter
        Toast.makeText(requireContext(), "List Updated", Toast.LENGTH_SHORT).show()
    }


    /***********************************************************************
     *setRecyclerViewsListToCustomFilters() - checks which checkBoxes are
     *            checked and update adapter accordingly
     **********************************************************************/
    private fun setRecyclerViewsListToCustomFilters() {
        checkifBothCSVandPDFareChecked()
        checkRestofCheckBoxes()
        binding.recyclerView.adapter = myAdapter
        Toast.makeText(requireContext(), "List Updated", Toast.LENGTH_SHORT).show()
    }


    /*******************************************************************************
     * checkifBothCSVandPDFareChecked() -> if both pdf and csv
     *   checkboxes are checked then there is no need to filer list
     *   by either of them , but if One of them is then filter by the checked
     *   Checkbox
     *****************************************************************************/
    private fun checkifBothCSVandPDFareChecked() {
        binding.backdrop.apply {
            if (csvCheckbox.isChecked && pdfCheckbox.isChecked)
                myAdapter.setData(getListofInternalFiles())

            else {

                if (csvCheckbox.isChecked)
                    myAdapter.filterByCSV()
                if (pdfCheckbox.isChecked)
                    myAdapter.filterByPDF()
            }
        }
    }


    /**************************************************************
     * checkRestofCheckBoxes() -> If none of the checkBoxes
     *   [WEEKLY,BIWEEKLY ,MONTHLY, 3MONTHS] is checked the the
     *   adapter is set with list of all files   otherwise
     *    filter adapter by checked Checkbox
     ****************************************************************/
    private fun checkRestofCheckBoxes() {
        val firscondition = checkIfNoneofPeriodicCheckboxesisChecked()
         if (firscondition)
         {
             // find the checked CheckBoxes and filter by them
             findCheckedCheckBoxesAndFilterByThem()
         }
    }




    /** ********************************************************************
     * checkIfNoneofPeriodicCheckboxesisChecked() -> returns true if
     *      atleast one  of   [WEEKLY,BIWEEKLY ,MONTHLY, 3MONTHS] is
     *      checked
     *@return  Returns a boolean
    *************************************************************************/
    private fun checkIfNoneofPeriodicCheckboxesisChecked(): Boolean {
        binding.backdrop.apply{

            return (monthlyCheckbox.isChecked || trimonthlyCheckbox.isChecked
                    || weeklyCheckbox.isChecked || biweeklyCheckbox.isChecked)
        }
    }


    /*****************************************************************
     *findCheckedCheckBoxesAndFilterByThem()
     *        filters list of files by  Checked   CheckBoxes
     ***************************************************************/
    private fun findCheckedCheckBoxesAndFilterByThem() {
        binding.backdrop.apply{
          myAdapter.filterByReportTypeList(collectListofReportTypes())
        }
    }

    /***********************************************************************
     * collectlistofReportTypes() - collects list of ReportTypes according
     *            to which CheckBoxes are checked    i.e if a WEEKLY CheckBox is checked
     *            a WEEKLY ReportType is added to the list to be returned
     * @return Returns  a  Mutable list of ReportTypes
     **********************************************************/
    private fun collectListofReportTypes():MutableList<ReportType>
    {
        val myreturnList: MutableList<ReportType> = ArrayList()
        binding.backdrop.apply{
            fu(myreturnList)
            return myreturnList
        }
    }

    private fun FilesBackdropBinding.fu(myreturnList: MutableList<ReportType>) {
        if (weeklyCheckbox.isChecked)
            myreturnList.add(ReportType.WEEKLY)
        if (biweeklyCheckbox.isChecked)
            myreturnList.add(ReportType.BIWEEKLY)
        if (monthlyCheckbox.isChecked)
            myreturnList.add(ReportType.MONTHLY)
        if (trimonthlyCheckbox.isChecked)
            myreturnList.add(ReportType.TRIMONTHLY)
    }
}