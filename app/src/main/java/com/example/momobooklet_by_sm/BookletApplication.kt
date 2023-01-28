package com.example.momobooklet_by_sm

import android.app.Application
import timber.log.Timber


class BookletApplication :Application(){



    init {

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }






}
