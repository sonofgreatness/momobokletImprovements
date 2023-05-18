package com.example.momobooklet_by_sm.common.util.classes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.momobooklet_by_sm.common.util.classes.events.networkEvent
import org.greenrobot.eventbus.EventBus

/**
 * listens for change in connectivity event (InternetConnectivityMonitor)
 */
class NetworkChangeListener : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (com.example.momobooklet_by_sm.common.util.classes.InternetConnectivityMonitor.isConnectedToInternet(
                context
            )
        ) {
            EventBus.getDefault().post(networkEvent(true))
        } else {
            EventBus.getDefault().post(networkEvent(false))
        }
    }
}