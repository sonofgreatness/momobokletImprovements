package com.example.momobooklet_by_sm.presentation.displaytransactions


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.momobooklet_by_sm.MainActivity
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.databinding.FragmentShowTransactionsBinding
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.BackupViewModel
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.TransactionViewModel
import java.io.File
import java.io.IOException


class ShowTransactions : Fragment() , SearchView.OnQueryTextListener  {
    private lateinit var adapter: ListAdapter
    private lateinit var _binding: FragmentShowTransactionsBinding
    private val binding get() = _binding
    private lateinit var mTransactionViewModel: TransactionViewModel
    private lateinit var  mBackupViewModel: BackupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentShowTransactionsBinding.inflate(inflater, container, false)
        //inflate menu
        mTransactionViewModel = (activity as MainActivity).mTransactionViewModel
        mBackupViewModel  = (activity as MainActivity).mBackupViewModel

        setupToolbar()
        adapter =  ListAdapter(null,requireActivity().application)
        setupRecyclerView()
        getAllTransaction()
        /*
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                val getpermission = Intent()
                getpermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivity(getpermission)
            }
            checkPermissionSMS()
        }*/
        val view = binding.root
        return view
    }


    @Throws(IOException::class)
    fun getFileFromAssets(context: Context, fileName: String): File = File(context.cacheDir, fileName)
        .also {
            if (!it.exists()) {
                it.outputStream().use { cache ->
                    context.assets.open(fileName).use { inputStream ->
                        inputStream.copyTo(cache)
                    }
                }
            }
        }
    /***************************************************************
     * Sets up Toolbar
     *              inflates menu with R.menu.show_transactions_menu
     *              Sets ToolBar's  searchview  OnQueryTextListener
     *                          to One Implemented by this Class
     *****************************************************************/
    private fun setupToolbar() {

         binding.toolbarShowTransactions.inflateMenu(R.menu.show_transactions_menu)
          val toolbar = binding.toolbarShowTransactions
        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.search->{TODO()}
                R.id.sync->{
                    Toast.makeText(requireContext(),"Sync", Toast.LENGTH_SHORT).show()
                    mBackupViewModel.importDataSet()
                return@setOnMenuItemClickListener true
                }
                else -> {
                    return@setOnMenuItemClickListener true}
            }
        }
        val searchView = toolbar.menu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(this)
    }

    /************************************************
     * Sets recyclerview adapters
     *                  Header adapter for the Titles
     *                  adapter for the the list elements
     *****************************************************/
    private fun setupRecyclerView() {
        val headerAdapter = HeaderAdapter()
        val concatAdapter = ConcatAdapter(headerAdapter, adapter)
        val recyclerView = binding.recyclerViewShowtransactions
        recyclerView.adapter = concatAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }


    /**************************************************************
     * Sets recyclerView list to ViewModel's _searchResults variable
     ***************************************************************/
    private fun getAllTransaction() {
        // Start coroutine to move  db accesss off main thread
        mTransactionViewModel.getAllTransaction()
        mTransactionViewModel._searchResults.observe(viewLifecycleOwner, Observer {
            adapter.differ.submitList(it)
        })
    }




     /******************************************************************************
      *onQueryTextSubmit(query: String?) -> Implements SearchView.onQueryTextSubmit
      *     -> Performs Performs FTS in DB TransactionModels
      * @param query : String , text to match using FTS
      * @return boolean returned is of no use
      *            Updates recycler view with search results
      *****************************************************************************/
    override fun onQueryTextSubmit(query: String?): Boolean {
    mTransactionViewModel.searchTransactions(query!!)
         adapter = ListAdapter(query,requireActivity().application)
         setupRecyclerView()
       mTransactionViewModel._triggerNoResultsToast.observe(viewLifecycleOwner)
       {
           if(it)
               showCustomToast()
       }
            return false
    }


    /******************************************************************
    * onQueryTextChange  , Does Nothing, Wait for user To submit Search
    ********************************************************************/
    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    /**
     *showCustomToast - informs user when a search term
     *                  returns no results
     */

    private fun showCustomToast() {
        val layout = layoutInflater.
                     inflate(
                       R.layout.custom_toast_layout, null
                        )

        val toast: Toast = Toast(requireContext())
        toast.apply {
            view = layout
            setGravity(Gravity.CENTER, 0, 0)
            duration = Toast.LENGTH_SHORT
            view = layout
            show()
        }
    }


    private fun checkPermissionSMS() {

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
            {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            }
            else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    43
                )
            }
        } else {
            // Permission has already been granted
            //mTransactionViewModel.exportTransactionsToPdf()
        }

    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {


        if (requestCode == 43)
        {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted, yay!
               // mTransactionViewModel.exportTransactionsToPdf()
            } else {
                // permission denied, boo! Disable the
                // functionality
            }
            return

        }
    }

}




