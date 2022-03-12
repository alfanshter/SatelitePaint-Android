package com.satelit.satelitpaint

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.satelit.satelitpaint.adapter.CartAdapter
import com.satelit.satelitpaint.adapter.ProdukCustomerAdapter
import com.satelit.satelitpaint.admin.HomeAdminActivity
import com.satelit.satelitpaint.database.NoteDB
import com.satelit.satelitpaint.database.entitas.Checkout
import com.satelit.satelitpaint.database.entitas.Note
import com.satelit.satelitpaint.model.*
import com.satelit.satelitpaint.session.SessionManager
import com.satelit.satelitpaint.webservice.ApiClient
import kotlinx.android.synthetic.main.activity_keranjang.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.NumberFormat

class KeranjangActivity : AppCompatActivity(), AnkoLogger {

    //adapter
    private var mAdapter: CartAdapter? = null

    val db by lazy {
        NoteDB(this)
    }

    private val api = ApiClient.instance()
    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    var hargatotal : Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keranjang)
        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)
        getcart()

        btncheckout.setOnClickListener {
            if (hargatotal!=null){
                startActivity<DataPembeliActivity>("hargatotal" to hargatotal)
                finish()
            }else{
                Snackbar.make(it,"Harap belanja dahulu",3000).show()
            }
        }

        btnback.setOnClickListener {
            finish()
        }
    }

    fun getcart() {


        rvcart.layoutManager = LinearLayoutManager(this)
        rvcart.setHasFixedSize(true)
        (rvcart.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL

        api.getcart(sessionManager.getDevice().toString())
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
                                    CartAdapter(notesList, this@KeranjangActivity)
                                rvcart.adapter = mAdapter
                                mAdapter!!.notifyDataSetChanged()
                            }
                            mAdapter!!.setDialog(object : CartAdapter.Dialog {

                                override fun onClick(
                                    position: Int,
                                    nama: String,
                                    harga: Int,
                                    deskripsi: String,
                                    foto: String,
                                    rating: Float,
                                    stok: Int,
                                    id: Int
                                ) {
                                    TODO("Not yet implemented")
                                }

                                override fun ceklist(position: Int, id: Int, cek: CheckBox) {
                                    progressDialog.setCanceledOnTouchOutside(false)
                                    progressDialog.setTitle(resources.getString(R.string.loading))
                                    progressDialog.show()
                                    if (cek.isChecked) {
                                        api.pickcart(id, 1)
                                            .enqueue(object : Callback<ResponseModel> {
                                                override fun onResponse(
                                                    call: Call<ResponseModel>,
                                                    response: Response<ResponseModel>
                                                ) {
                                                    try {
                                                        if (response.isSuccessful) {
                                                            if (response.body()!!.data == 1) {
                                                                progressDialog.dismiss()
                                                                getcart()
                                                                toast("telah dipilih")
                                                            } else {
                                                                cek.isChecked = false
                                                                progressDialog.dismiss()
                                                                toast("kesalahan sistem")
                                                            }
                                                        } else {
                                                            progressDialog.dismiss()
                                                            cek.isChecked = false
                                                            toast("kesalahan sistem")
                                                        }
                                                    } catch (e: Exception) {
                                                        progressDialog.dismiss()
                                                        cek.isChecked = false
                                                        info { "dinda ${e.message}" }
                                                        toast("kesalahan Aplkasi")

                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<ResponseModel>,
                                                    t: Throwable
                                                ) {
                                                    progressDialog.dismiss()
                                                    cek.isChecked = false
                                                    toast("kesalahan jaringan")
                                                }

                                            })
                                    }else{
                                        api.pickcart(id, 0)
                                            .enqueue(object : Callback<ResponseModel> {
                                                override fun onResponse(
                                                    call: Call<ResponseModel>,
                                                    response: Response<ResponseModel>
                                                ) {
                                                    try {
                                                        if (response.isSuccessful) {
                                                            if (response.body()!!.data == 1) {
                                                                progressDialog.dismiss()
                                                                getcart()
                                                                toast("tidak dipilih")
                                                            } else {
                                                                progressDialog.dismiss()
                                                                toast("kesalahan sistem")
                                                            }
                                                        } else {
                                                            progressDialog.dismiss()
                                                            toast("kesalahan sistem")
                                                        }
                                                    } catch (e: Exception) {
                                                        progressDialog.dismiss()
                                                        info { "dinda ${e.message}" }
                                                        toast("kesalahan Aplkasi")

                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<ResponseModel>,
                                                    t: Throwable
                                                ) {
                                                    progressDialog.dismiss()
                                                    toast("kesalahan jaringan")
                                                }

                                            })

                                    }
                                }

                                override fun plus(id: Int, jumlah: Int,counter : TextView) {
                                    progressDialog.setCanceledOnTouchOutside(false)
                                    progressDialog.setTitle(resources.getString(R.string.loading))
                                    progressDialog.show()
                                    api.updatejumlah(id,jumlah + 1).enqueue(object : Callback<ResponseModel>{
                                        override fun onResponse(
                                            call: Call<ResponseModel>,
                                            response: Response<ResponseModel>
                                        ) {
                                            try {
                                                if (response.isSuccessful) {
                                                    if (response.body()!!.data == 1) {
                                                        counter.text = "${jumlah + 1}"
                                                        getcart()
                                                        progressDialog.dismiss()
                                                    } else {
                                                        progressDialog.dismiss()
                                                        toast("kesalahan sistem")
                                                    }
                                                } else {
                                                    progressDialog.dismiss()
                                                    toast("kesalahan sistem")
                                                }
                                            } catch (e: Exception) {
                                                progressDialog.dismiss()
                                                info { "dinda ${e.message}" }
                                                toast("kesalahan Aplkasi")

                                            }

                                        }

                                        override fun onFailure(
                                            call: Call<ResponseModel>,
                                            e: Throwable
                                        ) {
                                            progressDialog.dismiss()
                                            info { "dinda ${e.message}" }
                                            toast("kesalahan Jaringan")

                                        }

                                    })
                                }

                                override fun min(id: Int, jumlah: Int,counter : TextView) {
                                    progressDialog.setCanceledOnTouchOutside(false)
                                    progressDialog.setTitle(resources.getString(R.string.loading))
                                    progressDialog.show()
                                    if (jumlah >0){
                                        api.updatejumlah(id,jumlah - 1).enqueue(object : Callback<ResponseModel>{
                                            override fun onResponse(
                                                call: Call<ResponseModel>,
                                                response: Response<ResponseModel>
                                            ) {
                                                try {
                                                    if (response.isSuccessful) {
                                                        if (response.body()!!.data == 1) {
                                                            counter.text = "${jumlah - 1}"
                                                            getcart()
                                                            progressDialog.dismiss()
                                                        } else {
                                                            progressDialog.dismiss()
                                                            toast("kesalahan sistem")
                                                        }
                                                    } else {
                                                        progressDialog.dismiss()
                                                        toast("kesalahan sistem")
                                                    }
                                                } catch (e: Exception) {
                                                    progressDialog.dismiss()
                                                    info { "dinda ${e.message}" }
                                                    toast("kesalahan Aplkasi")

                                                }

                                            }

                                            override fun onFailure(
                                                call: Call<ResponseModel>,
                                                e: Throwable
                                            ) {
                                                progressDialog.dismiss()
                                                info { "dinda ${e.message}" }
                                                toast("kesalahan Jaringan")

                                            }

                                        })
                                    }
                                    else{
                                        progressDialog.dismiss()
                                    }
                                }

                                override fun hapus(id: Int) {
                                    progressDialog.setCanceledOnTouchOutside(false)
                                    progressDialog.setTitle(resources.getString(R.string.loading))
                                    progressDialog.show()
                                    api.deletecart(id).enqueue(object : Callback<ResponseModel>{
                                        override fun onResponse(
                                            call: Call<ResponseModel>,
                                            response: Response<ResponseModel>
                                        ) {
                                            try {
                                                if (response.isSuccessful) {
                                                    if (response.body()!!.data == 1) {
                                                        getcart()
                                                        progressDialog.dismiss()
                                                    } else {
                                                        progressDialog.dismiss()
                                                        toast("kesalahan sistem")
                                                    }
                                                } else {
                                                    progressDialog.dismiss()
                                                    toast("kesalahan sistem")
                                                }
                                            } catch (e: Exception) {
                                                progressDialog.dismiss()
                                                info { "dinda ${e.message}" }
                                                toast("kesalahan Aplkasi")

                                            }

                                        }

                                        override fun onFailure(
                                            call: Call<ResponseModel>,
                                            e: Throwable
                                        ) {
                                            progressDialog.dismiss()
                                            info { "dinda ${e.message}" }
                                            toast("kesalahan Jaringan")

                                        }

                                    })

                                }


                            })
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

        api.sumcart(sessionManager.getDevice().toString()).enqueue(object : Callback<ResponseModel>{
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                try {
                    if (response.isSuccessful) {
                        var jumlah = response.body()!!.data
                        //convert to money
                        val formatter: NumberFormat = DecimalFormat("#,###")
                        val myNumber = response.body()!!.data
                        val convertjumlah: String = formatter.format(myNumber)

                        var voucher = 0
                         hargatotal = jumlah!! - voucher
                        val formathargatotal = hargatotal
                        val converthargatotal: String = formatter.format(formathargatotal)

                        txtdipilih.text = "Rp. ${convertjumlah}"
                        txttotal.text =  "Rp. ${converthargatotal}"
                    } else {
                        toast("kesalahan sistem")
                    }
                } catch (e: Exception) {
                    info { "dinda ${e.message}" }
                    toast("kesalahan Aplkasi")

                }

            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })


    }

    override fun onStart() {
        super.onStart()

    }
}