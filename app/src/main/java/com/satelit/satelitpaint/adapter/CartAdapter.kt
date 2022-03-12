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
import java.text.DecimalFormat
import java.text.NumberFormat


class CartAdapter(
    private val notesList: MutableList<CartModel>,
    private val context: Context,

    ) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

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

        fun ceklist(
            position: Int,
            id : Int,
            cek : CheckBox
        )

        fun plus(
            id: Int,
            jumlah : Int,
            counter : TextView
        )


        fun min(
            id: Int,
            jumlah : Int,
            counter : TextView
        )

        fun hapus(
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
        internal var mines: TextView
        internal var plus: TextView
        internal var counter: TextView
        internal var cek: CheckBox
        internal var hapus: ImageView


        init {
            foto = view.findViewById(R.id.imgfoto)
            nama = view.findViewById(R.id.txtnama)
            harga = view.findViewById(R.id.txtharga)
            mines = view.findViewById(R.id.btnmin)
            plus = view.findViewById(R.id.btnplus)
            counter = view.findViewById(R.id.txtcounter)
            cek = view.findViewById(R.id.checkbox)
            hapus = view.findViewById(R.id.btnhapus)

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_keranjang, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val note = notesList[position]
        if (note.pickcart==1){
            holder.cek.isChecked = true
        }else{
            holder.cek.isChecked = false
        }

        holder.cek.setOnClickListener {
            if (dialog!=null){
                dialog!!.ceklist(position,note.id!!,holder.cek)
            }
        }

        holder.plus.setOnClickListener {
            if (dialog!=null){
                dialog!!.plus(note.id!!,note.jumlah!!,holder.counter)
            }

        }

        holder.mines.setOnClickListener {
            if (dialog!=null){
                dialog!!.min(note.id!!,note.jumlah!!,holder.counter)
            }

        }

        holder.hapus.setOnClickListener {
            if (dialog!=null){
                dialog!!.hapus(note.id!!)
            }

        }




        holder.nama.text = "${note.nama!!.toUpperCase()}"
        val formatter: NumberFormat = DecimalFormat("#,###")
        val myNumber = note.harga
        val convertharga: String = formatter.format(myNumber)

        holder.harga.text = "Rp. ${convertharga}"
        holder.counter.text = note.jumlah.toString()
        Picasso.get().load(note.foto.toString()).fit().centerCrop().into(holder.foto)
    }


}


