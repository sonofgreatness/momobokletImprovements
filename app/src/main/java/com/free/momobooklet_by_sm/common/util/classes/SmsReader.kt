package com.free.momobooklet_by_sm.common.util.classes


/**
* Reads incoming SMSs
 *extracts OTP String from Firebase  Sender
 * ASSUMES : PERMISSION.READ_SMS == GRANTED
 *
**/

class SmsReader {
    private var otpString: String = ""

    init
    {

    }



    fun getOtpString ():String
    {
        return otpString
    }
}