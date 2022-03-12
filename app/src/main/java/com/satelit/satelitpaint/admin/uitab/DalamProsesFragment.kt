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
import com.satelit.satelitpaint.databinding.FragmentDalamProsesBinding
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


class DalamProsesFragment : Fragment(),AnkoLogger {
    private lateinit var mAdapter: ReportAdminAdapter
    private var api = ApiClient.instance()
    lateinit var sessionManager: SessionManager

    lateinit var binding : FragmentDalamProsesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_dalam_proses,container,false)
        binding.lifecycleOwner
        sessionManager = SessionManager(requireContext().applicationContext)

        binding.shimmerdalamproses.startShimmer()

        getreport()

        return binding.root

    }


    fun getreport() {

        binding.rvdalamproses.layoutManager = LinearLayoutManager(requireContext().applicationContext)
        binding.rvdalamproses.setHasFixedSize(true)
        (binding.rvdalamproses.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL

        api.gettransaksibystatusadmin(
            "Bearer ${sessionManager.getToken().toString()}",0
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
                                binding.shimmerdalamproses.stopShimmer()
                                binding.rvdalamproses.visibility = View.VISIBLE
                                binding.shimmerdalamproses.visibility = View.GONE
                                notesList.add(hasil)
                                mAdapter =
                                    ReportAdminAdapter(notesList, requireContext().applicationContext)
                                binding.rvdalamproses.adapter = mAdapter
                                mAdapter.notifyDataSetChanged()
                            }
                            if (data.data.isEmpty()){
                                binding.shimmerdalamproses.stopShimmer()
                                binding.shimmerdalamproses.visibility = View.GONE
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