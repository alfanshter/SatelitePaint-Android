package com.satelit.satelitpaint.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.model.DaftarCicilanModel
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.text.NumberFormat

class DaftarCicilanAdminAdapter(
    private val notesList: MutableList<DaftarCicilanModel>,
    private val context: Context,

    ) : RecyclerView.Adapter<DaftarCicilanAdminAdapter.ViewHolder>() {

    //database
    var userId: String? = null
    private var dialog: Dialog? = null


    interface Dialog {
        fun onClick(
            position: Int,
            note: DaftarCicilanModel
        )

        fun  info(status : Int, note : DaftarCicilanModel)
    }

    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var nomorpesanan: TextView
        internal var jatuhtempo: TextView
        internal var cicilenke: TextView
        internal var keterangan: TextView
        internal var jumlahbayar: TextView


        init {
            nomorpesanan = view.findViewById(R.id.txtnomorpesanan)
            jatuhtempo = view.findViewById(R.id.txtjatuhtempo)
            keterangan = view.findViewById(R.id.txtketerangan)

            jumlahbayar = view.findViewById(R.id.txtjumlahbayar)
            cicilenke = view.findViewById(R.id.txtcicilanke)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_bayarcicilan, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val note = notesList[position]
        holder.nomorpesanan.text = "Nomor Pesanan : ${note.nomorpesanan}"
        holder.cicilenke.text = "Cicilen ke : ${position+1}"
        holder.jatuhtempo.text = "Jatuh Tempo : ${note.jatuhtempo}"
        holder.jumlahbayar.text = "Jumlah Bayar : ${note.jumlahbayar}"
        if (note.status == 0){
            holder.keterangan.text = "Belum lunas"
        }
        else if (note.status == 1){
            holder.keterangan.text = "Sudah Lunas"
        }


        holder.itemView.setOnClickListener {
            if (dialog != null) {
                dialog!!.onClick(
                    holder.layoutPosition,
                    note
                )
            }
        }
    }


}
