package com.satelit.satelitpaint.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.model.SliderModel
import com.squareup.picasso.Picasso

class SliderAdminAdapter(
    private val notesList: MutableList<SliderModel>
    ) : RecyclerView.Adapter<SliderAdminAdapter.ViewHolder>() {

    //database
    var userId: String? = null
    private var dialog: Dialog? = null


    interface Dialog {
        fun onClick(
            position: Int,
            id: Int,
            foto : String

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

        init {
            gambar = view.findViewById(R.id.imgfoto)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.listslider, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val note = notesList[position]
        Picasso.get().load(note.foto.toString()).fit().centerCrop().into(holder.gambar)
        holder.itemView.setOnClickListener {
            if (dialog != null) {
                dialog!!.onClick(
                    holder.layoutPosition,
                    note.id!!,
                    note.foto!!
                )
            }
        }
    }


}
