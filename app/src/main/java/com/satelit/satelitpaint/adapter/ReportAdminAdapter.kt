package com.satelit.satelitpaint.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.model.TransaksiModel
import com.squareup.picasso.Picasso

class ReportAdminAdapter(
    private val notesList: MutableList<TransaksiModel>,
    private val context: Context,

    ) : RecyclerView.Adapter<ReportAdminAdapter.ViewHolder>() {

    //database
    var userId: String? = null
    private var dialog: Dialog? = null


    interface Dialog {
        fun onClick(
            position: Int,
            nomorpesanan: String,
            nama: String,
            telepon: String,
            alamat: String,
            metodepembayaran: String,
            totalharga: Int,
            tanggal : String,
            jam : String,
            foto : String,
            status : Int,
            id : Int
        )
    }

    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var nomorpesanan: TextView
        internal var harga: TextView
        internal var kalender: TextView
        internal var metodepembayaran: TextView
        internal var status: TextView


        init {
            nomorpesanan = view.findViewById(R.id.txtnomorpesanan)
            harga = view.findViewById(R.id.txtharga)
            status = view.findViewById(R.id.txtstatus)
            kalender = view.findViewById(R.id.txtkalender)
            metodepembayaran = view.findViewById(R.id.txtmetodepembayaran)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_transaksi, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val note = notesList[position]
        holder.nomorpesanan.text = "No. ${note.nomorpesanan!!}"
        holder.harga.text = "Rp. ${note.totalharga}"
        holder.kalender.text = "${note.tanggal.toString()} ${note.jam.toString()}"
        if (note.status == 0){
            holder.status.text = "Dalam proses"
        }
        else if (note.status == 2){
            holder.status.text = "Selesai"
        }
        else if (note.status == 3){
            holder.status.text = "Ditolak"
        }
        else if (note.status == 1){
            holder.status.text = "Sedang dikirim"
        }
        holder.metodepembayaran.text = note.metodepembayaran.toString()
        holder.itemView.setOnClickListener {
            if (dialog != null) {
                dialog!!.onClick(
                    holder.layoutPosition,
                    note.nomorpesanan,
                    note.nama!!,
                    note.telepon!!,
                    note.alamat!!,
                    note.metodepembayaran!!,
                    note.totalharga!!,
                    note.tanggal!!,
                    note.jam!!,
                    note.foto.toString(),
                    note.status!!,
                    note.id!!
                )
            }
        }
    }


}
