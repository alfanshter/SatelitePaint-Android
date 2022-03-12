package com.satelit.satelitpaint.admin

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.model.ResponseModel
import com.satelit.satelitpaint.session.SessionManager
import com.satelit.satelitpaint.webservice.ApiClient
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detal_produk.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.NumberFormat

class DetailProdukActivity : AppCompatActivity(),AnkoLogger {
    var nama : String? = null
    var harga : Int? = null
    var stok : Int? = null
    var id : Int? = null
    var deskripsi : String? = null
    var foto : String? = null
    var rating : Float? = null

    lateinit var sessionManager: SessionManager
    lateinit var progressDialog : ProgressDialog
    private val api = ApiClient.instance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detal_produk)

        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)

        val bundle: Bundle? = intent.extras
        nama = bundle!!.getString("nama")
        harga = bundle.getInt("harga")
        deskripsi = bundle.getString("deskripsi")
        foto = bundle.getString("foto")
        rating = bundle.getFloat("rating")
        stok = bundle.getInt("stok")
        id = bundle.getInt("id")

        Picasso.get().load(foto).fit().centerCrop().into(imgfoto)
        txtnama.text = nama.toString()
        val formatter: NumberFormat = DecimalFormat("#,###")
        val myNumber = harga
        val convertharga: String = formatter.format(myNumber)

        txtharga.text = "Rp. $convertharga"
        txtdeskripsi.text = deskripsi.toString()
        txtrating.text = deskripsi.toString()
        txtrating.text = rating.toString()


        btnedit.setOnClickListener {
            startActivity<TambahProdukActivity>("nama" to nama,
                "harga" to harga,
                "deskripsi" to  deskripsi,
                "foto" to foto,
                "rating" to rating,
                "stok" to stok,
                "id" to id
                )
        }

        btnback.setOnClickListener {
            finish()
        }

        btnhapus.setOnClickListener {view->
            val builder = AlertDialog.Builder(this@DetailProdukActivity)
            builder.setTitle("Produk $nama")
            builder.setMessage("Hapus Produk ini ? ")
            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                api.deleteproduct(
                    "Bearer ${sessionManager.getToken().toString()}",
                    id!!
                ).enqueue(object : Callback<ResponseModel> {
                    override fun onResponse(
                        call: Call<ResponseModel>,
                        response: Response<ResponseModel>
                    ) {
                        try {
                            if (response.isSuccessful) {
                                if (response.body()!!.data == 1) {
                                    val photoRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(foto.toString())
                                    photoRef.delete()
                                    startActivity<HomeAdminActivity>()
                                    toast("Hapus produk berhasil")
                                    finish()
                                } else {
                                    progressDialog.dismiss()
                                    Snackbar.make(view, "Jaringan Error", 3000).show()
                                }
                            } else {
                                progressDialog.dismiss()
                                Snackbar.make(view, "Response salah hubungi admin", 3000).show()
                            }
                        } catch (e: Exception) {
                            progressDialog.dismiss()
                            info { "dinda ${e.message}" }
                            Snackbar.make(view, "Ada kesalahan aplikasi", 3000).show()

                        }
                    }

                    override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                        progressDialog.dismiss()
                        info { "dinda ${t.message}" }
                        Snackbar.make(view, "Ada kesalahan Jaringan", 3000).show()
                    }

                })
            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
            }

            builder.show()


        }

    }
}