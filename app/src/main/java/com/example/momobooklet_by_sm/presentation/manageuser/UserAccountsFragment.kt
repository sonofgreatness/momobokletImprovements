package com.example.momobooklet_by_sm.presentation.manageuser
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.momobooklet_by_sm.MainActivity
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.common.util.Constants.Companion.REGISTRATION_HOME_KEY
import com.example.momobooklet_by_sm.databinding.FragmentUserAccountsBinding
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.BackupViewModel
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.UserViewModel

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

        adapter = UserProfileRecyclerViewAdapter(mUserViewModel,this)

        swipeDeleteIcon =  ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_delete_24_white)!!

        // Set up the RecyclerView
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter

        showTransactions()
        setAddAccountListener()
        setUpSwipeGestureControl(recyclerView)

        return binding.root
    }
    /*************************************************************************
     *sets Up deletion of UserModel record  in db   on item swipe
     * in recyclerview
    ***********************************************************************/
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
                val iconMargin = (itemView.height - swipeDeleteIcon.intrinsicHeight) / 2

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
            val mBundle = Bundle()
            mBundle.putString(REGISTRATION_HOME_KEY, "add_accounts")

            it.findNavController().navigate(R.id.action_userAccountsFragment_to_registerFragment,mBundle)
        }
    }



    /******************************************
     *populates recyclerview
     *****************************************/
    private fun showTransactions() {
        mUserViewModel.readAllData.observe(viewLifecycleOwner) { users ->
            adapter.differ.submitList(users)
        }
    }

}