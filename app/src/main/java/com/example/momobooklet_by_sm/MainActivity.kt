package com.example.momobooklet_by_sm


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.print.PrintAttributes
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.momobooklet_by_sm.ui.viewmodelProviderFactories.CommissionViewModelProviderFactory
import com.example.momobooklet_by_sm.ui.viewmodelProviderFactories.TransactionViewModelProviderFactory
import com.example.momobooklet_by_sm.ui.viewmodels.CommissionViewModel
import com.example.momobooklet_by_sm.ui.viewmodels.TransactionViewModel
import com.example.momobooklet_by_sm.ui.viewmodels.UserViewModel
import com.example.momobooklet_by_sm.util.Constants.Companion.DEFAULT_START_DATE
import com.example.momobooklet_by_sm.util.Constants.Companion.MONTHLY_STARTDATE_FILENAME
import com.example.momobooklet_by_sm.util.classes.Events.networkEvent
import com.example.momobooklet_by_sm.util.classes.NetworkChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.wwdablu.soumya.simplypdf.SimplyPdf
import com.wwdablu.soumya.simplypdf.composers.properties.TableProperties
import com.wwdablu.soumya.simplypdf.composers.properties.TextProperties
import com.wwdablu.soumya.simplypdf.composers.properties.cell.Cell
import com.wwdablu.soumya.simplypdf.composers.properties.cell.TextCell
import com.wwdablu.soumya.simplypdf.document.DocumentInfo
import com.wwdablu.soumya.simplypdf.document.Margin
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.util.*


//@AndroidEntryPoint
@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    lateinit var mUserViewModel: UserViewModel
    lateinit var mTransactionViewModel: TransactionViewModel
    lateinit var mCommissionViewModel: CommissionViewModel
    var myIsConnected: Boolean = false

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)

        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(NetworkChangeListener(), intentFilter)

    }

    //   @SuppressLint("ResourceType")
    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Listen to Network Broadcast
        checkIfMonthlyStartDateFileMustBeCreated()

        val TransactionViewModelProviderFactory = TransactionViewModelProviderFactory(application,this)
        val CommissionViewModelProviderFactory = CommissionViewModelProviderFactory(application, this)



        mTransactionViewModel = ViewModelProvider(this,TransactionViewModelProviderFactory)[TransactionViewModel::class.java]
        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        mCommissionViewModel = ViewModelProvider(this,CommissionViewModelProviderFactory)[CommissionViewModel::class.java]

        //mCommissionViewModel.exportDailyCommissionsPdf()

        setContentView(R.layout.activity_main)
        setUpBottomNav()
        firebasePlayIntegrityInitialize()
       // setUpExternalMemoryFileDirectories()


        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                val getpermission = Intent()
                getpermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivity(getpermission)
            }
            writepdf()
        }
        setUpExternalMemoryFileDirectories()
    }


    /***************************************************************************
     * setUpExternalMemoryFileDirectories() -> creates in External Storage
     *                                  the directory  "MoMoBooklet"
     *
     ***************************************************************************/
    private fun setUpExternalMemoryFileDirectories() {
        val hostPath = Environment.getExternalStorageDirectory().path
        val rootDirectory  = File( hostPath+"/"+getString(R.string.app_name))

        if (!rootDirectory.exists())
            rootDirectory.mkdir()
        val pdfDirectory = File(hostPath+"/"+getString(R.string.app_name)+"/"+"PDF")
        val csvDirectory = File(hostPath+"/"+getString(R.string.app_name)+"/"+"CSV")

        if (!pdfDirectory.exists())
                 pdfDirectory.mkdir()
        if (!csvDirectory.exists())
                  csvDirectory.mkdir()

    }


    @Subscribe
    fun makeToast(networkEvent: networkEvent) {

        myIsConnected = networkEvent.isnetworkConnected
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


    @SuppressLint("LogNotTimber")
    fun writepdf() {
        Log.d(
            "Show Me Where THe PDFs are : -> ",
            Environment.getExternalStorageDirectory().path
        )
        GlobalScope.launch {

            val hostDirectory = File(Environment.getExternalStorageDirectory().path + "/MyDocuments")

            if(!hostDirectory.exists())
                    hostDirectory.mkdir()

            val pdfFile = File(Environment.getExternalStorageDirectory().path + "/MyDocuments/test301 .pdf")

            try {
                pdfFile.createNewFile()
            } catch (e: Exception) {
                Log.d(
                    "Show me Where the PDFs are Create : -> ",
                    "${e.message}\n ${e.stackTraceToString()}"
                )

            }

            val simplyPdfDocument = SimplyPdf.with(
                applicationContext,
                pdfFile
            )
                .colorMode(DocumentInfo.ColorMode.COLOR)
                .paperSize(PrintAttributes.MediaSize.ISO_A4)
                .margin(
                    Margin(0u, 0u, 0u, 0u)
                )
                .firstPageBackgroundColor(Color.WHITE)
                .paperOrientation(DocumentInfo.Orientation.PORTRAIT)
                .build()




                /*initializing then self referencing a linked list
                */
            val rows = LinkedList<LinkedList<Cell>>().apply {

                add(LinkedList<Cell>().apply{
                    add(TextCell("Row 1 Column1",TextProperties().apply {
                        textColor  = "#001000"
                        textSize = 12
                    },simplyPdfDocument.usablePageWidth/3)
                    )
                    add(TextCell("Row 1 Column2",TextProperties().apply {
                        textColor  = "#001000"
                        textSize = 12
                    },simplyPdfDocument.usablePageWidth/3)
                    )


                })



                add(LinkedList<Cell>().apply{
                    add(TextCell("Row 2 Column1",TextProperties().apply {
                        textColor  = "#001000"
                        textSize = 12
                    },Cell.MATCH_PARENT))

                    add(LinkedList<Cell>().apply{
                        add(TextCell("Row 2 Column2",TextProperties().apply {
                            textColor  = "#001000"
                            textSize = 12
                        },Cell.MATCH_PARENT))
                    })
                })


            }

            val myTabProperties = TableProperties().apply{
                borderColor = "#000000"
                borderWidth = 2
                drawBorder = true
            }




            Log.d(
                "Show me Where the PDFs are EXIST : -> ",
                "${
                    File(Environment.getExternalStorageDirectory().path + "/test02.pdf").exists()
                }"
            )


            simplyPdfDocument.text.write("....", TextProperties().apply {
                textSize = 12
                textColor = "#000000"
                typeface = Typeface.DEFAULT
            })

            simplyPdfDocument.table.draw(rows,myTabProperties)
            simplyPdfDocument.finish()
        }

    }

    /**********************************************************************************************************
     *checkIfMonthlyStartDateFileMustBeCreated() -> checks if file of the name
     *                                      MonthlyDataStartDate.txt exists  in the files Directory (filesDir)
     *       if File exists : Do nothing
     *          if File does not exist create it
     *************************************************************************************************/
    private fun checkIfMonthlyStartDateFileMustBeCreated() {
        val FileToCheck = File(application.filesDir.path.plus("/").plus(MONTHLY_STARTDATE_FILENAME))
        if(!FileToCheck.exists())
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

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        //de register from broadcast
        try {
            unregisterReceiver(NetworkChangeListener())
        } catch (e: Exception) {
            Timber.d("Couldn't Unregister NetworkReciever , ${e}")
        }
        //check if reciever is registered before doing this ****************
    }

    var control: Boolean =false
    fun changeChipState(view: View) {
    //change backGroundtint
        (view as Chip).checkedIconTint

    }

}

