package com.satelit.satelitpaint

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
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.satelit.satelitpaint.admin.HomeAdminActivity
import com.satelit.satelitpaint.model.*
import com.satelit.satelitpaint.session.SessionManager
import com.satelit.satelitpaint.webservice.ApiClient
import com.satelit.satelitpaint.webservice.ApiClientNotification
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_metode_pembayaran.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.text.DecimalFormat
import java.text.NumberFormat

class MetodePembayaranActivity : AppCompatActivity(), AnkoLogger {
    var nama: String? = null
    var alamat: String? = null
    var telepon: String? = null
    var hargatotal: Int? = null
    var metodepembayaran: String? = null

    var cicilan: Int? = null
    var jumlahcicilan: Int? = null

    private var api = ApiClient.instance()
    private var apinotif = ApiClientNotification.instance()
    lateinit var progressDialog: ProgressDialog
    lateinit var sessionManager: SessionManager
    lateinit var firebaseDatabase: FirebaseDatabase
    private val REQUEST_PICK_IMAGE = 2
    lateinit var radioButton: RadioButton
    var filePath: Uri? = null
    var datafoto: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metode_pembayaran)
        sessionManager = SessionManager(this)
        val bundle: Bundle? = intent.extras
        hargatotal = bundle!!.getInt("hargatotal")
        alamat = bundle.getString("alamat")
        nama = bundle.getString("nama")
        telepon = bundle.getString("telepon")
        progressDialog = ProgressDialog(this)
        firebaseDatabase =
            FirebaseDatabase.getInstance("https://satelitepaint-b27bb-default-rtdb.asia-southeast1.firebasedatabase.app/")
        info { "dinda $hargatotal" }
        cektf.setOnClickListener {
            tfcek.visibility = View.VISIBLE
            tfuncek.visibility = View.GONE

            cicilanuncek.visibility = View.VISIBLE
            cicilancek.visibility = View.VISIBLE

            codcek.visibility = View.GONE
            coduncek.visibility = View.VISIBLE

            cicilancek.visibility = View.GONE
            cicilanuncek.visibility = View.VISIBLE


            view_camera.visibility = View.VISIBLE

            radiocicilan.visibility = View.GONE


            metodepembayaran = "transfer"
        }
        cekcod.setOnClickListener {
            codcek.visibility = View.VISIBLE
            coduncek.visibility = View.GONE

            cicilanuncek.visibility = View.VISIBLE
            cicilancek.visibility = View.VISIBLE


            tfcek.visibility = View.GONE
            tfuncek.visibility = View.VISIBLE

            cicilancek.visibility = View.GONE
            cicilanuncek.visibility = View.VISIBLE

            view_camera.visibility = View.INVISIBLE

            radiocicilan.visibility = View.GONE


            metodepembayaran = "cod"

        }

        cekcicilan.setOnClickListener {
            codcek.visibility = View.GONE
            coduncek.visibility = View.VISIBLE

            tfcek.visibility = View.GONE
            tfuncek.visibility = View.VISIBLE

            cicilancek.visibility = View.VISIBLE
            cicilanuncek.visibility = View.GONE

            radiocicilan.visibility = View.VISIBLE
            view_camera.visibility = View.INVISIBLE

            metodepembayaran = "cicilan"
        }

        cicilan()

        btnselanjutnya.setOnClickListener { view ->

            if (hargatotal != null && alamat != null && nama != null && telepon != null && metodepembayaran != null) {

                if (metodepembayaran == "transfer" && datafoto != null) {
                    progressDialog.setCanceledOnTouchOutside(false)
                    progressDialog.setTitle(resources.getString(R.string.loading))
                    progressDialog.show()

                    //get token notif
                    firebaseDatabase.reference.child("admin").child("admin")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                try {
                                    val token = snapshot.child("token").value.toString()
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
                                        val nomorpesanan = "${telepon}${kodeorder()}"
                                        api.checkout(
                                            sessionManager.getDevice().toString(),
                                            telepon!!,
                                            nama!!,
                                            alamat!!,
                                            metodepembayaran!!,
                                            hargatotal!!,
                                            0,
                                            nomorpesanan,
                                            downloadUrl.toString()
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

                                                                apinotif.createnotif(
                                                                    "Bearer AAAAsZm2aDg:APA91bEuGmtPDPc-iorxY-aFhNea1gkhATLt6wSDHnthDMW8bbL13jIYaZDHdqSgM7__d2J3pfN2_VK83u2HHTbXf2QQlKiLppzKhETM476zkrOkVvf5lUTtfUjQtzMGyoiMp6c_GPR1",
                                                                    PostNotification(
                                                                        token,
                                                                        "type_a",
                                                                        PostNotif(
                                                                            "Pesanan Baru",
                                                                            "dari : ${nama.toString()}"
                                                                        ),
                                                                        PostNotifData(
                                                                            "Pesanan Baru",
                                                                            "dari : ${nama.toString()}",
                                                                            "Value for key_1",
                                                                            "Value for key_2"
                                                                        )

                                                                    )

                                                                ).enqueue(object :
                                                                    Callback<ResponseNotif> {
                                                                    override fun onResponse(
                                                                        call: Call<ResponseNotif>,
                                                                        response: Response<ResponseNotif>
                                                                    ) {
                                                                        try {
                                                                            if (response.isSuccessful) {
                                                                                if (response.body()!!.success == 1) {
                                                                                    toast("berasil kirim request ke admin")
                                                                                }
                                                                            }
                                                                        } catch (e: Exception) {
                                                                            info { "dinda error ${e.message}" }
                                                                        }
                                                                    }

                                                                    override fun onFailure(
                                                                        call: Call<ResponseNotif>,
                                                                        t: Throwable
                                                                    ) {
                                                                        info { "dinda error ${t.message}" }
                                                                        toast("tidak dapat kirim notif")
                                                                    }

                                                                })
                                                                startActivity<PesananActivity>(
                                                                    "nomorpesanan" to nomorpesanan,
                                                                    "nama" to nama,
                                                                    "telepon" to telepon,
                                                                    "alamat" to alamat,
                                                                    "harga" to hargatotal
                                                                )
                                                                finish()
                                                            } else {
                                                                progressDialog.dismiss()
                                                                Snackbar.make(
                                                                    view,
                                                                    "Kesalahan sistem",
                                                                    3000
                                                                ).show()
                                                            }
                                                        } else {
                                                            progressDialog.dismiss()
                                                            Snackbar.make(
                                                                view,
                                                                "Kesalahan sistem",
                                                                3000
                                                            ).show()

                                                        }
                                                    } catch (e: Exception) {
                                                        progressDialog.dismiss()
                                                        info { "dinda ${e.message}" }
                                                        Snackbar.make(
                                                            view,
                                                            "Ada kesalahan aplikasi",
                                                            3000
                                                        ).show()

                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<ResponseModel>,
                                                    t: Throwable
                                                ) {
                                                    progressDialog.dismiss()
                                                    info { "dinda ${t.message}" }
                                                    Snackbar.make(view, "Kesalahan Jaringan", 3000)
                                                        .show()

                                                }

                                            })
                                    }

                                } catch (e: Exception) {
                                    info { "dinda token ${e.message}" }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                info { "dinda error ${error.message}" }
                            }

                        })

                }
                else if (metodepembayaran == "cod") {
                    progressDialog.setCanceledOnTouchOutside(false)
                    progressDialog.setTitle(resources.getString(R.string.loading))
                    progressDialog.show()
                    //get token notif
                    firebaseDatabase.reference.child("admin").child("admin")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                try {
                                    val token = snapshot.child("token").value.toString()
                                    val nomorpesanan = "${telepon}${kodeorder()}"
                                    api.checkout(
                                        sessionManager.getDevice().toString(),
                                        telepon!!,
                                        nama!!,
                                        alamat!!,
                                        metodepembayaran!!,
                                        hargatotal!!,
                                        0,
                                        nomorpesanan,
                                        ""
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
                                                            //create notif
                                                            apinotif.createnotif(
                                                                "Bearer AAAAsZm2aDg:APA91bEuGmtPDPc-iorxY-aFhNea1gkhATLt6wSDHnthDMW8bbL13jIYaZDHdqSgM7__d2J3pfN2_VK83u2HHTbXf2QQlKiLppzKhETM476zkrOkVvf5lUTtfUjQtzMGyoiMp6c_GPR1",
                                                                PostNotification(
                                                                    token,
                                                                    "type_a",
                                                                    PostNotif(
                                                                        "Pesanan Baru",
                                                                        "dari : ${nama.toString()}"
                                                                    ),
                                                                    PostNotifData(
                                                                        "Pesanan Baru",
                                                                        "dari : ${nama.toString()}",
                                                                        "Value for key_1",
                                                                        "Value for key_2"
                                                                    )

                                                                )

                                                            ).enqueue(object :
                                                                Callback<ResponseNotif> {
                                                                override fun onResponse(
                                                                    call: Call<ResponseNotif>,
                                                                    response: Response<ResponseNotif>
                                                                ) {
                                                                    try {
                                                                        if (response.isSuccessful) {
                                                                            if (response.body()!!.success == 1) {
                                                                                toast("berasil kirim request ke admin")
                                                                                startActivity<PesananActivity>(
                                                                                    "nomorpesanan" to nomorpesanan,
                                                                                    "nama" to nama,
                                                                                    "telepon" to telepon,
                                                                                    "alamat" to alamat,
                                                                                    "harga" to hargatotal
                                                                                )
                                                                                finish()
                                                                            } else {
                                                                                info { "dinda gagal kirim notif" }
                                                                                toast("gagal")
                                                                            }
                                                                        } else {
                                                                            info { "Response gagal" }
                                                                            toast("response gagal")
                                                                        }
                                                                    } catch (e: Exception) {
                                                                        info { "dinda error ${e.message}" }
                                                                    }
                                                                }

                                                                override fun onFailure(
                                                                    call: Call<ResponseNotif>,
                                                                    t: Throwable
                                                                ) {
                                                                    info { "dinda error ${t.message}" }
                                                                    toast("tidak dapat kirim notif")
                                                                }

                                                            })
                                                            //end create notif

                                                        } else {
                                                            progressDialog.dismiss()
                                                            Snackbar.make(
                                                                view,
                                                                "Kesalahan sistem",
                                                                3000
                                                            ).show()
                                                        }
                                                    } else {
                                                        progressDialog.dismiss()
                                                        Snackbar.make(
                                                            view,
                                                            "Kesalahan sistem",
                                                            3000
                                                        ).show()

                                                    }
                                                } catch (e: Exception) {
                                                    progressDialog.dismiss()
                                                    info { "dinda ${e.message}" }
                                                    Snackbar.make(
                                                        view,
                                                        "Ada kesalahan aplikasi",
                                                        3000
                                                    ).show()

                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<ResponseModel>,
                                                t: Throwable
                                            ) {
                                                progressDialog.dismiss()
                                                info { "dinda ${t.message}" }
                                                Snackbar.make(view, "Kesalahan Jaringan", 3000)
                                                    .show()

                                            }

                                        })
                                } catch (e: Exception) {
                                    info { "dinda token ${e.message}" }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                info { "dinda error ${error.message}" }
                            }

                        })

                }
                else if (metodepembayaran == "cicilan") {
                    if (cicilan!=null && jumlahcicilan!=null){
                        progressDialog.setCanceledOnTouchOutside(false)
                        progressDialog.setTitle(resources.getString(R.string.loading))
                        progressDialog.show()
                        //get token notif
                        firebaseDatabase.reference.child("admin").child("admin")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    try {
                                        val token = snapshot.child("token").value.toString()
                                        val nomorpesanan = "${telepon}${kodeorder()}"
                                        api.cicilan(
                                            sessionManager.getDevice().toString(),
                                            telepon!!,
                                            nama!!,
                                            alamat!!,
                                            metodepembayaran!!,
                                            hargatotal!!,
                                            0,
                                            nomorpesanan,
                                            "",
                                            cicilan!!,
                                            jumlahcicilan!!
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
                                                                //create notif
                                                                apinotif.createnotif(
                                                                    "Bearer AAAAsZm2aDg:APA91bEuGmtPDPc-iorxY-aFhNea1gkhATLt6wSDHnthDMW8bbL13jIYaZDHdqSgM7__d2J3pfN2_VK83u2HHTbXf2QQlKiLppzKhETM476zkrOkVvf5lUTtfUjQtzMGyoiMp6c_GPR1",
                                                                    PostNotification(
                                                                        token,
                                                                        "type_a",
                                                                        PostNotif(
                                                                            "Pesanan Baru",
                                                                            "dari : ${nama.toString()}"
                                                                        ),
                                                                        PostNotifData(
                                                                            "Pesanan Baru",
                                                                            "dari : ${nama.toString()}",
                                                                            "Value for key_1",
                                                                            "Value for key_2"
                                                                        )

                                                                    )

                                                                ).enqueue(object :
                                                                    Callback<ResponseNotif> {
                                                                    override fun onResponse(
                                                                        call: Call<ResponseNotif>,
                                                                        response: Response<ResponseNotif>
                                                                    ) {
                                                                        try {
                                                                            if (response.isSuccessful) {
                                                                                if (response.body()!!.success == 1) {
                                                                                    toast("berasil kirim request ke admin")
                                                                                    startActivity<PesananActivity>(
                                                                                        "nomorpesanan" to nomorpesanan,
                                                                                        "nama" to nama,
                                                                                        "telepon" to telepon,
                                                                                        "alamat" to alamat,
                                                                                        "harga" to hargatotal
                                                                                    )
                                                                                    finish()
                                                                                } else {
                                                                                    info { "dinda gagal kirim notif" }
                                                                                    toast("gagal")
                                                                                }
                                                                            } else {
                                                                                info { "Response gagal" }
                                                                                toast("response gagal")
                                                                            }
                                                                        } catch (e: Exception) {
                                                                            info { "dinda error ${e.message}" }
                                                                        }
                                                                    }

                                                                    override fun onFailure(
                                                                        call: Call<ResponseNotif>,
                                                                        t: Throwable
                                                                    ) {
                                                                        info { "dinda error ${t.message}" }
                                                                        toast("tidak dapat kirim notif")
                                                                    }

                                                                })
                                                                //end create notif

                                                            } else {
                                                                progressDialog.dismiss()
                                                                Snackbar.make(
                                                                    view,
                                                                    "Kesalahan sistem",
                                                                    3000
                                                                ).show()
                                                            }
                                                        } else {
                                                            progressDialog.dismiss()
                                                            Snackbar.make(
                                                                view,
                                                                "Kesalahan sistem",
                                                                3000
                                                            ).show()

                                                        }
                                                    } catch (e: Exception) {
                                                        progressDialog.dismiss()
                                                        info { "dinda ${e.message}" }
                                                        Snackbar.make(
                                                            view,
                                                            "Ada kesalahan aplikasi",
                                                            3000
                                                        ).show()

                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<ResponseModel>,
                                                    t: Throwable
                                                ) {
                                                    progressDialog.dismiss()
                                                    info { "dinda ${t.message}" }
                                                    Snackbar.make(view, "Kesalahan Jaringan", 3000)
                                                        .show()

                                                }

                                            })
                                    } catch (e: Exception) {
                                        info { "dinda token ${e.message}" }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    info { "dinda error ${error.message}" }
                                }

                            })

                    }else{
                        Snackbar.make(view, "Pilih  cicilan", 3000).show()
                    }

                }
                else {
                    Snackbar.make(view, "Jangan kosongi kolom", 3000).show()
                }

            } else {
                Snackbar.make(view, "Jangan kosongi kolom", 3000).show()
            }
        }

        btnuploadfoto.setOnClickListener {
            pilihfile()
        }

    }

    fun kodeorder(): String {
        val charPool: List<Char> = ('A'..'Z') + ('0'..'9')
        val outputStrLength = (5)
        return (1..outputStrLength)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
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

    fun cicilan() {
        val formatter: NumberFormat = DecimalFormat("#,###")
        val cicilan1 = hargatotal
        val cicilan2 = hargatotal?.div(2)
        val cicilan3 = hargatotal?.div(3)
        val cicilan4 = hargatotal?.div(4)

        val hargacicilan1: String = formatter.format(cicilan1)
        val hargacicilan2: String = formatter.format(cicilan2)
        val hargacicilan3: String = formatter.format(cicilan3)
        val hargacicilan4: String = formatter.format(cicilan4)

        radiocicilan1x.text = "Cicilan 1X : Rp. ${hargacicilan1}"
        radiocicilan2x.text = "Cicilan 2X : Rp. ${hargacicilan2}"
        radiocicilan3x.text = "Cicilan 3X : Rp. ${hargacicilan3}"
        radiocicilan4x.text = "Cicilan 4X : Rp. ${hargacicilan4}"

        radiocicilan.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
                if (radio.text == "Cicilan 1X : Rp. ${hargacicilan1}") {
                    cicilan = hargatotal
                    jumlahcicilan = 1
                }
                else if (radio.text == "Cicilan 2X : Rp. ${hargacicilan2}") {
                    cicilan = hargatotal?.div(2)
                    jumlahcicilan = 2
                }
                else if (radio.text == "Cicilan 3X : Rp. ${hargacicilan3}") {
                    cicilan = hargatotal?.div(3)
                    jumlahcicilan = 3
                }
                else if (radio.text == "Cicilan 4X : Rp. ${hargacicilan4}") {
                    cicilan = hargatotal?.div(4)
                    jumlahcicilan = 4
                }
            })

    }


}