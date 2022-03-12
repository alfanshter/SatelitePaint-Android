package com.satelit.satelitpaint.model

import com.google.gson.annotations.SerializedName


data class CicilanModel(

    @field:SerializedName("nomorpesanan")
    val nomorpesanan: String? = null,

    @field:SerializedName("totalharga")
    val totalharga: Int? = null,

    @field:SerializedName("jatuhtempo3")
    val jatuhtempo3: String? = null,

    @field:SerializedName("hargacicilan")
    val hargacicilan: Int? = null,

    @field:SerializedName("jatuhtempo4")
    val jatuhtempo4: String? = null,

    @field:SerializedName("telepon")
    val telepon: String? = null,

    @field:SerializedName("jatuhtempo1")
    val jatuhtempo1: String? = null,

    @field:SerializedName("jatuhtempo2")
    val jatuhtempo2: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("statuscicilan")
    val statuscicilan: Int? = null,

    @field:SerializedName("alamat")
    val alamat: String? = null,

    @field:SerializedName("metodepembayaran")
    val metodepembayaran: String? = null,

    @field:SerializedName("jumlahcicilan")
    val jumlahcicilan: Int? = null,

    @field:SerializedName("nama")
    val nama: String? = null,

    @field:SerializedName("idusers")
    val idusers: String? = null,

    @field:SerializedName("foto")
    val foto: Any? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("jam")
    val jam: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("tanggal")
    val tanggal: String? = null,

    @field:SerializedName("diskon")
    val diskon: Int? = null,

    @field:SerializedName("status")
    val status: Int? = null
)
