package com.example.momobooklet_by_sm.recordingtransanctions

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.momobooklet_by_sm.R
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


        binding!!.recordtransactDoorBtn.setOnClickListener {
val intent:Intent =Intent(context, RecordTransacts::class.java)
            startActivity(intent)

        }
        return binding!!.root
        //inflater.inflate(R.layout.fragment_record_transactions_landing_fragments, container, false);
    }
}