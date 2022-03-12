package com.satelit.satelitpaint.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.satelit.satelitpaint.DetailProdukActivity
import com.satelit.satelitpaint.KeranjangActivity
import com.satelit.satelitpaint.PesananActivity
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.adapter.RiwayatTransaksiCustomerAdapter
import com.satelit.satelitpaint.databinding.FragmentHomeBinding
import com.satelit.satelitpaint.databinding.FragmentTransaksiCustomerBinding
import com.satelit.satelitpaint.model.TransaksiModel
import com.satelit.satelitpaint.model.ResponseProduk
import com.satelit.satelitpaint.model.TransaksiAllResponse
import com.satelit.satelitpaint.session.SessionManager
import com.satelit.satelitpaint.webservice.ApiClient
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TransaksiCustomerFragment : Fragment(),AnkoLogger {

    private lateinit var mAdapter: RiwayatTransaksiCustomerAdapter
    lateinit var binding: FragmentTransaksiCustomerBinding
    lateinit var sessionManager: SessionManager
    private var api = ApiClient.instance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_transaksi_customer,
            container,
            false
        )

        sessionManager = SessionManager(requireContext().applicationContext)
        binding.shimmertransaksi.startShimmer()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.rvriwayat.layoutManager = LinearLayoutManager(requireContext().applicationContext)
        binding.rvriwayat.setHasFixedSize(true)
        (binding.rvriwayat.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL

        api.gettransaksibyidusers(sessionManager.getDevice().toString())
            .enqueue(object : Callback<TransaksiAllResponse> {
                override fun onResponse(
                    call: Call<TransaksiAllResponse>,
                    response: Response<TransaksiAllResponse>
                ) {
                    try {
                        if (response.isSuccessful) {
                            if (response.body()!!.data.isEmpty()){
                                binding.shimmertransaksi.startShimmer()
                                binding.shimmertransaksi.visibility = View.GONE
                                binding.txtnodata.visibility = View.VISIBLE
                            }else{
                                binding.shimmertransaksi.startShimmer()
                                binding.shimmertransaksi.visibility = View.GONE
                                binding.rvriwayat.visibility = View.VISIBLE
                                val notesList = mutableListOf<TransaksiModel>()
                                val data = response.body()
                                for (hasil in data!!.data) {
                                    notesList.add(hasil)
                                    mAdapter =
                                        RiwayatTransaksiCustomerAdapter(notesList, requireContext().applicationContext)
                                    binding.rvriwayat.adapter = mAdapter
                                    mAdapter.notifyDataSetChanged()
                                }
                                mAdapter.setDialog(object : RiwayatTransaksiCustomerAdapter.Dialog {

                                    override fun onClick(
                                        position: Int,
                                        nomorpesanan: String,
                                        nama: String,
                                        telepon: String,
                                        alamat: String,
                                        metodepembayaran: String,
                                        totalharga: Int
                                    ) {
                                        startActivity<PesananActivity>(
                                            "nomorpesanan" to nomorpesanan,
                                            "nama" to nama,
                                            "telepon" to telepon,
                                            "alamat" to alamat,
                                            "harga" to totalharga

                                        )

                                    }

                                })
                            }

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