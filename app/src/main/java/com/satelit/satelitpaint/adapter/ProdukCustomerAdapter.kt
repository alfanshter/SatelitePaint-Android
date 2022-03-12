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
import java.util.*
import kotlin.collections.ArrayList
import android.graphics.Movie
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.DiffResult
import java.text.DecimalFormat
import java.text.NumberFormat


class ProdukCustomerAdapter(
    private val notesList: List<ProdukModel>
    ) : RecyclerView.Adapter<ProdukCustomerAdapter.ViewHolder>(),Filterable {

    //database
    var userId: String? = null
    private var dialog: Dialog? = null

    private var movieList: List<ProdukModel>? = null
    private var movieListFiltered: List<ProdukModel>? = null

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
        internal var deskripsi: TextView


        init {
            gambar = view.findViewById(R.id.imgfoto)
            nama = view.findViewById(R.id.txtnama)
            harga = view.findViewById(R.id.txtharga)
            deskripsi = view.findViewById(R.id.txtdeskripsi)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.listproduk, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val note = notesList[position]
        //convert to money
        val formatter: NumberFormat = DecimalFormat("#,###")
        val myNumber = note.harga
        val harga: String = formatter.format(myNumber)

        holder.nama.text = "${note.nama!!.toUpperCase()}"
        holder.harga.text = "${harga}"
        Picasso.get().load(note.foto.toString()).fit().into(holder.gambar)
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

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    movieListFiltered = movieList
                } else {
                    val filteredList: MutableList<ProdukModel> = ArrayList()
                    for (movie in movieList!!) {
                        if (movie.nama!!.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(movie)
                        }
                    }
                    movieListFiltered = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = movieListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                movieListFiltered = filterResults.values as ArrayList<ProdukModel>
                notifyDataSetChanged()
            }
        }
    }
}
