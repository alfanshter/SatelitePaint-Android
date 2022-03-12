package com.satelit.satelitpaint.model

data class CartModel(
    val id : Int? = null,
    val idusers : String? = null,
    val idproduk : Int? = null,
    val nomorpesanan : String? = null,
    val nama : String? = null,
    val foto : String? = null,
    val deskripsi : String? = null,
    val harga : Int? = null,
    val totalharga : Int? = null,
    var jumlah : Int? = null,
    val status : Int? = null,
    val pickcart : Int? = null
)