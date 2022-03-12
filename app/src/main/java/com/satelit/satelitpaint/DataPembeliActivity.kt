package com.satelit.satelitpaint

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.satelit.satelitpaint.database.NoteDB
import com.satelit.satelitpaint.database.entitas.Checkout
import com.satelit.satelitpaint.database.entitas.Note
import com.satelit.satelitpaint.model.ResponseModel
import com.satelit.satelitpaint.webservice.ApiClient
import kotlinx.android.synthetic.main.activity_data_pembeli.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class DataPembeliActivity : AppCompatActivity(), AnkoLogger {
    lateinit var progressDialog: ProgressDialog

    val db by lazy {
        NoteDB(this)
    }

    private var api = ApiClient.instance()
    var hargatotal: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_pembeli)
        progressDialog = ProgressDialog(this)
        val bundle: Bundle? = intent.extras
        hargatotal = bundle!!.getInt("hargatotal")

        btncheckout.setOnClickListener { view ->
            val nama = edtnama.text.toString()
            val alamat = edtalamat.text.toString()
            val telepon = edttelepon.text.toString()

            if (nama.isNotEmpty() && alamat.isNotEmpty() && telepon.isNotEmpty()) {
                startActivity<MetodePembayaranActivity>(
                    "nama" to nama,
                    "alamat" to alamat,
                    "telepon" to telepon,
                    "hargatotal" to hargatotal,
                )
                finish()

            } else {
                Snackbar.make(view, "Jangan kosongi kolom ", 3000).show()

            }
        }
    }

    override fun onStart() {
        super.onStart()

    }
}