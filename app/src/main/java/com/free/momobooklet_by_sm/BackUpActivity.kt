package com.free.momobooklet_by_sm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.databinding.ActivityBackUp3Binding
import com.free.momobooklet_by_sm.presentation.ui.viewmodels.BackupDataBaseViewModel
import com.free.momobooklet_by_sm.presentation.ui.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackUpActivity : AppCompatActivity() {
private lateinit var binding: ActivityBackUp3Binding

val mBackUpDatabaseViewModel: BackupDataBaseViewModel by viewModels()
    val mUserViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackUp3Binding.inflate(layoutInflater)
        getFileManagementPermissions()
        setContentView(binding.root)
    }




    /***************************************************************************************
     * getFileManagementPermissions() : if Build.VERSION => 30
     *                                  request MANAGE_ALL_FILES_ACCESS_PERMISSION
     *
     *                              : else
     *                        request READ_EXTERNAL_FILES AND READ_INTERNAL_FILES PERMISSION
     *****************************************************************************************/
    fun getFileManagementPermissions() {
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                requestManageFilesPermissions()
            }
        } else {
            requestREADANDWRITE()
        }
    }

    /****************************************************************
     * requestREAD_AND_WRITE() -> To be called For SDK >= 30
     *          request Manifest.permission.WRITE_EXTERNAL_STORAGE
     *                           +
     *          request Manifest.permission.WRITE_EXTERNAL_STORAGE
     *
     *****************************************************************/
    private fun requestREADANDWRITE() {

        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) &&
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(
                    this, "This App needs file-access permission \n  " +
                            "to create and store your report files",
                    Toast.LENGTH_SHORT
                ).show()

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    Constants.REQUEST_FILE_PERMISSION
                )
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    Constants.REQUEST_FILE_PERMISSION
                )
            }
        } else {
            // Permission has already been granted
        }

    }

    /****************************************************************
     * requestREAD_AND_WRITE() -> To be called For SDK >= 30
     *          request Manifest.permission.WRITE_EXTERNAL_STORAGE
     *                           +
     *          request Manifest.permission.WRITE_EXTERNAL_STORAGE
     *
     *****************************************************************/
    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestManageFilesPermissions() {
        // Show Dialogue Explaining To User
        // That you are about to Request FileManager Permission

        AlertDialog.Builder(this)
            .setTitle("FILEMANAGER \n PERMISSION REQUEST")
            .setMessage(
                getString(R.string.request_manage_file_permission_back_up)
            )

            .setNegativeButton("CANCEL"){dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("GRANT") { dialog, which ->
                val getpermission = Intent()
                getpermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                dialog.dismiss()
                startActivity(getpermission)
            }
            .show()


    }

}