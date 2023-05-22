package com.example.momobooklet_by_sm

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.momobooklet_by_sm.domain.repositories.ConnectivityObserver
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity2 : AppCompatActivity() {

    val  mUserViewModel: UserViewModel by viewModels()
    @Inject
    lateinit var  connectivityObserver: ConnectivityObserver



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}