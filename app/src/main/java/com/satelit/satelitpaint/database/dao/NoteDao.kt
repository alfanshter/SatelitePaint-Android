package com.satelit.satelitpaint.database.dao

import androidx.room.*
import com.satelit.satelitpaint.database.entitas.Checkout
import com.satelit.satelitpaint.database.entitas.Note

@Dao
interface NoteDao {
     @Insert
     suspend fun addCart(note: Note)

     @Query("SELECT * FROM note ORDER BY id notnull DESC")
     suspend fun getNotes() : List<Note>

     @Query("SELECT * FROM note where status=:status ORDER BY id notnull DESC")
     suspend fun getCart(status: Boolean) : List<Note>
//
//     @Query("SELECT * FROM note WHERE id=:note_id")
//     suspend fun getNote(note_id: Int) : List<Note>
//
     @Query("SELECT SUM(jumlah * harga) as total from note where status=:status")
     suspend fun getsum(status : Boolean) : Int
//
     @Query("UPDATE note set status=:status where id=:id")
     suspend fun updateNote(status: Boolean, id : Int)
//
//     @Delete
//     suspend fun deleteNote(note: Note)

     //Cart
     //Del Checkout
     @Query("DELETE FROM note")
     suspend fun deletaCart()


     //Checkout
     @Insert
     suspend fun addCheckOut(note: Checkout)
     //Get Checkout
     @Query("SELECT * FROM checkout ORDER BY id notnull DESC")
     suspend fun getCheckout() : List<Checkout>
     //Del Checkout
     @Query("DELETE FROM checkout")
     suspend fun deleteCheckout()


}


