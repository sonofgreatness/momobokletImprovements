package com.example.momobooklet_by_sm.manageuser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.momobooklet_by_sm.databinding.FragmentBlankBinding

class LoginFragment : Fragment() {

private lateinit var _binding :FragmentBlankBinding
    private val binding get() = _binding!!
    // This property is only valid between onCreateView and
    // onDestroyView.


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        // Inflate the layout for this fragment
        _binding = FragmentBlankBinding.inflate(inflater, container, false)

        val view = binding.root
        binding.nextButton.setOnClickListener {
        }
        binding.registerBtn.setOnClickListener{
        }

        return view
        }


}