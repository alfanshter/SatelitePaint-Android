package com.satelit.satelitpaint

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.satelit.satelitpaint.ui.HomeFragment
import com.satelit.satelitpaint.ui.MapsFragment
import com.satelit.satelitpaint.ui.TransaksiCustomerFragment
import android.telephony.TelephonyManager
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import android.provider.Settings.Secure

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.satelit.satelitpaint.session.SessionManager
import com.satelit.satelitpaint.ui.CicilanUserFragment


class MainActivity : AppCompatActivity(),AnkoLogger {
    lateinit var sessionManager: SessionManager
    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.framecustomer,
                        HomeFragment()
                    ).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_transaksi -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.framecustomer,
                        TransaksiCustomerFragment()
                    ).commit()
                    return@OnNavigationItemSelectedListener true

                }

                R.id.navigation_cicilan -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.framecustomer,
                        CicilanUserFragment()
                    ).commit()
                    return@OnNavigationItemSelectedListener true

                }

                R.id.navigation_peta -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.framecustomer,
                        MapsFragment()
                    ).commit()
                    return@OnNavigationItemSelectedListener true

                }

            }

            false
        }

    var token  :String? = null
    lateinit var firestoreuser: FirebaseFirestore
    lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_viewcustomer)

        firebaseDatabase = FirebaseDatabase.getInstance("https://satelitepaint-b27bb-default-rtdb.asia-southeast1.firebasedatabase.app/")

        sessionManager = SessionManager(this)
        firestoreuser = FirebaseFirestore.getInstance()
        gettoken()
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        sessionManager.setDevice(getDeviceUniqueID(this).toString())
        moveToFragment(HomeFragment())

    }

    @SuppressLint("HardwareIds")
    fun getDeviceUniqueID(activity: Activity): String? {
        return Secure.getString(
            activity.contentResolver,
            Secure.ANDROID_ID
        )
    }

    private fun moveToFragment(fragment: Fragment) {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.framecustomer, fragment)
        fragmentTrans.commit()
    }

    //get Token UP
    private fun gettoken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            // Get new FCM registration token
            token = task.result
            if (token!=null){
                val usermap: MutableMap<String, Any?> = HashMap()
                usermap["users"] = getDeviceUniqueID(this).toString()
                usermap["token"] = token
                val post = firebaseDatabase.reference.child("users").child(getDeviceUniqueID(this).toString()).setValue(usermap)
            }

        })
    }

}