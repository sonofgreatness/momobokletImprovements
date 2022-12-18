package com.example.momobooklet_by_sm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.momobooklet_by_sm.ui.viewmodels.TransactionViewModel
import com.example.momobooklet_by_sm.ui.viewmodels.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


//@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

 lateinit var  mUserViewModel: UserViewModel
 lateinit var mTransactionViewModel: TransactionViewModel

 //   @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mTransactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        mUserViewModel =  ViewModelProvider(this).get(UserViewModel::class.java)

        setContentView(R.layout.activity_main)
        setUpBottomNav()

    }

    private fun setUpBottomNav() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
//set up bottom navigation with navcontroller
        val bottomnav:BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomnav.setupWithNavController(navController)
    }

}
