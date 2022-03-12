package com.satelit.satelitpaint.admin.ui.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.satelit.satelitpaint.PesananActivity
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.adapter.ProdukAdapter
import com.satelit.satelitpaint.adapter.ReportAdminAdapter
import com.satelit.satelitpaint.admin.RincianPesananAdminActivity
import com.satelit.satelitpaint.admin.RiwayatAdminActivity
import com.satelit.satelitpaint.databinding.FragmentReportBinding
import com.satelit.satelitpaint.model.PenghasilanModel
import com.satelit.satelitpaint.model.TransaksiAllResponse
import com.satelit.satelitpaint.model.TransaksiModel
import com.satelit.satelitpaint.session.SessionManager
import com.satelit.satelitpaint.webservice.ApiClient
import kotlinx.android.synthetic.main.fragment_report.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ReportFragment : Fragment(),AnkoLogger {
    lateinit var binding: FragmentReportBinding

    private lateinit var mAdapter: ReportAdminAdapter
    private var api = ApiClient.instance()
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report, container, false)
        binding.lifecycleOwner
        sessionManager = SessionManager(requireContext().applicationContext)
        binding.shimmerreport.startShimmer()



        binding.btnriwayat.setOnClickListener {
            startActivity<RiwayatAdminActivity>()
        }

        return binding.root
    }

     fun getreport() {

        binding.rvreport.layoutManager = LinearLayoutManager(requireContext().applicationContext)
        binding.rvreport.setHasFixedSize(true)
        (binding.rvreport.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL

        api.gettransaksiadmin(
            "Bearer ${sessionManager.getToken().toString()}"
        )
            .enqueue(object : Callback<TransaksiAllResponse> {
                override fun onResponse(
                    call: Call<TransaksiAllResponse>,
                    response: Response<TransaksiAllResponse>
                ) {
                    try {
                        if (response.isSuccessful) {
                            val notesList = mutableListOf<TransaksiModel>()
                            val data = response.body()
                            for (hasil in data!!.data) {
                                binding.shimmerreport.stopShimmer()
                                binding.rvreport.visibility = View.VISIBLE
                                binding.shimmerreport.visibility = View.GONE
                                notesList.add(hasil)
                                mAdapter =
                                    ReportAdminAdapter(notesList, requireContext().applicationContext)
                                binding.rvreport.adapter = mAdapter
                                mAdapter.notifyDataSetChanged()
                            }
                            if (data.data.isEmpty()){
                                binding.shimmerreport.stopShimmer()
                                binding.shimmerreport.visibility = View.GONE
                                binding.txtnodata.visibility = View.VISIBLE
                            }
                            mAdapter.setDialog(object : ReportAdminAdapter.Dialog {

                                override fun onClick(
                                    position: Int,
                                    nomorpesanan: String,
                                    nama: String,
                                    telepon: String,
                                    alamat: String,
                                    metodepembayaran: String,
                                    totalharga: Int,
                                    tanggal : String,
                                    jam : String,
                                    foto : String,
                                    status : Int,
                                    id: Int
                                ) {
                                    startActivity<RincianPesananAdminActivity>(
                                        "nomorpesanan" to nomorpesanan,
                                        "nama" to nama,
                                        "telepon" to telepon,
                                        "alamat" to alamat,
                                        "harga" to totalharga,
                                        "tanggal" to tanggal,
                                        "jam" to jam,
                                        "metodepembayaran" to metodepembayaran,
                                        "foto" to foto,
                                        "status" to status,
                                        "id" to id
                                    )

                                }

                            })
                        } else {
                            toast("gagal mendapatkan response")
                        }
                    } catch (e: Exception) {
                        info { "dinda ${e.message}" }
                    }
                }

                override fun onFailure(call: Call<TransaksiAllResponse>, t: Throwable) {
                    info { "dinda ${t.message}" }
                }

            })

    }

    override fun onStart() {
        super.onStart()
        getreport()
        totalpenghasilan()
        totalbulanini()
        totalhariini()
        totaltahunini()
    }

    fun totalpenghasilan(){
        api.gettotalpenghasilan("Bearer ${sessionManager.getToken().toString()}")
            .enqueue(object :Callback<PenghasilanModel>{
                override fun onResponse(
                    call: Call<PenghasilanModel>,
                    response: Response<PenghasilanModel>
                ) {
                    try {
                        if (response.isSuccessful){
                            txttotalpenghasilan.text = "Rp. ${response.body()!!.data}"
                        }else{
                            toast("ada masalah aplikasi")
                        }
                    }catch (e :Exception){
                            info { "dinda ${e.message}" }
                    }
                }

                override fun onFailure(call: Call<PenghasilanModel>, t: Throwable) {
                    toast("ada masalah jaringan")
                    info { "dinda ${t.message}" }

                }

            })
    }

    fun totaltahunini(){
        val gettahun = SimpleDateFormat("yyyy")
        val tahun = gettahun.format(Date())

        api.gettotalpenghasilantahunini("Bearer ${sessionManager.getToken().toString()}",tahun)
            .enqueue(object :Callback<PenghasilanModel>{
                override fun onResponse(
                    call: Call<PenghasilanModel>,
                    response: Response<PenghasilanModel>
                ) {
                    try {
                        if (response.isSuccessful){
                            txttahunini.text = "Rp. ${response.body()!!.data}"
                        }else{
                            toast("ada masalah aplikasi")
                        }
                    }catch (e :Exception){
                        info { "dinda ${e.message}" }
                    }
                }

                override fun onFailure(call: Call<PenghasilanModel>, t: Throwable) {
                    toast("ada masalah jaringan")
                    info { "dinda ${t.message}" }

                }

            })
    }

    fun totalbulanini(){
        val gettahun = SimpleDateFormat("yyyy")
        val getbulan = SimpleDateFormat("M")
        val tahun = gettahun.format(Date())
        val bulan = getbulan.format(Date())
        info { "dinda $tahun $bulan" }

        api.gettotalpenghasilanbulanini("Bearer ${sessionManager.getToken().toString()}",tahun,bulan)
            .enqueue(object :Callback<PenghasilanModel>{
                override fun onResponse(
                    call: Call<PenghasilanModel>,
                    response: Response<PenghasilanModel>
                ) {
                    try {
                        if (response.isSuccessful){
                            txtbulanini.text = "Rp. ${response.body()!!.data}"
                        }else{
                            toast("ada masalah aplikasi")
                        }
                    }catch (e :Exception){
                        info { "dinda ${e.message}" }
                    }
                }

                override fun onFailure(call: Call<PenghasilanModel>, t: Throwable) {
                    toast("ada masalah jaringan")
                    info { "dinda ${t.message}" }

                }

            })
    }

    fun totalhariini(){
        val gettahun = SimpleDateFormat("yyyy")
        val getbulan = SimpleDateFormat("M")
        val gethari = SimpleDateFormat("dd")
        val tahun = gettahun.format(Date())
        val bulan = getbulan.format(Date())
        val hari = gethari.format(Date())
        info { "dinda hariini $tahun $bulan $hari" }
        api.gettotalpenghasilanhariini("Bearer ${sessionManager.getToken().toString()}",tahun,bulan,hari)
            .enqueue(object :Callback<PenghasilanModel>{
                override fun onResponse(
                    call: Call<PenghasilanModel>,
                    response: Response<PenghasilanModel>
                ) {
                    try {
                        if (response.isSuccessful){
                            txtharini.text = "Rp. ${response.body()!!.data}"
                        }else{
                            toast("ada masalah aplikasi")
                        }
                    }catch (e :Exception){
                        info { "dinda ${e.message}" }
                    }
                }

                override fun onFailure(call: Call<PenghasilanModel>, t: Throwable) {
                    toast("ada masalah jaringan")
                    info { "dinda hari ini ${t.message}" }

                }

            })
    }


}