package com.satelit.satelitpaint.database.entitas

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val idproduk: Int,
    val nama: String,
    val foto: String,
    val deskripsi: String,
    val harga: Int,
    val jumlah: Int,
    val status: Boolean,
    )