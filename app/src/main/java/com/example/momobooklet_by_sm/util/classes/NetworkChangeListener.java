package com.example.momobooklet_by_sm.util.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.momobooklet_by_sm.util.classes.Events.networkEvent;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;
/**
*listens for change in connectivity event (InternetConnectivityMonitor)
**/
public class NetworkChangeListener  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (InternetConnectivityMonitor.isConnectedToInternet(context)) {
            EventBus.getDefault().post(new networkEvent(true));
        }
        else
        {
            EventBus.getDefault().post(new networkEvent(false));
        }
    }
}
