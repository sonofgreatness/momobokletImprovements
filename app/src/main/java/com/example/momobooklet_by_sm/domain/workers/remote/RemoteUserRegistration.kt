package com.example.momobooklet_by_sm.domain.workers.remote



import android.app.Activity
import com.example.momobooklet_by_sm.data.local.models.UserModel
import com.google.firebase.auth.FirebaseAuth


/**
 * Registers a User into firebase DataBase
 * verifies User.MoMo phone using twilio
 *Sign in verified User into Firebase Database
 *
*/
class RemoteUserRegistration(val user: UserModel, activity: Activity,mAuth:FirebaseAuth )
{
}
