package com.satelit.satelitpaint

import android.Manifest
import android.app.ProgressDialog
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.common.collect.Table
import com.itextpdf.text.*
import com.itextpdf.text.pdf.GrayColor
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.satelit.satelitpaint.adapter.DetailPesananAdapter
import com.satelit.satelitpaint.model.*
import com.satelit.satelitpaint.session.SessionManager
import com.satelit.satelitpaint.webservice.ApiClient
import kotlinx.android.synthetic.main.activity_pesanan.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import com.itextpdf.text.Chunk

import com.itextpdf.text.Paragraph
import java.io.IOException
import java.net.URL
import com.itextpdf.text.Phrase
import android.graphics.Bitmap

import android.graphics.BitmapFactory
import java.text.DecimalFormat
import java.text.NumberFormat


class PesananActivity : AppCompatActivity(),AnkoLogger {
    private val api = ApiClient.instance()
    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    var nomorpesanan : String? = null
    var nama : String? = null
    var telepon : String? = null
    var alamat : String? = null
    var totalharga : Int? = null
    var jumlahhalaman : Int? = null
    var listdataprintall = ArrayList<CartModel>()

    var informationArray = arrayOf("Name", "Perusahaan", "Alamat")

    private lateinit var mAdapter: DetailPesananAdapter

