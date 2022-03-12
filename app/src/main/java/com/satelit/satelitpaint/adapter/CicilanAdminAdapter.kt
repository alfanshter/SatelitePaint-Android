package com.satelit.satelitpaint.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.model.CicilanModel
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.text.NumberFormat

class CicilanAdminAdapter(
    private val notesList: MutableList<CicilanModel>,
    private val context: Context,

    ) : RecyclerView.Adapter<CicilanAdminAdapter.ViewHolder>() {

    //database
    var userId: String? = null
    private var dialog: Dialog? = null


    interface Dialog {
        fun onClick(
            position: Int,
            note: CicilanModel
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
        internal var jatuhtempo: TextView
        internal var cicilanke: TextView
        internal var jumlahbayar: TextView
        internal var status: TextView


        init {
            nomorpesanan = view.findViewById(R.id.txtnomorpesanan)
            jatuhtempo = view.findViewById(R.id.txtjatuhtempo)
            status = view.findViewById(R.id.txtstatus)
            cicilanke = view.findViewById(R.id.txtcicilanke)
            jumlahbayar = view.findViewById(R.id.txtjumlahbayar)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_cicilan, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val note = notesList[position]
        holder.cicilanke.text = "Jumlah cicilan : ${note.jumlahcicilan} X"
        holder.nomorpesanan.text = "No. ${note.nomorpesanan!!}"
        if (note.statuscicilan==1){
            holder.jatuhtempo.text = "Jatuh tempo : ${note.jatuhtempo1}"
        }
        else if (note.statuscicilan==2){
            holder.jatuhtempo.text = "Jatuh tempo : ${note.jatuhtempo2}"
        }
        else if (note.statuscicilan==3){
            holder.jatuhtempo.text = "Jatuh tempo : ${note.jatuhtempo3}"
        }
        else if (note.statuscicilan==4){
            holder.jatuhtempo.text = "Jatuh tempo : ${note.jatuhtempo4}"
        }

        val formatter: NumberFormat = DecimalFormat("#,###")
        val myNumber = note.hargacicilan
        val hargacicilan: String = formatter.format(myNumber)


        holder.jumlahbayar.text = "Rp. ${hargacicilan}"


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
            holder.status.text = "Sedang dalam cicilan"
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
