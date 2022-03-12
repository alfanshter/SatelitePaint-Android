package com.satelit.satelitpaint.model

data class CartResponse(
    val message : String? = null,
    val data : List<CartModel>
)