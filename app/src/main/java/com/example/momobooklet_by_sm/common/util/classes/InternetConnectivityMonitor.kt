package com.example.momobooklet_by_sm.common.util.classes

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * This is an event , the internet is either on
 * or off
 */
object InternetConnectivityMonitor {
    /********************************************************
     * isConnected reflects state of connection
     * true -> device is connected to the internet
     * false -> device is not connected to the internet
     **********************************************************/
    fun isConnectedToInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val info = connectivityManager.allNetworkInfo
            if (info != null) {
                for (networkInfo in info) {
                    if (networkInfo.state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
        }
        return false
    }
}