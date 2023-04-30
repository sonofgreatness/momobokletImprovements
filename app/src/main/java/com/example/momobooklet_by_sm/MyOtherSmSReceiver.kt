package com.example.momobooklet_by_sm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Build
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast

class MyOtherSmSReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Get the SMS message.
        val bundle = intent.extras
        val msgs: Array<SmsMessage?>
        var strMessage = ""
        val format = bundle!!.getString("format")

        // Retrieve the SMS message received.
        val pdus = bundle[pdu_type] as Array<Any>?
        if (pdus != null) {
            // Check the Android version.
            val isVersionM = Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.M

            // Fill the msgs array.
            msgs = arrayOfNulls(pdus.size)
            for (i in msgs.indices) {
                // Check Android version and use appropriate createFromPdu.
                if (isVersionM) {
                    // If Android version M or newer:
                    msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray, format)
                } else {   // If Android version L or older:
                    msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                }
                // Build the message to show.
                strMessage += "SMS from " + msgs[i]?.getOriginatingAddress()
                strMessage += """ :${msgs[i]?.getMessageBody()}
"""
                // Log and display the SMS message.
                Log.d(TAG, "onReceive: $strMessage")
                Log.d("MyOtherSMS_Text", "onReceive: $strMessage")
                Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show()
            }
        }
        Log.d("MyOtherSMS_Text2", "onReceive: $strMessage")
    }

    companion object {
        private val TAG = MyOtherSmSReceiver::class.java.simpleName
        const val pdu_type = "pdus"
    }
}