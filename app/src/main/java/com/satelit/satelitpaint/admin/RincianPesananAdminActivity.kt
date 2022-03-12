package com.satelit.satelitpaint.admin

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.adapter.DetailPesananAdapter
import com.satelit.satelitpaint.model.CartModel
import com.satelit.satelitpaint.model.CartResponse
import com.satelit.satelitpaint.model.ResponseModel
import com.satelit.satelitpaint.session.SessionManager
import com.satelit.satelitpaint.webservice.ApiClient
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_rincian_pesanan_admin.*
import kotlinx.android.synthetic.main.activity_rincian_pesanan_admin.rvpesanan
import kotlinx.android.synthetic.main.activity_rincian_pesanan_admin.txtalamat
import kotlinx.android.synthetic.main.activity_rincian_pesanan_admin.txtharga
import kotlinx.android.synthetic.main.activity_rincian_pesanan_admin.txtkalender
import kotlinx.android.synthetic.main.activity_rincian_pesanan_admin.txtnama
import kotlinx.android.synthetic.main.activity_rincian_pesanan_admin.txttelepon
import kotlinx.android.synthetic.main.activity_rincian_pesanan_admin.txttotalharga
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RincianPesananAdminActivity : AppCompatActivity(),AnkoLogger {
    var nomorpesanan : String? = null
    var nama : String? = null
    var telepon : String? = null
    var alamat : String? = null
    var tanggal : String? = null
    var metodepembayaran : String? = null
    var jam : String? = null
    var foto : String? = null
    var totalharga : Int? = null
    var status : Int? = null
    var id : Int? = null
    private lateinit var mAdapter: DetailPesananAdapter

//    0 - proses
//    1 - pengiriman
//    2 - selesai
//    3 - tolak

    private val api = ApiClient.instance()
    lateinit var progressDialog : ProgressDialog
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rincian_pesanan_admin)
        val bundle: Bundle? = intent.extras
        nomorpesanan = bundle!!.getString("nomorpesanan")
        nama = bundle.getString("nama")
        telepon = bundle.getString("telepon")
        alamat = bundle.getString("alamat")
        metodepembayaran = bundle.getString("metodepembayaran")
        foto = bundle.getString("foto")
        totalharga = bundle.getInt("harga")
        status = bundle.getInt("status")
        id = bundle.getInt("id")
        jam = bundle.getString("jam")
        tanggal = bundle.getString("tanggal")

        progressDialog = ProgressDialog(this)
        sessionManager = SessionManager(this)

        txtnopesanan.text = nomorpesanan.toString()
        txtnama.text = nama.toString()
        txttelepon.text = telepon.toString()
        txtalamat.text = alamat.toString()
        txtharga.text = totalharga.toString()
        txtkalender.text = "${tanggal} ${jam}"
        txtmetodepembayaran.text = metodepembayaran.toString()
        txttotalharga.text = "Rp. ${totalharga.toString()}"
        if (metodepembayaran == "transfer"){
            viewbuktitf.visibility = View.VISIBLE
            Picasso.get().load(foto).fit().centerCrop().into(imgfoto)
        }else{
            viewbuktitf.visibility = View.GONE
        }

        if (status == 0){
            viewpesanan.visibility = View.VISIBLE
        }
         if (status ==1){
             btnselesai.visibility = View.VISIBLE
        }

        btnterima.setOnClickListener {
            updatestatus(1,it)
        }

        btntolak.setOnClickListener {
            updatestatus(3,it)
        }

        btnselesai.setOnClickListener {
            updatestatus(2,it)
        }

        btback.setOnClickListener {
            finish()
        }

        btnchat.setOnClickListener {
            val kodenegara = telepon!!.substring(0,3)
            if (kodenegara == "+62"){
                val url = "https://api.whatsapp.com/send?phone=${telepon!!}"
                val i = Intent(Intent.ACTION_VIEW)
                i.setPackage("com.whatsapp")
                i.data = Uri.parse(url)
                startActivity(i)

            }else{
                val url = "https://api.whatsapp.com/send?phone=+62${telepon!!.drop(1)}"
                val i = Intent(Intent.ACTION_VIEW)
                i.setPackage("com.whatsapp")
                i.data = Uri.parse(url)
                startActivity(i)

            }
        }

        getcart()

    }
    fun getcart(){
        rvpesanan.layoutManager = LinearLayoutManager(this)
        rvpesanan.setHasFixedSize(true)
        (rvpesanan.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL

        api.getprodukcheckout(sessionManager.getDevice().toString(),nomorpesanan.toString())
            .enqueue(object : Callback<CartResponse> {
                override fun onResponse(
                    call: Call<CartResponse>,
                    response: Response<CartResponse>
                ) {
                    try {
                        if (response.isSuccessful) {
                            val notesList = mutableListOf<CartModel>()
                            val data = response.body()
                            for (hasil in data!!.data) {
                                notesList.add(hasil)
                                mAdapter =
                                    DetailPesananAdapter(notesList)
                                rvpesanan.adapter = mAdapter
                                mAdapter.notifyDataSetChanged()
                            }

                        } else {
                            toast("gagal mendapatkan response")
                        }
                    } catch (e: Exception) {
                        info { "dinda ${e.message}" }
                    }
                }

                override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                    info { "dinda ${t.message}" }
                }

            })

    }

    fun updatestatus(status : Int,view : View){
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setTitle(resources.getString(R.string.loading))
        progressDialog.show()

        api.updatestatuscheckout("Bearer ${sessionManager.getToken().toString()}", id!!,
            status
        ).enqueue(object :Callback<ResponseModel>{
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                try {
                    if (response.isSuccessful) {
                        if (response.body()!!.data == 1) {
                            progressDialog.dismiss()
                            if (status == 1){
                                toast("Pesanan diterima kirim pesanan sekarang")
                                finish()
                            }else if (status ==3)
                            {
                                toast("Pesanan ditolak kirim pesanan sekarang")
                                finish()
                            }else if (status ==2){
                                toast("Pesanan sudah selesai")
                                finish()
                            }
                            view.visibility = View.GONE
                        } else {
                            progressDialog.dismiss()
                            Snackbar.make(view, "Kesalahan pada aplikasi Silahkan hubungi admin", 3000).show()
                        }
                    } else {
                        progressDialog.dismiss()
                        Snackbar.make(view, "Kesalahan pada aplikasi Silahkan hubungi admin", 3000).show()
                    }
                } catch (e: Exception) {
                    progressDialog.dismiss()
                    info { "dinda ${e.message}" }
                    Snackbar.make(view, "Kesalahan pada aplikasi Silahkan hubungi admin", 3000).show()

                }
            }

            override fun onFailure(call: Call<ResponseModel>, e: Throwable) {
                progressDialog.dismiss()
                info { "dinda ${e.message}" }
                Snackbar.make(view, "Kesalahan pada jaringan", 3000).show()

            }

        })
    }
}