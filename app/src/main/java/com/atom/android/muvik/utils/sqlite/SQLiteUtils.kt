package com.atom.android.muvik.utils.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.atom.android.muvik.data.dao.SongDao

class SQLiteUtils(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQLiteTableContract.SongEntry.SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL(SQLiteTableContract.SongEntry.SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    companion object {
        private var instance: SQLiteUtils? = null
        fun getInstance(context: Context) = synchronized(this) {
            instance ?: SQLiteUtils(context).also { instance = it }
        }

        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "MUSIC.db"
    }
}
