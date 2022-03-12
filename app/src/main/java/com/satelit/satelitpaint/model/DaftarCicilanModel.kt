package com.satelit.satelitpaint.model

import com.google.gson.annotations.SerializedName


data class DaftarCicilanModel(

    @field:SerializedName("nomorpesanan")
    val nomorpesanan: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: Any? = null,

    @field:SerializedName("jumlahbayar")
    val jumlahbayar: Int? = null,

    @field:SerializedName("jatuhtempo")
    val jatuhtempo: String? = null,

    @field:SerializedName("created_at")
    val createdAt: Any? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("status")
    val status: Int? = null
)
