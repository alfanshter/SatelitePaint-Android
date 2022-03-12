package com.satelit.satelitpaint.admin

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.satelit.satelitpaint.DetailProdukActivity
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.adapter.ProdukAdapter
import com.satelit.satelitpaint.adapter.SliderAdminAdapter
import com.satelit.satelitpaint.model.*
import com.satelit.satelitpaint.webservice.ApiClient
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_slider_admin.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class SliderAdminActivity : AppCompatActivity(), AnkoLogger {
    var datafoto: ByteArray? = null
    private val REQUEST_PICK_IMAGE = 2
    var filePath: Uri? = null
    val api = ApiClient.instance()

    var nama : String? = null
    var idnama : Int? = null
    var harga : Int? = null
    var deskripsi : String? = null
    var foto : String? = null
    var stok : Int? = null
    var id_produk : Int? = null

    lateinit var progressDialog: ProgressDialog
    private lateinit var mAdapter: SliderAdminAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slider_admin)
        progressDialog = ProgressDialog(this)

        shimmerslider.startShimmer()

        btnfoto.setOnClickListener {
            pilihfile()
        }

        btnupload.setOnClickListener { view ->
            if ( nama!=null && harga!=null && deskripsi!=null && foto!=null
                && stok!=null && id_produk!=null && datafoto != null  ) {
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.setTitle(resources.getString(R.string.loading))
                progressDialog.show()
                val fileref =
                    FirebaseStorage.getInstance().reference.child(
                        System.currentTimeMillis().toString() + ".jpg"
                    )
                var uploadTask: StorageTask<*>
                uploadTask = fileref.putBytes(datafoto!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                    }
                    return@Continuation fileref.downloadUrl
                }).addOnCompleteListener { task ->
                    val downloadUrl = task.result
                    api.insertslider(
                        nama!!,harga!!,deskripsi!!,downloadUrl.toString(),
                        foto!!,stok!!,id_produk!!
                    )
                        .enqueue(object : Callback<ResponseModel> {
                            override fun onResponse(
                                call: Call<ResponseModel>,
                                response: Response<ResponseModel>
                            ) {
                                try {
                                    if (response.isSuccessful) {
                                        if (response.body()!!.data == 1) {
                                            progressDialog.dismiss()
                                            toast("upload berhasil")
                                            onStart()
                                        } else {
                                            progressDialog.dismiss()
                                            Snackbar.make(view, "Jaringan Error", 3000).show()
                                        }
                                    } else {
                                        progressDialog.dismiss()
                                        Snackbar.make(view, "Response salah hubungi admin", 3000)
                                            .show()
                                    }
                                } catch (e: Exception) {
                                    progressDialog.dismiss()
                                    info { "dinda ${e.message}" }
                                    Snackbar.make(view, "Ada kesalahan aplikasi", 3000).show()

                                }
                            }

                            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                                progressDialog.dismiss()
                                info { "dinda ${t.message}" }
                                Snackbar.make(view, "Ada kesalahan Jaringan", 3000).show()

                            }

                        })

                }

            }
            else {
                Snackbar.make(view, "Upload foto terlebih dahulu dan pilih produk", 3000).show()
            }
        }
        spinproduk()
    }

    private fun pilihfile() {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    dialog_permis(
                        "External storage", Manifest.permission.READ_EXTERNAL_STORAGE

                    )
                } else {
                    ActivityCompat
                        .requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            REQUEST_PICK_IMAGE
                        )
                }

            }
        }
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_PICK_IMAGE)

    }

    fun dialog_permis(
        msg: String,
        permission: String
    ) {
        val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertBuilder.setCancelable(true)
        alertBuilder.setTitle("Permission necessary")
        alertBuilder.setMessage("$msg permission is necessary")
        alertBuilder.setPositiveButton(android.R.string.yes,
            DialogInterface.OnClickListener { dialog, which ->
                ActivityCompat.requestPermissions(
                    (this as Activity?)!!, arrayOf(permission),
                    REQUEST_PICK_IMAGE
                )
            })
        val alert: AlertDialog = alertBuilder.create()
        alert.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK_IMAGE) {
                filePath = data?.data
                Picasso.get().load(filePath).fit().centerCrop().into(imgfoto)
                convert()
            }
        }

    }

    fun convert() {
        val bmp = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos)
        datafoto = baos.toByteArray()

    }


    override fun onStart() {
        super.onStart()

        rvslider.layoutManager = LinearLayoutManager(this)
        rvslider.setHasFixedSize(true)
        (rvslider.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL

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

                                shimmerslider.stopShimmer()
                                shimmerslider.visibility = View.GONE
                                rvslider.visibility = View.VISIBLE
                                txtnodata.visibility = View.GONE
                                notesList.add(hasil)
                                mAdapter =
                                    SliderAdminAdapter(notesList)
                                rvslider.adapter = mAdapter
                                mAdapter.notifyDataSetChanged()
                            }
                            if (data.data.isEmpty()){
                                shimmerslider.stopShimmer()
                                shimmerslider.visibility = View.GONE
                                txtnodata.visibility = View.VISIBLE
                            }
                            mAdapter.setDialog(object : SliderAdminAdapter.Dialog {
                                override fun onClick(position: Int, id: Int,foto : String) {
                                    val builder = AlertDialog.Builder(this@SliderAdminActivity)
                                    builder.setTitle("Data Slider")
                                    builder.setMessage("Hapus Slider ? ")
                                    builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                                        progressDialog.setCanceledOnTouchOutside(false)
                                        progressDialog.setTitle(resources.getString(R.string.loading))
                                        progressDialog.show()

                                        api.deleteslider(id).enqueue(object : Callback<ResponseModel>{
                                            override fun onResponse(
                                                call: Call<ResponseModel>,
                                                response: Response<ResponseModel>
                                            ) {
                                                try {
                                                    if (response.isSuccessful) {
                                                        if (response.body()!!.data == 1) {
                                                            val photoRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(foto.toString())
                                                            photoRef.delete().addOnCompleteListener {
                                                                if (it.isSuccessful){
                                                                    progressDialog.dismiss()
                                                                    toast("berhasil hapus")
                                                                    onStart()
                                                                }else{
                                                                    progressDialog.dismiss()
                                                                    toast("berhasil hapus")
                                                                    onStart()
                                                                }
                                                            }
                                                        } else {
                                                            progressDialog.dismiss()
                                                            toast("Aplikasi Error")
                                                        }
                                                    } else {
                                                        progressDialog.dismiss()
                                                        toast("Response error harap hubungi admin")
                                                    }
                                                } catch (e: Exception) {
                                                    progressDialog.dismiss()
                                                    info { "dinda ${e.message}" }
                                                    toast("Ada kesalahan jaringan")

                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<ResponseModel>,
                                                t: Throwable
                                            ) {
                                                progressDialog.dismiss()
                                                info { "dinda ${t.message}" }
                                                toast("Ada kesalahan jaringan")
                                            }

                                        })
                                    }

                                    builder.setNegativeButton(android.R.string.no) { dialog, which ->
                                        toast("tidak")
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

                override fun onFailure(call: Call<SliderResponse>, t: Throwable) {
                    info { "dinda ${t.message}" }
                }

            })

    }

    fun spinproduk(){
        api.getproductslider()
            .enqueue(object : Callback<ProdukSliderResponse> {
                override fun onResponse(
                    call: Call<ProdukSliderResponse>,
                    response: Response<ProdukSliderResponse>
                ) {
                    try {
                        if (response.isSuccessful) {
//                            val notesList = mutableListOf<ProdukModel>()
                            val data = ProdukSliderResponse.ProdukModel()
                            data.id = -1
                            data.nama = "Pilih Produk"

                            var notesList: MutableList<ProdukSliderResponse.ProdukModel> =
                                response.body()!!.data as MutableList<ProdukSliderResponse.ProdukModel>

                            notesList.add(0,data)

                            val adapter: ArrayAdapter<ProdukSliderResponse.ProdukModel> =
                                ArrayAdapter<ProdukSliderResponse.ProdukModel>(
                                    this@SliderAdminActivity,
                                    android.R.layout.simple_spinner_item,
                                    notesList
                                )

                            spnproduk.adapter = adapter
                            spnproduk.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                    }

                                    override fun onItemSelected(
                                        parent: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        if (position == 0) {
                                            idnama = null
                                            nama = null
                                            harga = null
                                            deskripsi = null
                                            foto = null
                                            stok = null
                                            id_produk = null
                                        }

                                        if (position > 0) {
                                                idnama = notesList[position].id
                                                nama = notesList[position].nama
                                                harga = notesList[position].harga
                                                deskripsi = notesList[position].deskripsi
                                                foto = notesList[position].foto
                                                stok = notesList[position].stok
                                                id_produk = notesList[position].id
                                        }
                                    }

                                }



                        } else {
                            toast("gagal mendapatkan response")
                        }
                    } catch (e: Exception) {
                        info { "dinda ${e.message}" }
                    }
                }

                override fun onFailure(call: Call<ProdukSliderResponse>, t: Throwable) {
                    info { "dinda ${t.message}" }
                }

            })

    }

}