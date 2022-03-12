package com.satelit.satelitpaint.admin.uitab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.adapter.ReportAdminAdapter
import com.satelit.satelitpaint.admin.RincianPesananAdminActivity
import com.satelit.satelitpaint.databinding.FragmentPengirimanBinding
import com.satelit.satelitpaint.databinding.FragmentSelesaiProsesBinding
import com.satelit.satelitpaint.model.TransaksiAllResponse
import com.satelit.satelitpaint.model.TransaksiModel
import com.satelit.satelitpaint.session.SessionManager
import com.satelit.satelitpaint.webservice.ApiClient
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PengirimanFragment : Fragment(), AnkoLogger {
    private lateinit var mAdapter: ReportAdminAdapter
    private var api = ApiClient.instance()
    lateinit var sessionManager: SessionManager

    lateinit var binding : FragmentPengirimanBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_pengiriman,container,false)
        binding.lifecycleOwner

        sessionManager = SessionManager(requireContext().applicationContext)

        binding.shimmerselesaiproses.startShimmer()

        getreport()

        return binding.root

    }

    fun getreport() {

        binding.rvselesai.layoutManager = LinearLayoutManager(requireContext().applicationContext)
        binding.rvselesai.setHasFixedSize(true)
        (binding.rvselesai.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL

        api.gettransaksibystatusadmin(
            "Bearer ${sessionManager.getToken().toString()}",1
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
                                binding.shimmerselesaiproses.stopShimmer()
                                binding.rvselesai.visibility = View.VISIBLE
                                binding.shimmerselesaiproses.visibility = View.GONE
                                notesList.add(hasil)
                                mAdapter =
                                    ReportAdminAdapter(notesList, requireContext().applicationContext)
                                binding.rvselesai.adapter = mAdapter
                                mAdapter.notifyDataSetChanged()
                            }
                            if (data.data.isEmpty()){
                                binding.shimmerselesaiproses.stopShimmer()
                                binding.shimmerselesaiproses.visibility = View.GONE
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

}