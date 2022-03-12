package com.satelit.satelitpaint.adapter

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.model.CicilanModel
import com.satelit.satelitpaint.model.SliderModel
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class ShowSliderAdapter(
) :
    SliderViewAdapter<ShowSliderAdapter.VH>(),AnkoLogger {
    private var mSliderItems = ArrayList<String>()
    private var mSliderItemslink = ArrayList<String>()
    private var mSliderModel = mutableListOf<SliderModel>()
    private var dialog: Dialog? = null
    fun renewItems(sliderItems: ArrayList<String>,sliderItemslink: ArrayList<String>,slidermodel : MutableList<SliderModel>) {
        mSliderItems = sliderItems
        mSliderItemslink = sliderItemslink
        mSliderModel= slidermodel
        notifyDataSetChanged()
    }


    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    interface Dialog {
        fun onClick(
            position: Int,
            note: SliderModel
        )
    }

    fun addItem(sliderItem: String,sliderlink : String) {
        mSliderItems.add(sliderItem)
        mSliderItemslink.add(sliderlink)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup): VH {
        val inflate: View = LayoutInflater.from(parent.context).inflate(R.layout.listshowslider, null)
        return VH(inflate)
    }

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        //load image into view
//        val note = notesList[position]
        Picasso.get().load(mSliderItems[position]).fit().centerCrop().into(viewHolder.imageView)
        viewHolder.itemView.setOnClickListener {
            if (dialog!=null){
                dialog!!.onClick(position,mSliderModel[position])
            }
        }
    }

    override fun getCount(): Int {
        mSliderModel.size
        mSliderItemslink.size
        return mSliderItems.size
    }

    inner class VH(itemView: View) : ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.imageSlider)

    }
}