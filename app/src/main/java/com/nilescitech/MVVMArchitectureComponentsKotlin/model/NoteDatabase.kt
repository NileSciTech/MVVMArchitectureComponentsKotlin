package com.nilescitech.MVVMArchitectureComponentsKotlin.model

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import android.os.AsyncTask

@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    private class PopulateDbAsyncTask internal constructor(db: NoteDatabase) : AsyncTask<Void, Void, Void>() {
        private val noteDao: NoteDao

        init {
            noteDao = db.noteDao()
        }

        override fun doInBackground(vararg voids: Void): Void? {
            noteDao.insert(Note("Title 1", "Description 1", 1))
            noteDao.insert(Note("Title 2", "Description 2", 2))
            noteDao.insert(Note("Title 3", "Description 3", 3))
            return null
        }
    }

    companion object {

        @Volatile
        var dbInstance: NoteDatabase? = null

        private var dbName = "note_database"

   /*     @Synchronized
        fun getInstance(context: Context): NoteDatabase {
            if (dbInstance == null) {
                dbInstance = Room.databaseBuilder<NoteDatabase>(context.applicationContext,
                        NoteDatabase::class.java, dbName)
                        .fallbackToDestructiveMigration()
                        .addCallback(roomCallback)
                        .build()
            }
            return dbInstance as NoteDatabase
        } */

        fun getInstance(context: Context): NoteDatabase {
            val tempInstance = dbInstance

            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, NoteDatabase::class.java, dbName)
                        .fallbackToDestructiveMigration()
                        .addCallback(roomCallback)
                        .build()
                dbInstance = instance
                return instance
            }
        }

        private val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                PopulateDbAsyncTask(dbInstance!!).execute()
            }
        }
    }
}
