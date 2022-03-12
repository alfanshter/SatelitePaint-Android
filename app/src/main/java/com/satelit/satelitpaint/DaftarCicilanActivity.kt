package com.satelit.satelitpaint

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.satelit.satelitpaint.adapter.DaftarCicilanAdminAdapter
import com.satelit.satelitpaint.databinding.ActivityDaftarCicilanBinding
import com.satelit.satelitpaint.databinding.FragmentUpdateJatuhTempoBinding
import com.satelit.satelitpaint.model.DaftarCicilanModel
import com.satelit.satelitpaint.model.DaftarCicilanResponse
import com.satelit.satelitpaint.model.ResponseModel
import com.satelit.satelitpaint.session.SessionManager
import com.satelit.satelitpaint.webservice.ApiClient
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class DaftarCicilanActivity : AppCompatActivity(), AnkoLogger {
    var nomorpesanan: String? = null
    var role: String? = null
    lateinit var binding: ActivityDaftarCicilanBinding
    private lateinit var mAdapter: DaftarCicilanAdminAdapter
    lateinit var sessionManager: SessionManager
    private var api = ApiClient.instance()
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_daftar_cicilan)
        binding.lifecycleOwner

        val bundle: Bundle? = intent.extras
        nomorpesanan = bundle!!.getString("nomorpesanan")
        role = bundle.getString("role")

        progressDialog = ProgressDialog(this)
        sessionManager = SessionManager(this)
        binding.shimmertransaksi.startShimmer()

        binding.btnlunas.setOnClickListener {

            val builder =
                AlertDialog.Builder(this@DaftarCicilanActivity)
            builder.setTitle("Cicilan")
            builder.setMessage("Piih aksi? ")
            builder.setPositiveButton("tandai sudah lunas") { dialog, which ->
                progressDialog.setTitle("Loading..")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                api.finishcicilan(nomorpesanan!!)
                    .enqueue(object : Callback<ResponseModel> {
                        override fun onResponse(
                            call: Call<ResponseModel>,
                            response: Response<ResponseModel>
                        ) {
                            try {
                                if (response.isSuccessful) {
                                    if (response.body()!!.data == 1) {
                                        progressDialog.dismiss()
                                        onStart()
                                        toast("Cicilan sudah lunas")
                                    }else {
                                        progressDialog.dismiss()
                                        toast("Sudah di lunasi")
                                    }
                                } else {
                                    progressDialog.dismiss()
                                    toast("Kesalahan sistem")
                                }
                            } catch (e: Exception) {
                                progressDialog.dismiss()
                                info { "dinda ${e.message}" }
                            }
                        }

                        override fun onFailure(
                            call: Call<ResponseModel>,
                            t: Throwable
                        ) {
                            progressDialog.dismiss()
                            toast("Kesalahan Jaringan")
                            info { "dinda ${t.message}" }
                        }

                    })
            }

            builder.setNegativeButton("batal") { dialog, which ->

            }

            builder.show()




        }

    }

    override fun onStart() {
        super.onStart()
        getcicilan()
        if (role=="admin"){
            getpelunasan()
        }

    }

    fun getpelunasan(){
        api.getsumcicilanakun(nomorpesanan!!)
            .enqueue(object : Callback<ResponseModel> {
                override fun onResponse(
                    call: Call<ResponseModel>,
                    response: Response<ResponseModel>
                ) {
                    try {
                        if (response.isSuccessful) {
                            if (response.body()!!.data == 1) {
                                binding.btnlunas.visibility = View.VISIBLE
                            }else{
                                binding.btnlunas.visibility = View.GONE
                            }
                        } else {
                            progressDialog.dismiss()
                            toast("Kesalahan sistem")
                        }
                    } catch (e: Exception) {
                        progressDialog.dismiss()
                        info { "dinda ${e.message}" }
                    }
                }

                override fun onFailure(
                    call: Call<ResponseModel>,
                    t: Throwable
                ) {
                    progressDialog.dismiss()
                    toast("Kesalahan Jaringan")
                    info { "dinda ${t.message}" }
                }

            })
    }

    fun getcicilan() {
        binding.rvdaftarcicilan.layoutManager = LinearLayoutManager(this)
        binding.rvdaftarcicilan.setHasFixedSize(true)
        (binding.rvdaftarcicilan.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL

        api.readbayarcicilan(nomorpesanan!!)
            .enqueue(object : Callback<DaftarCicilanResponse> {
                override fun onResponse(
                    call: Call<DaftarCicilanResponse>,
                    response: Response<DaftarCicilanResponse>
                ) {
                    try {
                        if (response.isSuccessful) {
                            if (response.body()!!.data!!.isEmpty()) {
                                binding.shimmertransaksi.startShimmer()
                                binding.shimmertransaksi.visibility = View.GONE
                                binding.txtnodata.visibility = View.VISIBLE
                            } else {
                                binding.shimmertransaksi.startShimmer()
                                binding.shimmertransaksi.visibility = View.GONE
                                binding.rvdaftarcicilan.visibility = View.VISIBLE
                                val notesList = mutableListOf<DaftarCicilanModel>()
                                val data = response.body()
                                for (hasil in data!!.data!!) {
                                    notesList.add(hasil)
                                    mAdapter =
                                        DaftarCicilanAdminAdapter(
                                            notesList,
                                            this@DaftarCicilanActivity
                                        )
                                    binding.rvdaftarcicilan.adapter = mAdapter
                                    mAdapter.setDialog(object : DaftarCicilanAdminAdapter.Dialog {
                                        override fun onClick(
                                            position: Int,
                                            note: DaftarCicilanModel
                                        ) {
                                            if (note.status == 0) {
                                                val builder =
                                                    AlertDialog.Builder(this@DaftarCicilanActivity)
                                                builder.setTitle("Cicilan")
                                                builder.setMessage("Piih aksi? ")
                                                builder.setPositiveButton("Sudah Bayar") { dialog, which ->
                                                    progressDialog.setTitle("Loading..")
                                                    progressDialog.setCanceledOnTouchOutside(false)
                                                    progressDialog.show()

                                                    api.updatesudahbayar(note.id!!, 1)
                                                        .enqueue(object : Callback<ResponseModel> {
                                                            override fun onResponse(
                                                                call: Call<ResponseModel>,
                                                                response: Response<ResponseModel>
                                                            ) {
                                                                try {
                                                                    if (response.isSuccessful) {
                                                                        if (response.body()!!.data == 1) {
                                                                            progressDialog.dismiss()
                                                                            onStart()
                                                                            toast("Cicilan terbayar")
                                                                        }
                                                                    } else {
                                                                        progressDialog.dismiss()
                                                                        toast("Kesalahan sistem")
                                                                    }
                                                                } catch (e: Exception) {
                                                                    progressDialog.dismiss()
                                                                    info { "dinda ${e.message}" }
                                                                }
                                                            }

                                                            override fun onFailure(
                                                                call: Call<ResponseModel>,
                                                                t: Throwable
                                                            ) {
                                                                progressDialog.dismiss()
                                                                toast("Kesalahan Jaringan")
                                                                info { "dinda ${t.message}" }
                                                            }

                                                        })
                                                }

                                                builder.setNegativeButton("Perpanjang") { dialog, which ->
                                                    val dialogBinding: FragmentUpdateJatuhTempoBinding? =
                                                        DataBindingUtil.inflate(
                                                            LayoutInflater.from(this@DaftarCicilanActivity),
                                                            R.layout.fragment_update_jatuh_tempo,
                                                            null,
                                                            false
                                                        )

                                                    val customDialog =
                                                        AlertDialog.Builder(
                                                            this@DaftarCicilanActivity,
                                                            0
                                                        ).create()

                                                    customDialog.apply {
                                                        window?.setBackgroundDrawable(
                                                            ColorDrawable(
                                                                Color.TRANSPARENT
                                                            )
                                                        )
                                                        setView(dialogBinding?.root)
                                                        setCancelable(false)
                                                    }.show()

                                                    dialogBinding?.btnbatal?.setOnClickListener {
                                                        customDialog.dismiss()
                                                    }

                                                    dialogBinding!!.btnupdate.setOnClickListener {
                                                        var jumlah =
                                                            dialogBinding.edtjumlah.text.toString()
                                                                .trim()
                                                        if (jumlah.isNotEmpty()) {
                                                            progressDialog.setTitle("Loading..")
                                                            progressDialog.setCanceledOnTouchOutside(
                                                                false
                                                            )
                                                            progressDialog.show()
                                                            //string to date
                                                            //creating date format
                                                            //creating date format
                                                            val dateFormat =
                                                                SimpleDateFormat("yyyy-MM-dd")
                                                            //parsing string to date using parse() method
                                                            //parsing string to date using parse() method
                                                            var parsedDate =
                                                                dateFormat.parse(note.jatuhtempo)
                                                            //finally creating a timestamp


                                                            //plus day

                                                            val c: Calendar = Calendar.getInstance()
                                                            c.time = parsedDate
                                                            c.add(Calendar.DATE, jumlah.toInt())
                                                            parsedDate = c.time

                                                            var hasil =
                                                                dateFormat.format(parsedDate)


                                                            api.updatejatuhtempo(note.id!!, hasil)
                                                                .enqueue(object :
                                                                    Callback<ResponseModel> {
                                                                    override fun onResponse(
                                                                        call: Call<ResponseModel>,
                                                                        response: Response<ResponseModel>
                                                                    ) {
                                                                        try {
                                                                            if (response.isSuccessful) {
                                                                                if (response.body()!!.data == 1) {
                                                                                    progressDialog.dismiss()
                                                                                    onStart()
                                                                                    toast("Jatuh tempo di perbarui")
                                                                                }
                                                                            } else {
                                                                                progressDialog.dismiss()
                                                                                toast("Kesalahan sistem")
                                                                            }
                                                                        } catch (e: Exception) {
                                                                            progressDialog.dismiss()
                                                                            info { "dinda ${e.message}" }
                                                                        }
                                                                    }

                                                                    override fun onFailure(
                                                                        call: Call<ResponseModel>,
                                                                        t: Throwable
                                                                    ) {
                                                                        progressDialog.dismiss()
                                                                        toast("Kesalahan Jaringan")
                                                                        info { "dinda ${t.message}" }
                                                                    }

                                                                })
                                                        } else {
                                                            toast("jangan kosongi kolom")
                                                        }

                                                    }

                                                }

                                                builder.show()

                                            } else {
                                                toast("Sudah terbayar")
                                            }

                                        }

                                        override fun info(status: Int, note: DaftarCicilanModel) {

                                        }

                                    })
                                    mAdapter.notifyDataSetChanged()
                                }
                            }

                        } else {
                            toast("gagal mendapatkan response")
                        }
                    } catch (e: Exception) {
                        info { "dinda ${e.message}" }
                    }
                }

                override fun onFailure(call: Call<DaftarCicilanResponse>, t: Throwable) {
                    info { "dinda ${t.message}" }
                }

            })

    }
}