package com.example.momobooklet_by_sm.manageuser
import com.example.momobooklet_by_sm.ui.viewmodels.UserViewModel
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.databinding.FragmentUserAccountsBinding


class UserAccountsFragment : Fragment() {

    private val mUserViewModel: UserViewModel by lazy{
        ViewModelProvider(this)[UserViewModel::class.java]
    }
    private lateinit var _binding: FragmentUserAccountsBinding
    private val binding get() = _binding
    private lateinit var adapter : UserProfileRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserAccountsBinding.inflate(inflater, container, false)
        adapter = UserProfileRecyclerViewAdapter(mUserViewModel)

        // Set up the RecyclerView
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter

        ShowTransactions()
        setAddAccountListener()
        return binding.root
    }
    private fun setAddAccountListener() {
        binding.addAccBtn.setOnClickListener {
            it!!.findNavController()
                .navigate(R.id.action_userAccountsFragment_to_registerFragment)
        }
    }
    /*populates recyclerview*/
    private fun ShowTransactions() {
        mUserViewModel.readAllData.observe(viewLifecycleOwner) { users ->
            adapter.differ.submitList(users)
        }
    }
}