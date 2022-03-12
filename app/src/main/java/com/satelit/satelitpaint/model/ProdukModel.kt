package com.satelit.satelitpaint.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProdukModel(
    @SerializedName("id")
    @Expose
    val id: Int? = null,

    @SerializedName("nama")
    @Expose
    val nama: String? = null,

    @SerializedName("foto")
    @Expose
    val foto: String? = null,

    @SerializedName("deskripsi")
    @Expose
    val deskripsi: String? = null,

    @SerializedName("stok")
    @Expose
    val stok: Int? = null,

    @SerializedName("mingrosir")
    @Expose
    val mingrosir: Int? = null,

    @SerializedName("maxgrosir")
    @Expose
    val maxgrosir: Int? = null,

    @SerializedName("harga")
    @Expose
    val harga: Int? = null,

    @SerializedName("hargagrosir")
    @Expose
    val hargagrosir: Int? = null,

    @SerializedName("rating")
    @Expose
    val rating: Float? = null

)