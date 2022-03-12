package com.satelit.satelitpaint

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.satelit.satelitpaint.admin.HomeAdminActivity
import com.satelit.satelitpaint.database.NoteDB
import com.satelit.satelitpaint.database.entitas.Note
import com.satelit.satelitpaint.model.ResponseModel
import com.satelit.satelitpaint.session.SessionManager
import com.satelit.satelitpaint.webservice.ApiClient
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_produk.*
import kotlinx.android.synthetic.main.activity_detail_produk.btnback
import kotlinx.android.synthetic.main.activity_detail_produk.imgfoto
import kotlinx.android.synthetic.main.activity_detail_produk.txtdeskripsi
import kotlinx.android.synthetic.main.activity_detail_produk.txtharga
import kotlinx.android.synthetic.main.activity_detail_produk.txtnama
import kotlinx.android.synthetic.main.activity_detail_produk.txtrating
import kotlinx.android.synthetic.main.activity_detal_produk.*
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
import java.text.DecimalFormat
import java.text.NumberFormat

class DetailProdukActivity : AppCompatActivity(),AnkoLogger {
    var nama: String? = null
    var harga: Int? = null
    var stok: Int? = null
    var id: Int? = null
    var deskripsi: String? = null
    var foto: String? = null
    var rating: Float? = null

    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    private val api = ApiClient.instance()

    val db by lazy {
        NoteDB(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_produk)
        progressDialog = ProgressDialog(this)
        sessionManager = SessionManager(this)
        val bundle: Bundle? = intent.extras
        nama = bundle!!.getString("nama")
        harga = bundle.getInt("harga")
        deskripsi = bundle.getString("deskripsi")
        foto = bundle.getString("foto")
        rating = bundle.getFloat("rating")
        stok = bundle.getInt("stok")
        id = bundle.getInt("id")


        Picasso.get().load(foto).fit().into(imgfoto)
        txtnama.text = nama.toString()
        //convert to money
        val formatter: NumberFormat = DecimalFormat("#,###")
        val myNumber = harga
        val hargaproduk: String = formatter.format(myNumber)

        txtharga.text = "Rp. $hargaproduk"
        txtdeskripsi.text = deskripsi.toString()
        txtrating.text = deskripsi.toString()
        txtrating.text = rating.toString()

        btnback.setOnClickListener {
            finish()
        }

        btnkeranjang.setOnClickListener {
            addCart(it)
        }

        btnbelanja.setOnClickListener {
            belanja(it)
        }

        btncart.setOnClickListener {
            startActivity<KeranjangActivity>()
            finish()
        }


    }

    private fun addCart(view : View) {
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setTitle(resources.getString(R.string.loading))
        progressDialog.show()
        api.addcart(
            id!!,
            sessionManager.getDevice().toString(),
            "",
            nama.toString(),
            foto.toString(),
            deskripsi.toString(),
            harga!!
        ).enqueue(object :Callback<ResponseModel>{
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                try {
                    if (response.isSuccessful) {
                        if (response.body()!!.data == 1) {
                            progressDialog.dismiss()
                            Snackbar.make(view, "${response.body()!!.message.toString()}", 3000).show()

                        } else {
                            progressDialog.dismiss()
                            Snackbar.make(view, "Ada kesalahan sistem", 3000).show()
                        }
                    } else {
                        progressDialog.dismiss()
                        Snackbar.make(view, "Ada kesalahan sistem", 3000).show()
                    }
                } catch (e: Exception) {
                    progressDialog.dismiss()
                    info { "dinda ${e.message}" }
                    Snackbar.make(view,"Ada kesalahan aplikasi",3000).show()

                }

            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                progressDialog.dismiss()
                info { "dinda ${t.message}" }
                Snackbar.make(view,"Jaringan bermasalah",3000).show()

            }

        })

    }
    private fun belanja(view : View) {
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setTitle(resources.getString(R.string.loading))
        progressDialog.show()
        api.addcart(
            id!!,
            sessionManager.getDevice().toString(),
            "",
            nama.toString(),
            foto.toString(),
            deskripsi.toString(),
            harga!!
        ).enqueue(object :Callback<ResponseModel>{
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                try {
                    if (response.isSuccessful) {
                        if (response.body()!!.data == 1) {
                            progressDialog.dismiss()
                            startActivity<KeranjangActivity>()
                            finish()

                        } else {
                            progressDialog.dismiss()
                            Snackbar.make(view, "Ada kesalahan sistem", 3000).show()
                        }
                    } else {
                        progressDialog.dismiss()
                        Snackbar.make(view, "Ada kesalahan sistem", 3000).show()
                    }
                } catch (e: Exception) {
                    progressDialog.dismiss()
                    info { "dinda ${e.message}" }
                    Snackbar.make(view,"Ada kesalahan aplikasi",3000).show()

                }

            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                progressDialog.dismiss()
                info { "dinda ${t.message}" }
                Snackbar.make(view,"Jaringan bermasalah",3000).show()

            }

        })

    }
}