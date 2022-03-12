package com.satelit.satelitpaint.admin.auth

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.admin.HomeAdminActivity
import com.satelit.satelitpaint.model.LoginResponse
import com.satelit.satelitpaint.session.SessionManager
import com.satelit.satelitpaint.webservice.ApiClient
import kotlinx.android.synthetic.main.activity_login2.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(),AnkoLogger {
    private var api = ApiClient.instance()
    lateinit var progressDialog: ProgressDialog
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)
        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)

        btnlogin.setOnClickListener { view ->
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.setTitle(resources.getString(R.string.loading))
            progressDialog.show()

            val nomorwa = edtnowa.text.toString()
            val password = edtpass.text.toString()

            if (nomorwa.isNotEmpty() && password.isNotEmpty()) {
                api.login(nomorwa, password).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        try {
                            if (response.isSuccessful) {
                                if (response.body()!!.data == 1) {
                                    progressDialog.dismiss()
                                    sessionManager.setToken(response.body()!!.token.toString())
                                    sessionManager.setLogin(true)
                                    startActivity<HomeAdminActivity>()
                                    finish()
                                } else {
                                    progressDialog.dismiss()
                                    Snackbar.make(view, "Nomor atau password salah", 3000).show()
                                }
                            } else {
                                progressDialog.dismiss()
                                Snackbar.make(view, "Response salah hubungi admin", 3000).show()
                            }
                        } catch (e: Exception) {
                            progressDialog.dismiss()
                            info { "dinda ${e.message}" }
                            Snackbar.make(view,"Ada kesalahan aplikasi",3000).show()

                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        progressDialog.dismiss()
                        info { "dinda ${t.message}" }
                        Snackbar.make(view,"Ada kesalahan Jaringan",3000).show()
                    }

                })
            } else {
                progressDialog.dismiss()
                Snackbar.make(view, "Jangan kosongi kolom", 3000).show()
            }
        }

    }
}