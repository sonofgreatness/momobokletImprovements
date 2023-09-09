package com.example.momobooklet_by_sm.presentation.manageuser
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.momobooklet_by_sm.MainActivity
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.common.util.Constants.Companion.BASE_URL2
import com.example.momobooklet_by_sm.common.util.Constants.Companion.REGISTRATION_HOME_KEY
import com.example.momobooklet_by_sm.data.local.models.TransactionModel
import com.example.momobooklet_by_sm.databinding.FragmentUserAccountsBinding
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.BackupViewModel
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.UserViewModel
import java.util.*

class UserAccountsFragment : Fragment() {
    private val policy: StrictMode.ThreadPolicy  = StrictMode.ThreadPolicy.Builder().permitAll().build()

    private val mUserViewModel: UserViewModel by lazy{
        (activity as MainActivity).mUserViewModel
    }
    private   lateinit var mBackUpViewModel: BackupViewModel
    private lateinit var _binding: FragmentUserAccountsBinding
    private val binding get() = _binding
    private lateinit var adapter : UserProfileRecyclerViewAdapter
    private var swipeBackground : ColorDrawable  =
                                 ColorDrawable(Color.parseColor("#DE3163"))
    private lateinit var swipeDeleteIcon : Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StrictMode.setThreadPolicy(policy)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBackUpViewModel = (activity as MainActivity).mBackupViewModel
        _binding = FragmentUserAccountsBinding.inflate(inflater, container, false)
        adapter = UserProfileRecyclerViewAdapter(mUserViewModel)
        swipeDeleteIcon =  ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_delete_24_white)!!
        // Set up the RecyclerView
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter

        ShowTransactions()
        setAddAccountListener()
        setUpSwipeGestureControl(recyclerView)

        return binding.root
    }

    private fun setUpSwipeGestureControl(recyclerView: RecyclerView) {


        val itemTouchHelperCallBack = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
                adapter.removeItem(viewHolder as UserProfileRecyclerViewAdapter.MyViewHolderr)
            }

            override fun onChildDrawOver(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder?,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {


                val itemView = viewHolder!!.itemView
                val iconMargin = (itemView.height - swipeDeleteIcon!!.intrinsicHeight) / 2

                if (dX > 0) {

                    swipeBackground.setBounds(
                        itemView.left,
                        itemView.top,
                        dX.toInt(),
                        itemView.bottom
                    )
                    swipeDeleteIcon.setBounds(
                        itemView.left + iconMargin, itemView.top + iconMargin,
                        itemView.left + iconMargin + swipeDeleteIcon.intrinsicWidth,
                        itemView.bottom - iconMargin
                    )
                } else {
                    swipeBackground.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )

                    swipeDeleteIcon.setBounds(
                        itemView.right - iconMargin - swipeDeleteIcon.intrinsicWidth,
                        itemView.top + iconMargin,
                        itemView.right - iconMargin,
                        itemView.bottom - iconMargin
                    )

                }
                swipeBackground.draw(canvas)
                canvas.save()

                if (dX > 0)
                    canvas.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                else
                    canvas.clipRect(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )

                swipeDeleteIcon.draw(canvas)
                canvas.restore()

                super.onChildDrawOver(
                    canvas,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    /**********************************
     *
     ************************************/
    private fun setAddAccountListener() {
        binding.addAccBtn.setOnClickListener {
            val mBundle:Bundle = Bundle()
            mBundle.putString(REGISTRATION_HOME_KEY, "add_accounts")

            it.findNavController().navigate(R.id.action_userAccountsFragment_to_registerFragment,mBundle)
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