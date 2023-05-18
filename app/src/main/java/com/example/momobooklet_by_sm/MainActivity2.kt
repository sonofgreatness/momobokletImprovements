package com.example.momobooklet_by_sm

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.momobooklet_by_sm.common.util.classes.DrawableSpan
import com.example.momobooklet_by_sm.common.util.classes.events.networkEvent
import com.example.momobooklet_by_sm.common.util.classes.NetworkChangeListener
import com.example.momobooklet_by_sm.databinding.ActivityMain2Binding
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.UserViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class MainActivity2 : AppCompatActivity() {

    private lateinit var drawableSpan: DrawableSpan
    private lateinit var binding: ActivityMain2Binding
    private var passwordEndIconChangeHelper = true
    private var mBundle: Bundle = Bundle()
    val  mUserViewModel: UserViewModel by viewModels()
    var myIsConnected: Boolean = false

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)

        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(NetworkChangeListener(), intentFilter)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
    }
    @Subscribe
    fun makeToast(networkEvent: networkEvent) {
        myIsConnected = networkEvent.isnetworkConnected
    }
    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}