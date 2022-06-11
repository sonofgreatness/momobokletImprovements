package com.example.momobooklet_by_sm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.momobooklet_by_sm.displaytransactions.DisplayTransactionsLandingFragment
import com.example.momobooklet_by_sm.managepdfs.ManagePDFsLandingFragment
import com.example.momobooklet_by_sm.manageuser.ManageUserLandingFragment
import com.example.momobooklet_by_sm.recordingtransanctions.RecordTransactionsLandingFragment
import com.google.android.material.tabs.TabLayout


//@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationHost {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


/*Animation
        val imageview:ImageView =findViewById(R.id.imageView)

        (imageview.drawable as? Animatable)?.start()

*/


        val tabLayout :TabLayout =findViewById(R.id.tabs)
        val viewPager: ViewPager= findViewById(R.id.viewpager)
setUpViewAdapter(viewPager)
tabLayout.setupWithViewPager(viewPager)

        tabLayout.getTabAt(0)?.setIcon(R.drawable.home)
        tabLayout.getTabAt(2)?.setIcon(R.drawable.user_account_icon)

// if there is no saved insstance then load the login fragment uto main main activity framne
  /**  if (savedInstanceState == null) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, UserAccountsFragment())
            .commit()
    }**/



}

    override fun navigateTo(fragment: Fragment, addToBackstack: Boolean) {
        val transaction = supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)

        if (addToBackstack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }
private fun setUpViewAdapter(viewPager:ViewPager) {

    val adapter = ViewPagerAdapter(supportFragmentManager)
    adapter.addFragment(RecordTransactionsLandingFragment(), "HOME")
    adapter.addFragment(DisplayTransactionsLandingFragment(), "RECORDS")
    adapter.addFragment(ManageUserLandingFragment(), "USERS")
    adapter.addFragment(ManagePDFsLandingFragment(), "PDFS")
    viewPager.adapter = adapter
    }

}
