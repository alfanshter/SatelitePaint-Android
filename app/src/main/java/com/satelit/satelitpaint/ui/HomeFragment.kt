package com.satelit.satelitpaint.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.satelit.satelitpaint.DetailProdukActivity
import com.satelit.satelitpaint.KeranjangActivity
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.SearchProdukActivity
import com.satelit.satelitpaint.adapter.ProdukCustomerAdapter
import com.satelit.satelitpaint.adapter.ShowSliderAdapter
import com.satelit.satelitpaint.admin.auth.LoginActivity
import com.satelit.satelitpaint.databinding.FragmentHomeBinding
import com.satelit.satelitpaint.model.*
import com.satelit.satelitpaint.webservice.ApiClient
import com.smarteist.autoimageslider.SliderView
import com.squareup.picasso.Picasso
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment(),AnkoLogger {
    private lateinit var mAdapter: ProdukCustomerAdapter
    lateinit var binding: FragmentHomeBinding
    private var api = ApiClient.instance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.shimmerDessert.startShimmer()
        binding.btccart.setOnClickListener {
            startActivity<KeranjangActivity>()
        }

        binding.btnadmin.setOnClickListener {
            startActivity<LoginActivity>()
        }

        binding.btnsearch.setOnClickListener {
            startActivity<SearchProdukActivity>()
        }

        binding.btnthinnera.setOnClickListener {
            binding.btnthinnera.background.setTint(resources.getColor(R.color.black))
            binding.btnthinnera.textColor = resources.getColor(R.color.white)

            binding.btnthinnerbspecial.background.setTint(resources.getColor(R.color.abuputih))
            binding.btnthinnerbspecial.textColor = resources.getColor(R.color.black)

            binding.btnthinneraspecial.background.setTint(resources.getColor(R.color.abuputih))
            binding.btnthinneraspecial.textColor = resources.getColor(R.color.black)

            binding.btnspiritus.background.setTint(resources.getColor(R.color.abuputih))
            binding.btnspiritus.textColor = resources.getColor(R.color.black)

            binding.btnproduksemua.background.setTint(resources.getColor(R.color.abuputih))
            binding.btnproduksemua.textColor = resources.getColor(R.color.black)

            getprodukoption("Thinner A")
        }

        binding.btnthinnerbspecial.setOnClickListener {
            binding.btnthinnera.background.setTint(resources.getColor(R.color.abuputih))
            binding.btnthinnera.textColor = resources.getColor(R.color.black)

            binding.btnthinnerbspecial.background.setTint(resources.getColor(R.color.black))
            binding.btnthinnerbspecial.textColor = resources.getColor(R.color.white)

            binding.btnthinneraspecial.background.setTint(resources.getColor(R.color.abuputih))
            binding.btnthinneraspecial.textColor = resources.getColor(R.color.black)

            binding.btnspiritus.background.setTint(resources.getColor(R.color.abuputih))
            binding.btnspiritus.textColor = resources.getColor(R.color.black)


            binding.btnproduksemua.background.setTint(resources.getColor(R.color.abuputih))
            binding.btnproduksemua.textColor = resources.getColor(R.color.black)

            getprodukoption("Thinner B Special")
        }

        binding.btnthinneraspecial.setOnClickListener {
            binding.btnthinnera.background.setTint(resources.getColor(R.color.abuputih))
            binding.btnthinnera.textColor = resources.getColor(R.color.black)

            binding.btnthinnerbspecial.background.setTint(resources.getColor(R.color.abuputih))
            binding.btnthinnerbspecial.textColor = resources.getColor(R.color.black)

            binding.btnthinneraspecial.background.setTint(resources.getColor(R.color.black))
            binding.btnthinneraspecial.textColor = resources.getColor(R.color.white)

            binding.btnspiritus.background.setTint(resources.getColor(R.color.abuputih))
            binding.btnspiritus.textColor = resources.getColor(R.color.black)


            binding.btnproduksemua.background.setTint(resources.getColor(R.color.abuputih))
            binding.btnproduksemua.textColor = resources.getColor(R.color.black)

            getprodukoption("Thinner A Special")
        }

        binding.btnspiritus.setOnClickListener {
            binding.btnthinnera.background.setTint(resources.getColor(R.color.abuputih))
            binding.btnthinnera.textColor = resources.getColor(R.color.black)

            binding.btnthinnerbspecial.background.setTint(resources.getColor(R.color.abuputih))
            binding.btnthinnerbspecial.textColor = resources.getColor(R.color.black)

            binding.btnthinneraspecial.background.setTint(resources.getColor(R.color.abuputih))
            binding.btnthinneraspecial.textColor = resources.getColor(R.color.black)

            binding.btnspiritus.background.setTint(resources.getColor(R.color.black))
            binding.btnspiritus.textColor = resources.getColor(R.color.white)

            binding.btnproduksemua.background.setTint(resources.getColor(R.color.abuputih))
            binding.btnproduksemua.textColor = resources.getColor(R.color.black)

            getprodukoption("Spiritus")
        }


        binding.btnproduksemua.setOnClickListener {
            binding.btnthinnera.background.setTint(resources.getColor(R.color.abuputih))
            binding.btnthinnera.textColor = resources.getColor(R.color.black)

            binding.btnthinnerbspecial.background.setTint(resources.getColor(R.color.abuputih))
            binding.btnthinnerbspecial.textColor = resources.getColor(R.color.black)

            binding.btnthinneraspecial.background.setTint(resources.getColor(R.color.abuputih))
            binding.btnthinneraspecial.textColor = resources.getColor(R.color.black)

            binding.btnspiritus.background.setTint(R.color.white)
            binding.btnspiritus.textColor = resources.getColor(R.color.black)


            binding.btnproduksemua.background.setTint(resources.getColor(R.color.black))
            binding.btnproduksemua.textColor = resources.getColor(R.color.white)

            getproduk()
        }


        binding.btnchat.setOnClickListener {

            val number = "6282138161919"
            val url = "https://api.whatsapp.com/send?phone=$number"
            openweb(url)
//            val i = Intent(Intent.ACTION_VIEW)
//            i.setPackage("com.whatsapp")
//            i.data = Uri.parse(url)
//            startActivity(i)
        }
        binding.btnfb.setOnClickListener {
            openweb("https://www.facebook.com/udsatelit.thinner")
        }

        binding.btnig.setOnClickListener {
            openig()
        }

        binding.btnweb.setOnClickListener {
            openweb("https://www.satelitpaint.com/")
        }


        getslider()
        gambar()


        return binding.root
    }

    fun openig(){
        val uri = Uri.parse("http://instagram.com/_u/satelit.paint/?hl=id")
        val likeIng = Intent(Intent.ACTION_VIEW, uri)

        likeIng.setPackage("com.instagram.android")

        try {
            startActivity(likeIng)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/satelit.paint/?hl=id")
                )
            )
        }
    }
    fun openweb(string: String) {
        val openURL = Intent(android.content.Intent.ACTION_VIEW)
        openURL.data = Uri.parse(string)
        startActivity(openURL)
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
        adapter.setDialog(object : ShowSliderAdapter.Dialog{
            override fun onClick(position: Int, note: SliderModel) {
                startActivity<DetailProdukActivity>(
                    "nama" to note.nama,
                    "harga" to note.harga,
                    "deskripsi" to note.deskripsi,
                    "foto" to note.foto_produk,
                    "rating" to note.rating,
                    "stok" to note.stok,
                    "id" to note.id_produk
                )
            }

        })
        adapter.renewItems(images,link,sliderModel)
        imageSlider.setSliderAdapter(adapter)
        imageSlider.isAutoCycle = true
        imageSlider.scrollTimeInSec = 3
        imageSlider.startAutoCycle()
    }

    override fun onStart() {
        super.onStart()
        getproduk()

    }

    fun getprodukoption(kategori :String){
        val notesList = mutableListOf<ProdukModel>()
        notesList.clear()
        binding.rvproduk.adapter!!.notifyDataSetChanged()

        binding.rvproduk.layoutManager = GridLayoutManager(requireContext().applicationContext,2)
        binding.rvproduk.setHasFixedSize(true)
        (binding.rvproduk.layoutManager as GridLayoutManager).orientation =
            GridLayoutManager.VERTICAL

        api.getprodukoption(kategori)
            .enqueue(object : Callback<ResponseProduk> {
                override fun onResponse(
                    call: Call<ResponseProduk>,
                    response: Response<ResponseProduk>
                ) {
                    try {
                        if (response.isSuccessful) {

                            val data = response.body()
                            if (response.body()!!.data.isEmpty()){
                                binding.shimmerDessert.stopShimmer()
                                binding.shimmerDessert.visibility = View.GONE
                                binding.rvproduk.visibility = View.GONE
                                binding.txtnodata.visibility = View.VISIBLE

                            }else{
                                for (hasil in data!!.data) {
                                    binding.shimmerDessert.stopShimmer()
                                    binding.shimmerDessert.visibility = View.GONE
                                    binding.rvproduk.visibility = View.VISIBLE
                                    binding.txtnodata.visibility = View.GONE
                                    notesList.add(hasil)
                                    mAdapter =
                                        ProdukCustomerAdapter(notesList)
                                    info { "dinda ${hasil.nama}" }
                                    binding.rvproduk.adapter = mAdapter
                                    mAdapter.notifyDataSetChanged()
                                }
                                mAdapter.setDialog(object : ProdukCustomerAdapter.Dialog {


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
                            }

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

    fun getproduk(){
        binding.rvproduk.layoutManager = GridLayoutManager(requireContext().applicationContext,2)
        binding.rvproduk.setHasFixedSize(true)
        (binding.rvproduk.layoutManager as GridLayoutManager).orientation =
            GridLayoutManager.VERTICAL

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
                                binding.shimmerDessert.stopShimmer()
                                binding.shimmerDessert.visibility = View.GONE
                                binding.rvproduk.visibility = View.VISIBLE
                                notesList.add(hasil)
                                mAdapter =
                                    ProdukCustomerAdapter(notesList)

                                binding.rvproduk.adapter = mAdapter
                                mAdapter.notifyDataSetChanged()
                            }
                            mAdapter.setDialog(object : ProdukCustomerAdapter.Dialog {


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
        api.getgambarcustomer().enqueue(object : Callback<GambarModel>{
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