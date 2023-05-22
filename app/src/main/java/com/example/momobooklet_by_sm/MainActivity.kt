package com.example.momobooklet_by_sm


//import com.mixpanel.android.mpmetrics.MixpanelAPI
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.momobooklet_by_sm.common.util.Constants.Companion.DEFAULT_START_DATE
import com.example.momobooklet_by_sm.common.util.Constants.Companion.MONTHLY_STARTDATE_FILENAME
import com.example.momobooklet_by_sm.common.util.Constants.Companion.REQUEST_FILE_PERMISSION
import com.example.momobooklet_by_sm.domain.repositories.ConnectivityObserver
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.BackupViewModel
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.CommissionViewModel
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.TransactionViewModel
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@AndroidEntryPoint
@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    //lateinit var mMixpanel: MixpanelAPI
    val mUserViewModel: UserViewModel by viewModels()
    val mTransactionViewModel: TransactionViewModel by viewModels()
    val mCommissionViewModel: CommissionViewModel by viewModels()
    val mBackupViewModel : BackupViewModel by viewModels()
    @Inject
    lateinit var  connectivityObserver: ConnectivityObserver



    override fun onResume() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(
            baseContext
        )
        val previouslyStarted = prefs.getBoolean("previously started", false)
        if (!previouslyStarted) {
            val edit = prefs.edit()
            edit.putBoolean("previously started", java.lang.Boolean.TRUE)
            edit.commit()
            moveToMainActivity2()
        }
        super.onResume()
    }





    //   @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val trackAutomaticEvents = true
        //  mMixpanel =
        //     MixpanelAPI.getInstance(this, "a34dd4774bd7d4f78b8285e889ebdab6", trackAutomaticEvents)

        determineifToregisterUser()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun determineifToregisterUser() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(
            baseContext
        )
        val previouslyStarted = prefs.getBoolean("previously started", false)
        if (!previouslyStarted) { // user needs to register
            val edit = prefs.edit()
            edit.putBoolean("previously started", java.lang.Boolean.TRUE)
            edit.commit()
            //mMixpanel.identify(randomInt.toString())
            moveToMainActivity2()
        } else
            normalFlow()// user is registered
    }
    private fun moveToMainActivity2() {
        val i = Intent(baseContext, MainActivity2::class.java)
        startActivity(i)

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun normalFlow() {
        // Listen to Network Broadcast
        checkIfMonthlyStartDateFileMustBeCreated()
        setUpViewModels()

        setContentView(R.layout.activity_main)
        setUpBottomNav()
        firebasePlayIntegrityInitialize()
        setUpExternalMemoryFileDirectories()
        //getFileManagementPermissions()
    }

    /****************************************************
     * setUpViewModels ->  initiates the variables
     *              mTransactionViewModel
     *              mCommissionViewModel
     *              mUserViewModel
     *******************************************************/
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpViewModels() {
        print("does nothing for now")
    }

    @Throws(JSONException::class)
    private fun sendToMixpanel() {
        val props = JSONObject()
        props.put("source", "Pat's affiliate site")
        props.put("Opted out of email", true)
        //mMixpanel.track("Sign Up", props)
    }

    /***************************************************************************
     * setUpExternalMemoryFileDirectories() -> creates in External Storage
     *                                  the directory  "MoMoBooklet"
     *
     ***************************************************************************/
    private fun setUpExternalMemoryFileDirectories() {
        val hostPath = Environment.getExternalStorageDirectory().path
        val rootDirectory = File(hostPath + "/" + getString(R.string.app_name))


        if (!rootDirectory.exists())
            rootDirectory.mkdir()


        val pdfDirectory = File(hostPath + "/" + getString(R.string.app_name) + "/" + "PDF")
        val csvDirectory = File(hostPath + "/" + getString(R.string.app_name) + "/" + "CSV")

        if (!pdfDirectory.exists())
            pdfDirectory.mkdir()
        if (!csvDirectory.exists())
            csvDirectory.mkdir()


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
                    REQUEST_FILE_PERMISSION
                )
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    REQUEST_FILE_PERMISSION
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
                getString(R.string.request_manage_file_permission)
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

    /*
    private fun getAppSignature() {
        val  appSigner=
            AppSignatureHelper(this)
        Log.d("AppSignature","${appSigner.appSignatures}")
    }
     */

    private fun firebasePlayIntegrityInitialize() {
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

    }

    private fun setUpBottomNav() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        //set up bottom navigation with nav controller
        val bottomnav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomnav.setupWithNavController(navController)
    }


    /**********************************************************************************************************
     *checkIfMonthlyStartDateFileMustBeCreated() -> checks if file of the name
     *                                      MonthlyDataStartDate.txt exists  in the files Directory (filesDir)
     *       if File exists : Do nothing
     *          if File does not exist create it
     *************************************************************************************************/
    private fun checkIfMonthlyStartDateFileMustBeCreated() {
        val FileToCheck = File(application.filesDir.path.plus("/").plus(MONTHLY_STARTDATE_FILENAME))
        if (!FileToCheck.exists())
            createFileAndWriteDefaultData(FileToCheck)

    }

    /****************************************************
     * Creates File and writes the default "3" in it .
     *
     **************************************************/
    @SuppressLint("LogNotTimber")
    private fun createFileAndWriteDefaultData(FileToCheck: File) {
        lifecycleScope.launch {
            try {
                FileToCheck.createNewFile()
                try {

                    val fos: FileOutputStream =
                        openFileOutput(MONTHLY_STARTDATE_FILENAME, Context.MODE_PRIVATE)
                    fos.write(DEFAULT_START_DATE.toByteArray())
                    fos.close()
                } catch (ex: Exception) {
                    Log.d(
                        "FailedToWriteManageStartDateFile : -> ",
                        "${ex.message} :::::: \t ${ex.stackTrace}"
                    )
                }
            } catch (e: Exception) {
                Log.d(
                    "FailedToCreateManageStartDateFile : -> ",
                    "${e.message} :::::: \t ${e.stackTrace}"
                )
            }
        }
    }
    /**************************************************************
     * prevents moving to registration Activity by pressing back
     ***************************************************************/
    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}

