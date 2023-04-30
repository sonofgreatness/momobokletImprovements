package com.example.momobooklet_by_sm

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.momobooklet_by_sm.databinding.ActivityMain2Binding
import com.example.momobooklet_by_sm.ui.viewmodels.UserViewModel
import com.example.momobooklet_by_sm.util.classes.DrawableSpan
import com.example.momobooklet_by_sm.util.classes.Events.networkEvent
import com.example.momobooklet_by_sm.util.classes.NetworkChangeListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class MainActivity2 : AppCompatActivity() {

    private lateinit var drawableSpan: DrawableSpan
    private lateinit var binding: ActivityMain2Binding
    private var passwordEndIconChangeHelper = true
    private var mBundle: Bundle = Bundle()
    lateinit var mUserViewModel: UserViewModel
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
        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]
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