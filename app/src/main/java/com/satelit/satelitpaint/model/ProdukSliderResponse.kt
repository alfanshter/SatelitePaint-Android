package com.satelit.satelitpaint.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ProdukSliderResponse{
    val message : String? = null
    val data : List<ProdukModel> =ArrayList()

    class ProdukModel{
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("nama")
        @Expose
        var nama: String? = null

        @SerializedName("foto")
        @Expose
        val foto: String? = null

        @SerializedName("deskripsi")
        @Expose
        val deskripsi: String? = null

        @SerializedName("stok")
        @Expose
        val stok: Int? = null

        @SerializedName("mingrosir")
        @Expose
        val mingrosir: Int? = null

        @SerializedName("maxgrosir")
        @Expose
        val maxgrosir: Int? = null

        @SerializedName("harga")
        @Expose
        val harga: Int? = null

        @SerializedName("hargagrosir")
        @Expose
        val hargagrosir: Int? = null

        @SerializedName("rating")
        @Expose
        val rating: Float? = null

        override fun toString(): String {
            return nama!!
        }
    }

}


