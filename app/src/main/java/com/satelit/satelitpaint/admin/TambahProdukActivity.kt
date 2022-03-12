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
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.model.ResponseModel
import com.satelit.satelitpaint.session.SessionManager
import com.satelit.satelitpaint.webservice.ApiClient
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_tambah_produk.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnFailureListener

import com.google.android.gms.tasks.OnSuccessListener
import org.jetbrains.anko.*


class TambahProdukActivity : AppCompatActivity(), AnkoLogger {
    private val REQUEST_PICK_IMAGE = 2
    var filePath: Uri? = null
    var datafoto: ByteArray? = null
    var kategori: String? = null
    lateinit var progressDialog: ProgressDialog

    //var
    var mingrosir = 0
    var maxgrosir = 0
    var hargagrosir = 0

    //firebase
    private var storageReference: StorageReference? = null

    //data
    var nama: String? = null
    var harga: Int? = null
    var id: Int? = null
    var stok: Int? = null
    var deskripsi: String? = null
    var foto: String? = null
    var rating: Float? = null

    lateinit var sessionManager: SessionManager
    private var api = ApiClient.instance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_produk)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            nama = bundle.getString("nama")
            harga = bundle.getInt("harga")
            stok = bundle.getInt("stok")
            id = bundle.getInt("id")
            deskripsi = bundle.getString("deskripsi")
            foto = bundle.getString("foto")
            rating = bundle.getFloat("rating")

            textView5.text = "Update Produk"
            edtnama.setText(nama)
            edtdeskripsi.setText(deskripsi)
            edtharga.setText(harga.toString())
            edtstok.setText(stok.toString())
            Picasso.get().load(foto.toString()).fit().centerCrop().into(imggambar)
            btnsimpan.visibility = View.GONE
            btnupdate.visibility = View.VISIBLE
        }
        progressDialog = ProgressDialog(this)
        sessionManager = SessionManager(this)
        btnaddfoto.setOnClickListener {
            pilihfile()
        }

        spinner()
        btnsimpan.setOnClickListener { view ->
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.setTitle(resources.getString(R.string.loading))
            progressDialog.show()

            val nama = edtnama.text.toString()
            val deskripsi = edtdeskripsi.text.toString()
            val harga = edtharga.text.toString()
            val stok = edtstok.text.toString()

            if (nama.isNotEmpty() && deskripsi.isNotEmpty() && harga.isNotEmpty() && stok.isNotEmpty() && datafoto != null && kategori!=null) {
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
                    api.insertproduct(
                        "Bearer ${sessionManager.getToken().toString()}",
                        nama,
                        downloadUrl.toString(),
                        kategori!!,
                        deskripsi,
                        stok.toInt(),
                        0,
                        0,
                        harga.toInt(),
                        0
                    ).enqueue(object : Callback<ResponseModel> {
                        override fun onResponse(
                            call: Call<ResponseModel>,
                            response: Response<ResponseModel>
                        ) {
                            try {
                                if (response.isSuccessful) {
                                    if (response.body()!!.data == 1) {
                                        startActivity<HomeAdminActivity>()
                                        toast("Insert produk berhasil")
                                        finish()
                                    } else {
                                        progressDialog.dismiss()
                                        Snackbar.make(view, "Jaringan Error", 3000).show()
                                    }
                                } else {
                                    progressDialog.dismiss()
                                    Snackbar.make(view, "Response salah hubungi admin", 3000).show()
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
            } else {
                progressDialog.dismiss()
                Snackbar.make(view, "Jangan kosongi kolom", 3000).show()
            }

        }

        btnupdate.setOnClickListener { view->
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.setTitle(resources.getString(R.string.loading))
            progressDialog.show()

            val nama = edtnama.text.toString()
            val deskripsi = edtdeskripsi.text.toString()
            val harga = edtharga.text.toString()
            val stok = edtstok.text.toString()
            if (nama.isNotEmpty() && deskripsi.isNotEmpty() && harga.isNotEmpty() && stok.isNotEmpty()) {

                if (datafoto==null){
                    api.updateproduct(
                        "Bearer ${sessionManager.getToken().toString()}",
                        id!!,
                        nama,
                        foto.toString(),
                        deskripsi,
                        stok.toInt(),
                        0,
                        0,
                        harga.toInt(),
                        0
                    ).enqueue(object : Callback<ResponseModel> {
                        override fun onResponse(
                            call: Call<ResponseModel>,
                            response: Response<ResponseModel>
                        ) {
                            try {
                                if (response.isSuccessful) {
                                    if (response.body()!!.data == 1) {
                                        startActivity<HomeAdminActivity>()
                                        toast("Update produk berhasil")
                                        finish()
                                    } else {
                                        progressDialog.dismiss()
                                        Snackbar.make(view, "Jaringan Error", 3000).show()
                                    }
                                } else {
                                    progressDialog.dismiss()
                                    Snackbar.make(view, "Response salah hubungi admin", 3000).show()
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

                }else{

                    val photoRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(foto.toString())
                    photoRef.delete().addOnSuccessListener { // File deleted successfully
                        val fileref =
                            FirebaseStorage.getInstance().reference.child(
                                System.currentTimeMillis().toString() + ".jpg"
                            )
                        var uploadTaskUpdate: StorageTask<*>
                        uploadTaskUpdate = fileref.putBytes(datafoto!!)
                        uploadTaskUpdate.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                            if (!task.isSuccessful) {
                            }
                            return@Continuation fileref.downloadUrl
                        }).addOnCompleteListener { task ->
                            val downloadUrl = task.result
                            api.updateproduct(
                                "Bearer ${sessionManager.getToken().toString()}",
                                id!!,
                                nama,
                                downloadUrl.toString(),
                                deskripsi,
                                stok.toInt(),
                                0,
                                0,
                                harga.toInt(),
                                0
                            ).enqueue(object : Callback<ResponseModel> {
                                override fun onResponse(
                                    call: Call<ResponseModel>,
                                    response: Response<ResponseModel>
                                ) {
                                    try {
                                        if (response.isSuccessful) {
                                            if (response.body()!!.data == 1) {
                                                startActivity<HomeAdminActivity>()
                                                toast("Update produk berhasil")
                                                finish()
                                            } else {
                                                progressDialog.dismiss()
                                                Snackbar.make(view, "Jaringan Error", 3000).show()
                                            }
                                        } else {
                                            progressDialog.dismiss()
                                            Snackbar.make(view, "Response salah hubungi admin", 3000).show()
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
                    }.addOnFailureListener {
                        progressDialog.dismiss()
                        Snackbar.make(view, "Jaringan bermasalah", 3000).show()

                    }
                }

            } else {
                progressDialog.dismiss()
                Snackbar.make(view, "Jangan kosongi kolom", 3000).show()
            }


        }

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
                Picasso.get().load(filePath).fit().centerCrop().into(imggambar)
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


    fun spinner(){
        val datakelamin = arrayOf("Thinner B Special","Thinner A","Thinner A Special","Spiritus","Rockstar","Produk Lainnya")
        val spinner = find<Spinner>(R.id.spnkategori)
        if (spinner!=null){
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, datakelamin)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    kategori = datakelamin[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }

        }

    }


}