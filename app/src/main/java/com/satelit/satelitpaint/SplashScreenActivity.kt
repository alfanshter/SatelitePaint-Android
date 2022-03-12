package com.satelit.satelitpaint

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.satelit.satelitpaint.admin.HomeAdminActivity
import com.satelit.satelitpaint.admin.auth.LoginActivity
import com.satelit.satelitpaint.admin.ui.home.HomeAdminFragment
import com.satelit.satelitpaint.session.SessionManager
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class SplashScreenActivity : AppCompatActivity() {
    lateinit var handler: Handler
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        sessionManager = SessionManager(this)

        if (sessionManager.getLogin()==true){
            handler = Handler()
            handler.postDelayed({
                startActivity(intentFor<HomeAdminActivity>().clearTask().newTask())
                finish()
            }, 3000)

        }else{
            handler = Handler()
            handler.postDelayed({
                startActivity(intentFor<MainActivity>().clearTask().newTask())
                finish()
            }, 3000)

        }

    }
}