    //gambar
    var bmp: Bitmap? = null
    var scaledBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesanan)
        val bundle: Bundle? = intent.extras
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), PackageManager.PERMISSION_GRANTED
        )

        nomorpesanan = bundle!!.getString("nomorpesanan")
        nama = bundle.getString("nama")
        telepon = bundle.getString("telepon")
        alamat = bundle.getString("alamat")
        totalharga = bundle.getInt("harga")

        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)

        val sdf = SimpleDateFormat("dd MMMM yyyy hh:mm")
        val currentDate = sdf.format(Date())

        txtnama.text = nama.toString()
        txttelepon.text = telepon.toString()
        txtalamat.text = alamat.toString()

        val formatter: NumberFormat = DecimalFormat("#,###")
        val myNumber = totalharga
        val harga: String = formatter.format(myNumber)

        txtharga.text = "Rp. ${harga.toString()}"
        txttotalharga.text = "Rp. ${harga.toString()}"
        txtkalender.text = currentDate.toString()

        btncheckout.setOnClickListener {
            savepdfall()
        }
    }


    override fun onStart() {
        super.onStart()
        rvpesanan.layoutManager = LinearLayoutManager(this)
        rvpesanan.setHasFixedSize(true)
        (rvpesanan.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        
        api.getprodukcheckout(sessionManager.getDevice().toString(),nomorpesanan.toString())
            .enqueue(object : Callback<CartResponse> {
                override fun onResponse(
                    call: Call<CartResponse>,
                    response: Response<CartResponse>
                ) {
                    try {
                        if (response.isSuccessful) {
                            val notesList = mutableListOf<CartModel>()
                            val data = response.body()
                            for (hasil in data!!.data) {
                                notesList.add(hasil)
                                mAdapter =
                                    DetailPesananAdapter(notesList)
                                rvpesanan.adapter = mAdapter
                                mAdapter.notifyDataSetChanged()
                            }

                        } else {
                            toast("gagal mendapatkan response")
                        }
                    } catch (e: Exception) {
                        info { "dinda ${e.message}" }
                    }
                }

                override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                    info { "dinda ${t.message}" }
                }

            })

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun savepdfall() {
        val mdoc = Document()
        val mfilename =
            SimpleDateFormat("yyyy_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        val cw = ContextWrapper(this)
        val FileDirectory = cw.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val mFilePath = File(FileDirectory, "${System.currentTimeMillis()}.pdf")

//        val mfilepath = Environment.getExternalStorageDirectory().toString() + "/" + mfilename + ".pdf"

        api.getprodukcheckout(sessionManager.getDevice().toString(),nomorpesanan.toString())
            .enqueue(object : Callback<CartResponse> {
                override fun onResponse(
                    call: Call<CartResponse>,
                    response: Response<CartResponse>
                ) {
                    try {
                        if (response.isSuccessful) {
                            val notesList = mutableListOf<CartModel>()
                            val data = response.body()
                            listdataprintall.clear()
                            for (hasil in data!!.data) {
                                notesList.add(hasil)
                                listdataprintall.add(hasil)
                            }

                            try {
                                PdfWriter.getInstance(mdoc, FileOutputStream(mFilePath))
                                mdoc.open()

                                val FontNormal = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL, BaseColor.BLACK)
                                val FontBold = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.BOLD, BaseColor.BLACK)
                                val FontBigBold = Font(Font.FontFamily.TIMES_ROMAN, 20f, Font.BOLD, BaseColor.BLACK)

                                val columnWidths = floatArrayOf(0.5f, 1f, 2f, 0.5f,1.5f,1.5f)
                                val table = PdfPTable(columnWidths)
                                var cell: PdfPCell

                                table.widthPercentage = 100f
                                table.defaultCell.isUseAscender = true
                                table.defaultCell.isUseDescender = true
                                val f = Font(Font.FontFamily.HELVETICA, 13f, Font.NORMAL, GrayColor.GRAYWHITE)

                                //HEADER
                                val bitmap = BitmapFactory.decodeResource(resources, R.drawable.logopdf)
                                val stream = ByteArrayOutputStream()
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                                val bitMapData = stream.toByteArray()
                                val img1: Image = Image.getInstance(bitMapData)
                                val logo = Image.getInstance(img1)
                                logo.scaleToFit(85f,75f)
                                cell = PdfPCell(logo)
                                cell.rowspan = 3
                                cell.colspan = 2
                                cell.border = Rectangle.NO_BORDER
                                table.addCell(cell)

                                cell = PdfPCell(Phrase("UD.Satelite"))
                                cell.colspan =5
                                cell.border = Rectangle.NO_BORDER
                                table.addCell(cell)

                                cell = PdfPCell(Phrase("Jl. Petikan No. 11a, Mulung Driyorejo Gresik, Driyorejo-6177"))
                                cell.colspan = 5
                                cell.border = Rectangle.NO_BORDER
                                table.addCell(cell)

                                cell = PdfPCell(Phrase("0821-3816-1919"))
                                cell.colspan = 5
                                cell.border = Rectangle.NO_BORDER
                                table.addCell(cell)

                                cell = PdfPCell(Phrase(" "))
                                cell.colspan = 6
                                cell.rowspan =3
                                cell.border = Rectangle.NO_BORDER
                                table.addCell(cell)
                                //End HEAder


                                //Baris kedua
                                cell = PdfPCell(Phrase("Nama Pemesan : ${nama}"))
                                cell.colspan = 6
                                cell.border = Rectangle.NO_BORDER
                                table.addCell(cell)

                                cell = PdfPCell(Phrase("Nama Toko : Satelite Paint"))
                                cell.colspan = 6
                                cell.border = Rectangle.NO_BORDER
                                table.addCell(cell)

                                cell = PdfPCell(Phrase("No Hp : ${telepon}"))
                                cell.colspan = 6
                                cell.border = Rectangle.NO_BORDER
                                table.addCell(cell)

                                cell = PdfPCell(Phrase("Alamat : ${alamat}"))
                                cell.colspan = 6
                                cell.rowspan = 2
                                cell.border = Rectangle.NO_BORDER
                                table.addCell(cell)

                                cell = PdfPCell(Phrase(" "))
                                cell.colspan = 4
                                cell.border = Rectangle.NO_BORDER
                                table.addCell(cell)

                                cell = PdfPCell(Phrase("Total"))
                                cell.colspan = 2
                                cell.border = Rectangle.NO_BORDER
                                table.addCell(cell)

                                cell = PdfPCell(Phrase("Estimate"))
                                cell.colspan = 4
                                cell.border = Rectangle.NO_BORDER
                                table.addCell(cell)

                                val formatter: NumberFormat = DecimalFormat("#,###")
                                val myNumber = totalharga
                                val convertharga: String = formatter.format(myNumber)

                                cell = PdfPCell(Phrase("Rp. ${convertharga}"))
                                cell.colspan = 2
                                cell.border = Rectangle.NO_BORDER
                                table.addCell(cell)

                                cell = PdfPCell(Phrase("Order Id ${nomorpesanan}"))
                                cell.colspan = 4
                                cell.border = Rectangle.NO_BORDER
                                table.addCell(cell)

                                cell = PdfPCell(Phrase("Produk"))
                                cell.colspan = 2
                                cell.border = Rectangle.NO_BORDER
                                table.addCell(cell)

                                cell = PdfPCell(Phrase(" "))
                                cell.colspan = 6
                                cell.rowspan =3
                                cell.border = Rectangle.NO_BORDER
                                table.addCell(cell)

                                table.addCell("No")
                                table.addCell("Product")
                                table.addCell("Item")
                                table.addCell("Qty")
                                table.addCell("Price")
                                table.addCell("Total")

                                for (i in listdataprintall.indices) {
                                    val tanggal = listdataprintall[i].nama
                                    val nomor = i + 1
                                    table.addCell(nomor.toString())
                                    table.addCell("${listdataprintall.get(i).nama .toString()}")
                                    table.addCell("${listdataprintall.get(i).deskripsi .toString()}")
                                    table.addCell("${listdataprintall.get(i).jumlah .toString()}")
                                    table.addCell("Rp. ${formatnumber(listdataprintall.get(i).harga!!)}")
                                    table.addCell("Rp. ${formatnumber(listdataprintall.get(i).totalharga!!)}")

                                }
                                mdoc.add(table)
                                mdoc.close()
                                if (mFilePath.exists()) {

                                    val uri = FileProvider.getUriForFile(
                                        this@PesananActivity,
                                        "com.satelit.satelitpaint.fileprovider",
                                        mFilePath
                                    )
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setDataAndType(uri, "application/pdf")
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    startActivity(intent)
                                } else toast("File path is incorrec")
                                toast("$mfilename.pdf\n" + " is create to \n" + "$mFilePath")
                            } catch (e: Exception) {
                                toast("" + e.toString())
                                info { "dinda ${e.message}" }

                            }

                        } else {
                            toast("gagal mendapatkan response")
                        }
                    } catch (e: Exception) {
                        info { "dinda ${e.message}" }
                    }
                }

                override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                    info { "dinda ${t.message}" }
                }

            })

    }


    fun formatnumber(number : Int): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        val formattedNumber: String = formatter.format(number)

        return formattedNumber
    }

}