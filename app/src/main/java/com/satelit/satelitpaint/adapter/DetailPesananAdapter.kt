package com.satelit.satelitpaint.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.database.entitas.Note
import com.satelit.satelitpaint.model.CartModel
import com.satelit.satelitpaint.model.ProdukModel
import com.squareup.picasso.Picasso


class DetailPesananAdapter(
    private val notesList: MutableList<CartModel>
    ) : RecyclerView.Adapter<DetailPesananAdapter.ViewHolder>() {

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
        internal var foto: ImageView
        internal var nama: TextView
        internal var harga: TextView

        init {
            foto = view.findViewById(R.id.imgfoto)
            nama = view.findViewById(R.id.txtnama)
            harga = view.findViewById(R.id.txtharga)


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_detailpesanan, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val note = notesList[position]

        holder.nama.text = "${note.nama!!.toUpperCase()}"
        holder.harga.text = "${note.jumlah} x ${note.harga}"
        Picasso.get().load(note.foto.toString()).fit().centerCrop().into(holder.foto)
    }


}


