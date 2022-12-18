package com.example.momobooklet_by_sm

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.example.momobooklet_by_sm.ui.viewmodels.TransactionViewModel
import com.example.momobooklet_by_sm.ui.viewmodels.UserViewModel
import timber.log.Timber

class BookletApplication :Application(){



    init {

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }






}
