package com.satelit.satelitpaint.admin

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.admin.ui.cicilan.CicilanFragment
import com.satelit.satelitpaint.admin.ui.home.HomeAdminFragment
import com.satelit.satelitpaint.admin.ui.report.ReportFragment

class HomeAdminActivity : AppCompatActivity() {
    var token  :String? = null
    lateinit var firestoreuser: FirebaseFirestore
    lateinit var firebaseDatabase: FirebaseDatabase

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.framehome,
                        HomeAdminFragment()
                    ).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_report -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.framehome,
                        ReportFragment()
                    ).commit()
                    return@OnNavigationItemSelectedListener true

                }

                R.id.navigation_cicilan -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.framehome,
                        CicilanFragment()
                    ).commit()
                    return@OnNavigationItemSelectedListener true

                }


            }

            false
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_admin)
        val navView: BottomNavigationView = findViewById(R.id.nav_viewhome)
        firebaseDatabase = FirebaseDatabase.getInstance("https://satelitepaint-b27bb-default-rtdb.asia-southeast1.firebasedatabase.app/")

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        moveToFragment(HomeAdminFragment())
        gettoken()

    }

    private fun moveToFragment(fragment: Fragment) {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.framehome, fragment)
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
                val post = firebaseDatabase.reference.child("admin").child("admin").setValue(usermap)
            }

        })
    }


    @SuppressLint("HardwareIds")
    fun getDeviceUniqueID(activity: Activity): String? {
        return Settings.Secure.getString(
            activity.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }
}