package com.example.momobooklet_by_sm.displaytransactions

import android.content.Intent
import android.os.Bundle
import android.os.Build
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.momobooklet_by_sm.MainActivity
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.database.ViewModel.TransactionViewModel
import com.example.momobooklet_by_sm.databinding.FragmentShowTransactionsBinding


class ShowTransactions : Fragment() {
    private var checker:Int=0
private lateinit var mTransactiViewModel:TransactionViewModel
    private lateinit var _binding:FragmentShowTransactionsBinding
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
_binding= FragmentShowTransactionsBinding.inflate(inflater,container,false)
binding.toolbarShowTransactions.inflateMenu(R.menu.show_transactions_menu)


        val headerAdapter = HeaderAdapter()
        val adapter=ListAdapter()
        val concatAdapter = ConcatAdapter(headerAdapter, adapter)
        val recyclerView =binding.recyclerViewShowtransactions
        recyclerView.adapter=concatAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

mTransactiViewModel=ViewModelProvider(this).get(TransactionViewModel::class.java)


        when(checker){

0->

{     mTransactiViewModel.readAllTransactiondata.observe(viewLifecycleOwner, Observer { transaction ->
    adapter.setData(transaction)
})
}



            1->{

                mTransactiViewModel.readAllTransactiondata_bydate.observe(viewLifecycleOwner, Observer { transaction ->
                    adapter.setData(transaction)
            })}
            2->{




            }
            3->run {}
            4->{}

        }

            binding.toolbarShowTransactions.setNavigationOnClickListener(View.OnClickListener {




                val intent: Intent=Intent(activity,MainActivity::class.java)
                startActivity(intent)
            })



        val view = binding.root



        // set Title for toolbar

        return view

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
when(item.itemId){
    R.id.sort_date->{
        //read all transactions should return list from query with sort by  date condition
        checker=1
        return true
    }

R.id.sort_amount->{

    checker=2
    return true}

    R.id.sort_type->{

        checker=3
        return true
    }

    R.id.sort_amount->{
        checker = 4
        return true}
    R.id.sort_name->{
        checker =5
        return true}
}
        return super.onOptionsItemSelected(item)  }


}