package com.satelit.satelitpaint.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.satelit.satelitpaint.DaftarCicilanActivity
import com.satelit.satelitpaint.PesananActivity
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.adapter.CicilanAdminAdapter
import com.satelit.satelitpaint.adapter.CicilanUserAdapter
import com.satelit.satelitpaint.adapter.RiwayatTransaksiCustomerAdapter
import com.satelit.satelitpaint.admin.DetailCicilanActivity
import com.satelit.satelitpaint.databinding.FragmentCicilanUserBinding
import com.satelit.satelitpaint.databinding.FragmentTransaksiCustomerBinding
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

class CicilanUserFragment : Fragment(),AnkoLogger {
    private lateinit var mAdapter: CicilanUserAdapter
    lateinit var binding: FragmentCicilanUserBinding
    lateinit var sessionManager: SessionManager
    private var api = ApiClient.instance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_cicilan_user,
            container,
            false
        )

        sessionManager = SessionManager(requireContext().applicationContext)
        binding.shimmertransaksi.startShimmer()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.rvcicilan.layoutManager = LinearLayoutManager(requireContext().applicationContext)
        binding.rvcicilan.setHasFixedSize(true)
        (binding.rvcicilan.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL

        api.getcicilanakun(sessionManager.getDevice().toString())
            .enqueue(object : Callback<CicilanResponse> {
                override fun onResponse(
                    call: Call<CicilanResponse>,
                    response: Response<CicilanResponse>
                ) {
                    try {
                        if (response.isSuccessful) {
                            if (response.body()!!.data!!.isEmpty()){
                                binding.shimmertransaksi.startShimmer()
                                binding.shimmertransaksi.visibility = View.GONE
                                binding.txtnodata.visibility = View.VISIBLE
                            }else{
                                binding.shimmertransaksi.startShimmer()
                                binding.shimmertransaksi.visibility = View.GONE
                                binding.rvcicilan.visibility = View.VISIBLE
                                val notesList = mutableListOf<CicilanModel>()
                                val data = response.body()
                                for (hasil in data!!.data!!) {
                                    notesList.add(hasil)
                                    mAdapter =
                                        CicilanUserAdapter(notesList, requireContext().applicationContext)
                                    binding.rvcicilan.adapter = mAdapter
                                    mAdapter.setDialog(object : CicilanUserAdapter.Dialog {
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
                                                    "role" to "user"
                                                )


                                            }

                                            builder.setNegativeButton("lihat cicilan") { dialog, which ->
                                                startActivity<DaftarCicilanActivity>("nomorpesanan" to note.nomorpesanan, "role" to "user")

                                            }

                                            builder.show()

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

                override fun onFailure(call: Call<CicilanResponse>, t: Throwable) {
                    info { "dinda ${t.message}" }
                }

            })

    }
}