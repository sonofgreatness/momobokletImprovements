package com.example.momobooklet_by_sm

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import timber.log.Timber

class AbstractyPermissionsActivity : Activity() {
    protected fun getDesiredPermissions(): Array<String?> =
        Array<String?>(1) { Manifest.permission.RECEIVE_SMS }

//    protected abstract fun onPermissionDenied()
protected  fun onReady(state: Bundle?)
{
val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
}

    private val REQUEST_PERMISSION = 125
    private val STATE_IN_PERMISSION = "inPermission"
    private var isInPermission = false
    private var state: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_abstracty_permissions)
        /*=============================================
         Check if app has READ SMS permission
                if not REQUEST IT
                if yes start MainActivity
         *=============================================
         */

        this.state = savedInstanceState
        if (state != null)// re-created ui
            isInPermission = state!!.getBoolean(STATE_IN_PERMISSION, false)

        if (hasAllPermissions(getDesiredPermissions())) {
            Timber.d("Permission Check Made")
            onReady(state)
        } else if (!isInPermission) {
            Timber.d("Permission Request Called")
            Timber.d("Permission List -> ${netPermissions(getDesiredPermissions()).size}")
            isInPermission = true
            //Explaination


                Timber.d("REQUUUUUUU")
                ActivityCompat.requestPermissions(this, netPermissions(getDesiredPermissions()),REQUEST_PERMISSION)
                //Now Check permission
                val test = (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)==
                        PackageManager.PERMISSION_GRANTED)

                Timber.d("CHECK PERMISSION AFTER REQUEST -> ${test}")
        }
    }

    /**
    * Gets permissions app is not granted but it desires
    * */
     fun netPermissions(desiredPermissions: Array<String?>?): Array<String>
    {

        var i = 0
        var  result : Array<String> = arrayOf("0")

        for (perm in desiredPermissions!!) {
            if (!hasPermission(perm)) {
                if (perm != null) {
                        result!![i] = perm
                         i++
                }
            }
        }
        Toast.makeText(this, "${result.get(0)}", Toast.LENGTH_SHORT).show()
        Timber.d("net permissions size-> ${result.size}  net permissions  string ${result[0]} ")
        return result!!
     }
    /**
     * checks if user granted all permissions in request list
     *              (READ SMS) for now
     *@param : desiredPermmissions -> Array of Permissions
     *  @return : returns true if ALL Permissions  granted
     **/
    fun hasAllPermissions(desiredPermissions: Array<String?>?): Boolean {

        Timber.d("HAS ALL permissions  Length-> ${desiredPermissions!!.size}    String ${desiredPermissions[0]}")
        for (perm in desiredPermissions!!) {
            if (!hasPermission(perm)) {
                return false
            }
        }
        return true
    }
    /**
     *  checks if app has a permission
     *  @param perm -> the permission to check
     *  @return returns true if app is granted the permission
     */
    private fun hasPermission(perm: String?): Boolean
    {
        Timber.d("HAS Permission -> ${ContextCompat.checkSelfPermission(this, perm!!)}")

        Timber.d("HAS PERmisssion String -> ${perm!!}")
        return(ContextCompat.checkSelfPermission(this, perm!!)==
                PackageManager.PERMISSION_GRANTED)
    }
    /**
    * Handles request
     * if all permissions are granted call OnReady
     *  if permissions are not granted Tell User why this is BAD
    * */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        isInPermission = false

        if(requestCode == REQUEST_PERMISSION) {
            if (hasAllPermissions(getDesiredPermissions())) {
                onReady(state)

            }
        }
        // Tell User This Is Bad
        Log.d("Truly Horrible", "Vogue")
        Toast.makeText(this, "Very Very Bad Stuff",Toast.LENGTH_SHORT).show()
    }
    /**
    *Checks if request for permissions is initiated
    * stores flag   in outState ,
    *flag == isInPermission == true if request for permission was initiated
    **/
    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
            outState.putBoolean(STATE_IN_PERMISSION,isInPermission)
    }
}