package com.satelit.satelitpaint.model

data class TransaksiModel(
    val id : Int? = null,
    val nama : String? = null,
    val telepon : String? = null,
    val alamat : String? = null,
    val nomorpesanan : String? = null,
    val metodepembayaran : String? = null,
    val totalharga : Int? = null,
    val diskon : Int? = null,
    val tanggal : String? = null,
    val jam : String? = null,
    val status : Int? = null,
    val idusers : String? = null,
    val foto : String? = null
)