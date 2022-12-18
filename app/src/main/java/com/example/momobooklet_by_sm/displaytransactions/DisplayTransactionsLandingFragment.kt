package com.example.momobooklet_by_sm.displaytransactions

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.databinding.FragmentDisplayTransactionsLandingBinding


class DisplayTransactionsLandingFragment : Fragment() {
    private lateinit var _binding: FragmentDisplayTransactionsLandingBinding
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDisplayTransactionsLandingBinding.inflate(inflater, container, false)
        binding.showTransactionsDoorBtn.setOnClickListener {

        }


        //  binding.recordsShowroomImageview.getBackground()

/*val animDrawable =binding.recordsShowroomImageview.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

  */

        return binding.root
    }
}