package com.satelit.satelitpaint.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.model.ProdukModel
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.text.NumberFormat

class ProdukAdapter(
    private val notesList: List<ProdukModel>
    ) : RecyclerView.Adapter<ProdukAdapter.ViewHolder>() {

    //database
    var userId: String? = null
    private var dialog: Dialog? = null


    interface Dialog {
        fun onClick(
            position: Int,
            nama: String,
            harga: Int,
            deskripsi: String,
            foto: String,
            rating: Float,
            stok: Int,
            id: Int
        )
    }

    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var gambar: ImageView
        internal var nama: TextView
        internal var harga: TextView
        internal var edit: ImageView


        init {
            gambar = view.findViewById(R.id.imggambar)
            nama = view.findViewById(R.id.txtname)
            harga = view.findViewById(R.id.txtharga)
            edit = view.findViewById(R.id.btedit)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_produkadmin, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val note = notesList[position]
        //convert to money
        val formatter: NumberFormat = DecimalFormat("#,###")
        val myNumber = note.harga
        val harga: String = formatter.format(myNumber)

        holder.nama.text = "${note.nama!!.toUpperCase()}"
        holder.harga.text = "Rp. ${harga}"
        Picasso.get().load(note.foto.toString()).fit().centerCrop().into(holder.gambar)
        holder.itemView.setOnClickListener {
            if (dialog != null) {
                dialog!!.onClick(
                    holder.layoutPosition,
                    note.nama,
                    note.harga!!,
                    note.deskripsi!!,
                    note.foto!!,
                    note.rating!!,
                    note.stok!!,
                    note.id!!
                )
            }
        }
    }


}
