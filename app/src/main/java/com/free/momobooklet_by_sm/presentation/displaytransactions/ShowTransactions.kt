package com.free.momobooklet_by_sm.presentation.displaytransactions


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.free.momobooklet_by_sm.BackUpActivity
import com.free.momobooklet_by_sm.presentation.help.HelpActivity
import com.free.momobooklet_by_sm.MainActivity
import com.free.momobooklet_by_sm.R
import com.free.momobooklet_by_sm.data.local.models.UserModel
import com.free.momobooklet_by_sm.databinding.FragmentShowTransactionsBinding
import com.free.momobooklet_by_sm.domain.workers.remote.transactions.TransactionBackupManager
import com.free.momobooklet_by_sm.presentation.ui.viewmodels.BackupViewModel
import com.free.momobooklet_by_sm.presentation.ui.viewmodels.TransactionViewModel
import com.free.momobooklet_by_sm.presentation.ui.viewmodels.UserViewModel
import timber.log.Timber
import java.io.File
import java.io.IOException


class ShowTransactions : Fragment() , SearchView.OnQueryTextListener  {
    private lateinit var adapter: ListAdapter
    private lateinit var _binding: FragmentShowTransactionsBinding
    private val binding get() = _binding
    private lateinit var mTransactionViewModel: TransactionViewModel
    private lateinit var  mBackupViewModel: BackupViewModel
    private  lateinit var  mUserViewModel: UserViewModel
    private var mainUser: UserModel? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentShowTransactionsBinding.inflate(inflater, container, false)
        //inflate menu
        mUserViewModel = (activity as MainActivity).mUserViewModel
        mTransactionViewModel = (activity as MainActivity).mTransactionViewModel
        mBackupViewModel  = (activity as MainActivity).mBackupViewModel
        setmainUser()
        setupToolbar()
        adapter =  ListAdapter(null,requireActivity().application)
        setupRecyclerView()
        getAllTransaction()
        val view = binding.root
        return view
    }


    /*************************************************************************
     * Gets UserModel with IsIncontrol value == TRUE
     *****************************************************************************/
    private fun setmainUser() {

        try {

            mUserViewModel.setActiveUsers()
            mainUser = mUserViewModel.userInControl.value?.get(0)
        }
        catch (ex:Exception)
        {
            Log.d("No Active User", "${ex.message}")
        }
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
                R.id.menu_help_records->{
                    move_to_help()
                    return@setOnMenuItemClickListener true
                }
                R.id.menu_backup_manager->{
                     move_disaster_recovery()
                    return@setOnMenuItemClickListener true
                }
                R.id.search->{
                    Timber.d("perform search")
                    return@setOnMenuItemClickListener true
                }
                R.id.sync->{
                    showAboutToDownloadMessage()
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    return@setOnMenuItemClickListener true}
            }
        }
        val searchView = toolbar.menu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(this)
    }



    private fun move_to_help() {
        val intent = Intent(requireContext(), HelpActivity::class.java)
        startActivity(intent)
    }


    private fun move_disaster_recovery(){

        val intent = Intent(this.requireContext(),BackUpActivity::class.java)
        startActivity(intent)
    }

   /**************************************************
    * Alerts User they are about to perform          *
    *        a potentially long  network operation   *
    **************************************************/
    private fun showAboutToDownloadMessage() {
       AlertDialog.Builder(requireContext())
           .setTitle(resources.getString(R.string.download_warning))
           .setIcon(resources.getDrawable(R.drawable.ic_baseline_warning_24))
           .setMessage(resources.getString(R.string.download_warning_body))
           .setNegativeButton("Abort"){_, _ ->
           }
           .setPositiveButton("Continue") { _, _ ->

               TransactionBackupManager(requireContext().applicationContext).downloadTransactionData()

           }
           .show()
    }


    /******************************************************
     * Sets recyclerview adapters                         *
     *                  Header adapter for the Titles     *
     *                  adapter for the the list elements *
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
        mTransactionViewModel._searchResults.observe(viewLifecycleOwner) {
            adapter.differ.submitList(it)
        }
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


    /*******************************************************************
     *onQueryTextChange  , Does Nothing,
     * * Wait for user To submit Search*
    *******************************************************************/
    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    /*******************************************************************
     *showCustomToast - informs user when a search term                *
     *                  returns no results                             *
     *******************************************************************/

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
}




