package com.example.momobooklet_by_sm.presentation.manageuser
import android.annotation.SuppressLint
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.UserViewModel
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.momobooklet_by_sm.data.local.models.TransactionModel
import com.example.momobooklet_by_sm.databinding.FragmentUserAccountsBinding
import com.example.momobooklet_by_sm.common.util.Constants.Companion.BASE_URL2
import android.os.StrictMode
import com.example.momobooklet_by_sm.presentation.ui.viewmodelProviderFactories.BackupViewModelProviderFactory
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.BackupViewModel
import java.util.*
import kotlin.collections.HashMap

class UserAccountsFragment : Fragment() {
    val policy: StrictMode.ThreadPolicy  = StrictMode.ThreadPolicy.Builder().permitAll().build()

    private val mUserViewModel: UserViewModel by lazy{
        ViewModelProvider(this)[UserViewModel::class.java]
    }
    private   lateinit var mBackUpViewModel: BackupViewModel
    private lateinit var _binding: FragmentUserAccountsBinding
    private val binding get() = _binding
    private lateinit var adapter : UserProfileRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StrictMode.setThreadPolicy(policy)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBackUpViewModel = ViewModelProvider(
            this,
            BackupViewModelProviderFactory(requireActivity().application)
        )[BackupViewModel::class.java]
        _binding = FragmentUserAccountsBinding.inflate(inflater, container, false)
        adapter = UserProfileRecyclerViewAdapter(mUserViewModel)

        // Set up the RecyclerView
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter

        ShowTransactions()
        setAddAccountListener()
        return binding.root
    }
    /**********************************
     *
     ************************************/
    private fun setAddAccountListener() {
        binding.addAccBtn.setOnClickListener {
             //ReadFromTransactionsDriveSheet()
            //writeToTransactionsDriveSheet()
            //readFromGoogleSheetTheRightWay()
        }
    }


    /*****************************************************************
     * Adds A record to `Transactions`  sheet in Google Drive Sheet
     *
     *
     ******************************************************************/
    private fun writeToTransactionsDriveSheet() {
        val url =
            "https://script.google.com/macros/s/AKfycbxL7N5NOR6Vm32pyPjLDE2wqFFDhKaEpEUhYCaUE1CkD9LJYR8Wq1zM6VH8V5LrEg2HoQ/exec"
        val stringRequest = object : StringRequest(Method.POST, url,
            Response.Listener {
                Log.d("Response", "${it}")
            }, Response.ErrorListener {

            }) {
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String, String>()
                params["TransactionId"] = "Poland"
                params["Date"] = "Quit"
                params["CustomerName"] = "AppCustomer"
                params["CustomerPin"] = "AppPin"
                params["TransactionType"] = "dummy"
                params["Amount"] = "80"
                params["Time"] = "Future"
                params["CustomerPhone"] = "7898824095"
                params["AgentPhoneNumber"] = "Agency"

                return params
            }
        }
        val queue = Volley.newRequestQueue(requireContext())
        queue.add(stringRequest)
        Toast.makeText(requireContext(), "Recorded", Toast.LENGTH_SHORT).show()
    }




    @SuppressLint("LogNotTimber")
    private fun readFromGoogleSheetTheRightWay() {
        mBackUpViewModel.importDataSet()
    }

    /*****************************************************************
     * Adds A record to `Transactions`  sheet in Google Drive Sheet
     *******************************************************************/
    private fun ReadFromTransactionsDriveSheet() {
        val transactionsList =  arrayListOf<TransactionModel>()
        val queue = Volley.newRequestQueue(requireContext())
        val url= BASE_URL2

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener {
                              val data = it.getJSONArray("transactionList")

                                Log.d("JsonData", "${data}")
                              for (i in 0 until data.length())
                              {
                                  val transactionJsonObject = data.getJSONObject(i)
                                  val transactionObject = TransactionModel(
                                      UUID.randomUUID().toString(),
                                      "Date",
                                      transactionJsonObject.get("customerName").toString() as String,
                                      transactionJsonObject.get("customerPin").toString() as String,
                                      transactionJsonObject.get("customerPhone").toString() as String,
                                      false,
                                      0.0F,
                                      ByteArray(0),
                                      transactionJsonObject.get("time") as String,
                                      transactionJsonObject.get("agentPhoneNumber").toString() as String
                                      )
                                  transactionsList.add(transactionObject)
                              }
                Log.d("List Of Transactions : ", "${transactionsList.size}")
            }, Response.ErrorListener {
                Toast.makeText(requireContext(),"$it", Toast.LENGTH_LONG).show()
            })
        {

        }
        queue.add(jsonObjectRequest)
    }
    /******************************************
     *populates recyclerview
     *
     *****************************************/
    private fun ShowTransactions() {
        mUserViewModel.readAllData.observe(viewLifecycleOwner) { users ->
            adapter.differ.submitList(users)
        }
    }

}