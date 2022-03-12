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
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.model.GambarModel
import com.satelit.satelitpaint.model.ResponseModel
import com.satelit.satelitpaint.session.SessionManager
import com.satelit.satelitpaint.webservice.ApiClient
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_gambar.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class EditGambarActivity : AppCompatActivity(),AnkoLogger {
    var datafoto: ByteArray? = null
    private val REQUEST_PICK_IMAGE = 2
    var filePath: Uri? = null
    val api = ApiClient.instance()
    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_gambar)
        progressDialog = ProgressDialog(this)
        sessionManager = SessionManager(this)
        btntambah.setOnClickListener {
            pilihfile()
        }

        btnupload.setOnClickListener {view->
            if (datafoto != null) {
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
                    api.insertgambar("Bearer ${sessionManager.getToken()}", downloadUrl.toString())
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
                                            gambar()
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

            } else {
                Snackbar.make(view, "Upload foto terlebih dahulu", 3000).show()
            }
        }
        btndelete.setOnClickListener {
            deletegambar(it)
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

    fun gambar(){
        api.getgambar("Bearer ${sessionManager.getToken()}").enqueue(object : Callback<GambarModel>{
            override fun onResponse(call: Call<GambarModel>, response: Response<GambarModel>) {
                try {
                    if (response.isSuccessful){
                        shimmergambar.stopShimmer()
                        shimmergambar.visibility = View.GONE
                        imgfoto.visibility = View.VISIBLE
                        Picasso.get().load(response.body()!!.foto).fit().centerCrop().into(imgfoto)
                    }
                }catch (e :Exception){
                    info { "dinda gambar ${e.message}" }
                }
            }

            override fun onFailure(call: Call<GambarModel>, t: Throwable) {
                toast("Masalah jaringan")
                info { "dinda ${t.message}" }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        gambar()

    }
    fun deletegambar(view:View){

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Gambar")
        builder.setMessage("Hapus gambar  ini ? ")
        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.setTitle(resources.getString(R.string.loading))
            progressDialog.show()

            api.deletegambar("Bearer ${sessionManager.getToken()}", "0").enqueue(object : Callback<ResponseModel>{
                override fun onResponse(
                    call: Call<ResponseModel>,
                    response: Response<ResponseModel>
                ) {
                    try {
                        if (response.isSuccessful) {
                            if (response.body()!!.data == 1) {
                                progressDialog.dismiss()
                                onStart()
                            } else {
                                progressDialog.dismiss()
                                Snackbar.make(view, "Response salah hubungi admin", 3000).show()
                            }
                        } else {
                            progressDialog.dismiss()
                            Snackbar.make(view, "Response salah hubungi admin", 3000).show()
                        }
                    } catch (e: Exception) {
                        progressDialog.dismiss()
                        info { "dinda ${e.message}" }

                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    progressDialog.dismiss()
                    info { "dinda ${t.message}" }
                    Snackbar.make(view,"Ada kesalahan jaringan",3000).show()

                }

            })

        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            toast("tidak")
        }

        builder.show()


    }
}