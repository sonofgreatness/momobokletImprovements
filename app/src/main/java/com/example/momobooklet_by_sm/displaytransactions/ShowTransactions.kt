package com.example.momobooklet_by_sm.displaytransactions


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.databinding.FragmentShowTransactionsBinding
import com.example.momobooklet_by_sm.ui.viewmodels.TransactionViewModel
import timber.log.Timber


class ShowTransactions : Fragment(){


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
         binding.toolbarShowTransactions.inflateMenu(R.menu.show_transactions_menu)


        val view = binding.root
        val headerAdapter = HeaderAdapter()

        val concatAdapter = ConcatAdapter(headerAdapter, adapter)
        val recyclerView = binding.recyclerViewShowtransactions
        recyclerView.adapter = concatAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
         mTransactionViewModel=ViewModelProvider(this)[TransactionViewModel::class.java]

            Timber.e("transactionViewModel->${mTransactionViewModel}")
         getAllTransaction()

        return view
    }



private fun getAllTransaction(){
// Start coroutine to move  db accesss off main thread

    mTransactionViewModel.getAllTransaction()
    mTransactionViewModel._searchResults.observe(viewLifecycleOwner,Observer{
        adapter.differ.submitList(it)
        Timber.e("tranactionFragment->$it")
    })
}



    }




