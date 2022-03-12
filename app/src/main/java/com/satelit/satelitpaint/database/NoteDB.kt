package com.satelit.satelitpaint.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.satelit.satelitpaint.database.dao.NoteDao
import com.satelit.satelitpaint.database.entitas.Checkout
import com.satelit.satelitpaint.database.entitas.Note

@Database(
    entities = [Note::class,Checkout::class],
    version = 1
)
abstract class NoteDB : RoomDatabase(){

    abstract fun noteDao() : NoteDao

    companion object {

        @Volatile private var instance : NoteDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            NoteDB::class.java,
            "note12345.db"
        ).build()

    }
}