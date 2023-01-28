package com.example.momobooklet_by_sm.displaytransactions


import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.databinding.FragmentShowTransactionsBinding
import com.example.momobooklet_by_sm.ui.viewmodels.TransactionViewModel
import timber.log.Timber


class ShowTransactions : Fragment() , SearchView.OnQueryTextListener  {
    private val adapter = ListAdapter()
    private lateinit var _binding: FragmentShowTransactionsBinding
    private val binding get() = _binding
    private lateinit var mTransactionViewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentShowTransactionsBinding.inflate(inflater, container, false)
        //inflate menu
        mTransactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        setupToolbar()
        setupRecyclerView()
        getAllTransaction()
        val view = binding.root
        return view
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

        val layout = layoutInflater.inflate(
            R.layout.custom_toast_layout, null
        )
        //make a toast
        val toast: Toast = Toast(requireContext())
        toast.apply {
            view = layout
            setGravity(Gravity.CENTER, 0, 1)
            duration = Toast.LENGTH_SHORT
            view = layout
            show()
        }
    }


}




