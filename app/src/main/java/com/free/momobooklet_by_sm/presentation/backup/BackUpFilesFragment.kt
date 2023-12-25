package com.free.momobooklet_by_sm.presentation.backup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.free.momobooklet_by_sm.BackUpActivity
import com.free.momobooklet_by_sm.databinding.FragmentBackUpFilesBinding
import com.free.momobooklet_by_sm.presentation.displaytransactions.ListAdapter
import com.free.momobooklet_by_sm.presentation.ui.viewmodels.BackupDataBaseViewModel
import timber.log.Timber


class BackUpFilesFragment : Fragment() {

    private lateinit var adapter: BackupDetailsAdapter
    private lateinit var _binding : FragmentBackUpFilesBinding
    private  val binding get() = _binding
    private  lateinit var  mBackupDataBaseViewModel: BackupDataBaseViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        // Inflate the layout for this fragment
        _binding = FragmentBackUpFilesBinding.inflate(inflater, container, false)
        mBackupDataBaseViewModel = (activity as BackUpActivity).mBackUpDatabaseViewModel

        adapter = BackupDetailsAdapter(requireActivity().application, mBackupDataBaseViewModel,"71000000")
        getBackupDetails()
        setUpRecyclerView()
        return binding.root
    }

    /**
     *gets list<BackupDetails> , submits it
     *    to addpter
     **/
    private fun getBackupDetails() {
        adapter.differ.submitList(
        mBackupDataBaseViewModel.getListOfBackupDetails())

    }


    private fun setUpRecyclerView() {

        binding.recyclerViewBackups
                      .adapter = adapter

        binding.recyclerViewBackups
                     .layoutManager = LinearLayoutManager(requireContext())
    }


}