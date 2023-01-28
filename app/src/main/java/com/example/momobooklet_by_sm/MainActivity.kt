package com.example.momobooklet_by_sm


import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.momobooklet_by_sm.ui.viewmodels.TransactionViewModel
import com.example.momobooklet_by_sm.ui.viewmodels.UserViewModel
import com.example.momobooklet_by_sm.util.classes.Events.networkEvent
import com.example.momobooklet_by_sm.util.classes.NetworkChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber


//@AndroidEntryPoint
@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

 lateinit var  mUserViewModel: UserViewModel
 lateinit var mTransactionViewModel: TransactionViewModel
 var myIsConnected: Boolean = false

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)

        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(NetworkChangeListener(), intentFilter)

    }
 //   @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     // Listen to Network Broadcast


        mTransactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        setContentView(R.layout.activity_main)
        setUpBottomNav()
     firebasePlayIntegrityInitialize()
 }


    @Subscribe
fun makeToast(networkEvent: networkEvent)
{

    myIsConnected = networkEvent.isnetworkConnected
}
    /*
    private fun getAppSignature() {
        val  appSigner=
            AppSignatureHelper(this)
        Log.d("AppSignature","${appSigner.appSignatures}")
    }
     */

    private fun firebasePlayIntegrityInitialize()
    {
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

    }
    private fun setUpBottomNav() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
//set up bottom navigation with nav controller
        val bottomnav:BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomnav.setupWithNavController(navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        //de register from broadcast
        try {
            unregisterReceiver(NetworkChangeListener())
        } catch (e:Exception)
        {
            Timber.d("Couldn't Unregister NetworkReciever , ${e}")
        }
    //check if reciever is registered before doing this ****************
        }

    }

