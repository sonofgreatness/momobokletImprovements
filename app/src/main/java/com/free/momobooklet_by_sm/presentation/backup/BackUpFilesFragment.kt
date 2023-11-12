package com.free.momobooklet_by_sm.presentation.backup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.free.momobooklet_by_sm.databinding.FragmentBackUpFilesBinding


class BackUpFilesFragment : Fragment() {

    private lateinit var _binding : FragmentBackUpFilesBinding
    private  val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentBackUpFilesBinding.inflate(inflater, container, false)
        return binding.root
    }
}