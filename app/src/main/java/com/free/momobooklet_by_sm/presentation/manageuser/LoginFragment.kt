package com.free.momobooklet_by_sm.presentation.manageuser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.free.momobooklet_by_sm.databinding.FragmentBlankBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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

        binding.nextButton.setOnClickListener {}
        binding.registerBtn.setOnClickListener{}
        couritines()
        return view
        }

    private fun couritines() {
        GlobalScope.launch {
            val job: Job =  launch {

            }
            job.join()
                
        }
    }
}