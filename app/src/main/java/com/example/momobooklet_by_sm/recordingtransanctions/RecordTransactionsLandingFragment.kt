package com.example.momobooklet_by_sm.recordingtransanctions

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.momobooklet_by_sm.databinding.FragmentRecordTransactionsLandingFragmentsBinding

class RecordTransactionsLandingFragment : Fragment() {
    private var binding: FragmentRecordTransactionsLandingFragmentsBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentRecordTransactionsLandingFragmentsBinding.inflate(inflater, container, false)
        binding!!.recordtransactBtn.setOnClickListener {
        }
        return binding!!.root
        //inflater.inflate(R.layout.fragment_record_transactions_landing_fragments, container, false);
    }
}