package com.satelit.satelitpaint.admin.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.satelit.satelitpaint.MainActivity
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.adapter.ProdukAdapter
import com.satelit.satelitpaint.adapter.ShowSliderAdapter
import com.satelit.satelitpaint.admin.*
import com.satelit.satelitpaint.databinding.FragmentHomeadminBinding
import com.satelit.satelitpaint.model.*
import com.satelit.satelitpaint.session.SessionManager
import com.satelit.satelitpaint.webservice.ApiClient
import com.smarteist.autoimageslider.SliderView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.popup_logout.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeAdminFragment : Fragment(), AnkoLogger {
    private lateinit var mAdapter: ProdukAdapter
    lateinit var binding: FragmentHomeadminBinding
    private var api = ApiClient.instance()
    lateinit var sessionManager: SessionManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_homeadmin, container, false)
        sessionManager = SessionManager(requireContext().applicationContext)

        binding.shimmergambar.startShimmer()
        binding.btnadd.setOnClickListener {
            startActivity<TambahProdukActivity>()
        }

        binding.btnslider.setOnClickListener {
            startActivity<SliderAdminActivity>()
        }

        binding.textInputEditText.setOnClickListener {
            startActivity<SearchProdukAdminActivity>()
        }

        binding.btngambar.setOnClickListener {
            startActivity<EditGambarActivity>()
        }

        binding.btnadmin.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.popup_logout, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(requireActivity())
                .setView(mDialogView)
            //show dialog
            val  mAlertDialog = mBuilder.show()
            //login button click of custom layout
            mDialogView.btnlogout.setOnClickListener {
                //dismiss dialog
                startActivity<MainActivity>()
                activity!!.finish()
                 sessionManager.setLogin(false)
                 sessionManager.setToken("")
                mAlertDialog.dismiss()
            }

            mDialogView.btnclose.setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
            }

        }


        getslider()
        gambar()


        return binding.root
    }

    fun getslider(){
        val imageList: ArrayList<String> = ArrayList()
        val linkList: ArrayList<String> = ArrayList()
        var slidermodel = mutableListOf<SliderModel>()
        api.getslider()
            .enqueue(object : Callback<SliderResponse> {
                override fun onResponse(
                    call: Call<SliderResponse>,
                    response: Response<SliderResponse>
                ) {
                    try {
                        if (response.isSuccessful) {
                            val notesList = mutableListOf<SliderModel>()
                            val data = response.body()
                            for (hasil in data!!.data) {
                                imageList.add(hasil.foto.toString())
                                linkList.add(hasil.foto.toString())
                                slidermodel.add(hasil)
                            }
                            setImageInSlider(imageList, binding.imageSlider,linkList,slidermodel)
                        } else {
                            toast("gagal mendapatkan response")
                        }
                    } catch (e: Exception) {
                        info { "dinda ${e.message}" }
                    }
                }

                override fun onFailure(call: Call<SliderResponse>, t: Throwable) {
                    info { "dinda ${t.message}" }
                }

            })
    }

    private fun setImageInSlider(
        images: ArrayList<String>, imageSlider: SliderView,
        link: ArrayList<String>,
        sliderModel: MutableList<SliderModel>
    ) {
        val adapter = ShowSliderAdapter()
        adapter.renewItems(images,link,sliderModel)
        imageSlider.setSliderAdapter(adapter)
        imageSlider.isAutoCycle = true
        imageSlider.startAutoCycle()
    }

    override fun onStart() {
        super.onStart()
        binding.rvproduk.layoutManager = LinearLayoutManager(requireContext().applicationContext)
        binding.rvproduk.setHasFixedSize(true)
        (binding.rvproduk.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        api.getproduct()
            .enqueue(object : Callback<ResponseProduk> {
                override fun onResponse(
                    call: Call<ResponseProduk>,
                    response: Response<ResponseProduk>
                ) {
                    try {
                        if (response.isSuccessful) {
                            val notesList = mutableListOf<ProdukModel>()
                            val data = response.body()
                            for (hasil in data!!.data) {
                                notesList.add(hasil)
                                mAdapter =
                                    ProdukAdapter(notesList)
                                binding.rvproduk.adapter = mAdapter
                                mAdapter.notifyDataSetChanged()
                            }
                            mAdapter.setDialog(object : ProdukAdapter.Dialog {


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
                                    startActivity<DetailProdukActivity>(
                                        "nama" to nama,
                                        "harga" to harga,
                                        "deskripsi" to deskripsi,
                                        "foto" to foto,
                                        "rating" to rating,
                                        "stok" to stok,
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

                override fun onFailure(call: Call<ResponseProduk>, t: Throwable) {
                    info { "dinda ${t.message}" }
                }

            })

    }

    fun gambar(){
        api.getgambar("Bearer ${sessionManager.getToken()}").enqueue(object : Callback<GambarModel>{
            override fun onResponse(call: Call<GambarModel>, response: Response<GambarModel>) {
                try {
                    if (response.isSuccessful){
                        if (response.body()!!.foto.isNotEmpty()){
                            binding.shimmergambar.stopShimmer()
                            binding.shimmergambar.visibility = View.GONE
                            binding.imggambar.visibility = View.VISIBLE
                            Picasso.get().load(response.body()!!.foto).fit().centerCrop().into(binding.imggambar)

                        }else
                        {
                            binding.shimmergambar.stopShimmer()
                            binding.shimmergambar.visibility = View.GONE
                            binding.imggambar.visibility = View.VISIBLE
                        }
                    }
                }catch (e :Exception){
                    info { "dinda ${e.message}" }
                }
            }

            override fun onFailure(call: Call<GambarModel>, t: Throwable) {
                toast("Masalah jaringan")
                info { "dinda ${t.message}" }
            }
        })

    }

}