package com.example.momobooklet_by_sm.manageuser


import com.example.momobooklet_by_sm.ui.viewmodels.UserViewModel
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.example.momobooklet_by_sm.database.models.UserModel
import java.util.ArrayList
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.momobooklet_by_sm.manageuser.UserProfileRecyclerViewAdapter
import androidx.lifecycle.ViewModelProvider
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ConcatAdapter
import com.example.momobooklet_by_sm.databinding.FragmentUserAccountsBinding
import com.example.momobooklet_by_sm.displaytransactions.HeaderAdapter
import com.example.momobooklet_by_sm.displaytransactions.ShowTransactions
import com.example.momobooklet_by_sm.ui.viewmodels.TransactionViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import timber.log.Timber

class UserAccountsFragment : Fragment() {
    private lateinit var mUserViewModel: UserViewModel
    private lateinit var _binding: FragmentUserAccountsBinding
    private val binding get() = _binding



    private val adapter = UserProfileRecyclerViewAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentUserAccountsBinding.inflate(inflater, container, false)

        // Set up the RecyclerView
        val recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
        val headerAdapter = HeaderAdapter()

        val concatAdapter = ConcatAdapter(headerAdapter, adapter)
        recyclerView.adapter = concatAdapter
        // add dummy data in database
        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        mUserViewModel._readAllData.observe(viewLifecycleOwner) { users ->
            adapter.differ.submitList(users)

            Timber.e("usersfragments->$users")
            Timber.e("listofusers->${adapter.differ.currentList}")
        }


Timber.e("userVieModel->${mUserViewModel}")


        ShowTransactions()

        return binding.root
    }

    private fun AddUsers() {
        val object1 = UserModel("ABC MOBILE MONEY", "6", "hello@gmail.com", "pass1", true)
        val object2 = UserModel("ABC MOBILE MONEY", "76911464", "hello@gmail.com", "pass1", false)
        //   mUserViewModel.addUser(object1)
        // mUserViewModel.addUser(object2)
        val list :List<UserModel> = listOf(object1,object2)


        adapter.differ.submitList(list)
        Toast.makeText(context, "users added", Toast.LENGTH_SHORT).show()
    }

    private fun ShowTransactions() {


        mUserViewModel._readAllData.observe(viewLifecycleOwner) { users ->
            adapter.differ.submitList(users)

            Timber.e("usersfragments->$users")
            Timber.e("listofusers->${adapter.differ.currentList}")
        }

        Toast.makeText(context, " added", Toast.LENGTH_SHORT).show()
    }
}