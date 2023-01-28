package com.example.momobooklet_by_sm.util.classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
* This is an event , the internet is either on
* or off
*/

public class InternetConnectivityMonitor {
    /**
     * isConnected reflects state of connection
     *      true -> device is connected to the internet
     *      false -> device is not connected to the internet
     */
    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager!= null){
            NetworkInfo[] info =  connectivityManager.getAllNetworkInfo();
            if (info!=null){
                for (NetworkInfo networkInfo : info) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
