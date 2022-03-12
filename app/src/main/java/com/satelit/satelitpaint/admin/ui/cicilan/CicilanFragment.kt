package com.satelit.satelitpaint.admin.ui.cicilan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.satelit.satelitpaint.DaftarCicilanActivity
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.adapter.CicilanAdminAdapter
import com.satelit.satelitpaint.admin.BayarCicilanActivity
import com.satelit.satelitpaint.admin.DetailCicilanActivity
import com.satelit.satelitpaint.admin.RincianPesananAdminActivity
import com.satelit.satelitpaint.admin.RiwayatAdminActivity
import com.satelit.satelitpaint.databinding.FragmentCicilanBinding
import com.satelit.satelitpaint.model.CicilanResponse
import com.satelit.satelitpaint.model.CicilanModel
import com.satelit.satelitpaint.session.SessionManager
import com.satelit.satelitpaint.webservice.ApiClient
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CicilanFragment : Fragment(),AnkoLogger {
    lateinit var binding: FragmentCicilanBinding

    private lateinit var mAdapter: CicilanAdminAdapter
    private var api = ApiClient.instance()
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cicilan, container, false)
        binding.lifecycleOwner
        sessionManager = SessionManager(requireContext().applicationContext)
        binding.shimmerreport.startShimmer()
        
        binding.btnriwayat.setOnClickListener {
            startActivity<RiwayatAdminActivity>()
        }

        return binding.root
        
    }

    fun getreport() {

        binding.rvcicilan.layoutManager = LinearLayoutManager(requireContext().applicationContext)
        binding.rvcicilan.setHasFixedSize(true)
        (binding.rvcicilan.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL

        api.getcicilan()
            .enqueue(object : Callback<CicilanResponse> {
                override fun onResponse(
                    call: Call<CicilanResponse>,
                    response: Response<CicilanResponse>
                ) {
                    try {
                        if (response.isSuccessful) {
                            val notesList = mutableListOf<CicilanModel>()
                            val data = response.body()!!.data
                            for (hasil in data!!) {
                                binding.shimmerreport.stopShimmer()
                                binding.rvcicilan.visibility = View.VISIBLE
                                binding.shimmerreport.visibility = View.GONE
                                notesList.add(hasil)
                                mAdapter =
                                    CicilanAdminAdapter(notesList, requireContext().applicationContext)
                                binding.rvcicilan.adapter = mAdapter
                                mAdapter.notifyDataSetChanged()
                            }
                            if (data.isEmpty()){
                                binding.shimmerreport.stopShimmer()
                                binding.shimmerreport.visibility = View.GONE
                                binding.txtnodata.visibility = View.VISIBLE
                            }
                            mAdapter.setDialog(object : CicilanAdminAdapter.Dialog {
                                override fun onClick(position: Int, note: CicilanModel) {
                                    val builder = AlertDialog.Builder(requireActivity())
                                    builder.setTitle("Cicilan")
                                    builder.setMessage("Pilih aksi ? ")
                                    builder.setPositiveButton("lihat pesanan") { dialog, which ->
                                            startActivity<DetailCicilanActivity>(
                                                "nomorpesanan" to note.nomorpesanan,
                                                "nama" to note.nama,
                                                "telepon" to note.telepon,
                                                "alamat" to note.alamat,
                                                "harga" to note.totalharga,
                                                "tanggal" to note.tanggal,
                                                "jam" to note.jam,
                                                "metodepembayaran" to note.metodepembayaran,
                                                "foto" to note.foto,
                                                "status" to note.status,
                                                "jumlahcicilan" to note.jumlahcicilan,
                                                "hargacicilan" to note.hargacicilan,
                                                "id" to note.id,
                                                "role" to "admin"
                                            )


                                    }

                                    builder.setNegativeButton("lihat cicilan") { dialog, which ->
                                        startActivity<DaftarCicilanActivity>("nomorpesanan" to note.nomorpesanan,"role" to "admin")

                                    }

                                    builder.show()

                                }


                            })
                        } else {
                            toast("gagal mendapatkan response")
                        }
                    } catch (e: Exception) {
                        info { "dinda ${e.message}" }
                    }
                }

                override fun onFailure(call: Call<CicilanResponse>, t: Throwable) {
                    info { "dinda ${t.message}" }
                }

            })

    }

    override fun onStart() {
        super.onStart()
        getreport()

    }


}