package com.satelit.satelitpaint.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.satelit.satelitpaint.DetailProdukActivity
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.adapter.ProdukAdapter
import com.satelit.satelitpaint.model.ProdukModel
import com.satelit.satelitpaint.model.ResponseProduk
import com.satelit.satelitpaint.webservice.ApiClient
import kotlinx.android.synthetic.main.activity_search_produk_admin.*
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchProdukAdminActivity : AppCompatActivity() {
    val api = ApiClient.instance()
    var mAdapter : ProdukAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_produk_admin)

        showsearch()
        rvsearch1.layoutManager = LinearLayoutManager(this)
        rvsearch2.layoutManager = LinearLayoutManager(this)
    }

    fun showsearch(){
        api.getproduct().enqueue(object : Callback<ResponseProduk> {
            override fun onResponse(
                call: Call<ResponseProduk>,
                response: Response<ResponseProduk>
            ) {
                val datalist = response.body()

                mAdapter = datalist?.let { ProdukAdapter(it.data) }
                rvsearch1.adapter = mAdapter

                search.imeOptions = EditorInfo.IME_ACTION_SEARCH
                search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(action: String?): Boolean =false

                    override fun onQueryTextChange(action: String?): Boolean {
                        if (action!=null){
                            if (action.isEmpty()){
                                rvsearch1.visibility = View.VISIBLE
                                rvsearch2.visibility = View.GONE
                            }else if (action.length >1){
                                val filter = datalist?.data?.filter { it.nama!!.contains("$action",true) }
                                mAdapter = ProdukAdapter(filter as List<ProdukModel>)
                                mAdapter!!.setDialog(object : ProdukAdapter.Dialog{
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
                                if (action.isNotEmpty()){
                                    rvsearch2.visibility = View.VISIBLE
                                    rvsearch2.adapter = mAdapter
                                    rvsearch1.visibility = View.INVISIBLE
                                }else{
                                    rvsearch1.visibility = View.VISIBLE
                                    rvsearch2.visibility = View.GONE
                                }
                            }
                        }
                        return false
                    }

                })

            }

            override fun onFailure(call: Call<ResponseProduk>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

}