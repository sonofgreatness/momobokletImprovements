package com.example.momobooklet_by_sm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.shrinetutor2.LoginFragment

class MainActivity : AppCompatActivity(), NavigationHost {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


// if there is no saved insstance then load the login fragment uto main main activity framne
    if (savedInstanceState == null) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, LoginFragment())
            .commit()
    }
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

}