package com.atom.android.muvik.utils.sqlite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import com.atom.android.muvik.data.dao.SongDao
import com.atom.android.muvik.data.model.Song
import com.atom.android.muvik.data.repository.SongRepository
import com.atom.android.muvik.data.source.local.SongLocalDataSource
import com.atom.android.muvik.utils.extension.extractSong

class SongDBHelper(val dbUtils: SQLiteUtils) : SongDao {

    @SuppressLint("Range")
    override fun findAll(): MutableList<Song> {
        val db = dbUtils.readableDatabase
        val projection = arrayOf(
            SQLiteTableContract.SongEntry._ID,
            SQLiteTableContract.SongEntry.COLUMN_NAME_URI,
            SQLiteTableContract.SongEntry.COLUMN_NAME_FAVORITE
        )
        val cursor = db.query(
            SQLiteTableContract.SongEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        val songs = mutableListOf<Song>()
        with(cursor) {
            while (moveToNext()) {
                val id = getString(getColumnIndex(SQLiteTableContract.SongEntry._ID))
                val uri = getString(getColumnIndex(SQLiteTableContract.SongEntry.COLUMN_NAME_URI))
                val favorite =
                    getInt(getColumnIndex(SQLiteTableContract.SongEntry.COLUMN_NAME_FAVORITE))
                try {
                    songs.add(Song.extractSong(id, uri, favorite))
                } catch (ex: Exception) {
                    delete(id)
                }
            }
            cursor.close()
        }
        db.close()
        return songs
    }

    @SuppressLint("Range")
    override fun findFavoriteSongs(): MutableList<Song> {
        val db = dbUtils.readableDatabase
        val projection = arrayOf(
            SQLiteTableContract.SongEntry._ID,
            SQLiteTableContract.SongEntry.COLUMN_NAME_URI,
            SQLiteTableContract.SongEntry.COLUMN_NAME_FAVORITE
        )
        val selection = "${SQLiteTableContract.SongEntry.COLUMN_NAME_FAVORITE} = ?"
        val selectionArgs = arrayOf(SQLiteTableContract.SongEntry.IS_FAVORITE)
        val cursor = db.query(
            SQLiteTableContract.SongEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        val songs = mutableListOf<Song>()
        with(cursor) {
            while (moveToNext()) {
                val id = getString(getColumnIndex(SQLiteTableContract.SongEntry._ID))
                val uri = getString(getColumnIndex(SQLiteTableContract.SongEntry.COLUMN_NAME_URI))
                val favorite =
                    getInt(getColumnIndex(SQLiteTableContract.SongEntry.COLUMN_NAME_FAVORITE))
                songs.add(Song.extractSong(id, uri, favorite))
            }
            cursor.close()
        }
        db.close()
        return songs
    }

    override fun insert(song: Song) {
        val db = dbUtils.writableDatabase
        val values = ContentValues().apply {
            put(SQLiteTableContract.SongEntry._ID, song.id)
            put(SQLiteTableContract.SongEntry.COLUMN_NAME_URI, song.url)
            put(SQLiteTableContract.SongEntry.COLUMN_NAME_FAVORITE, song.favorite)
        }
        if (exists(song.id)) {
            update(song.id, song.name, song.url)
        } else {
            val newRowId = db.insert(SQLiteTableContract.SongEntry.TABLE_NAME, null, values)
        }
        db.close()

    }

    override fun update(id: String, name: String?, uri: String?): Int {
        val db = dbUtils.writableDatabase
        val values = ContentValues().apply {
            put(SQLiteTableContract.SongEntry.COLUMN_NAME_NAME, name)
            put(SQLiteTableContract.SongEntry.COLUMN_NAME_URI, uri)
        }
        val selection = "${SQLiteTableContract.SongEntry._ID} = ?"
        val selectionArgs = arrayOf(id)
        val countRowEffect = db.update(
            SQLiteTableContract.SongEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )
        db.close()

        return countRowEffect
    }

    override fun updateFavorite(id: String, favorite: Boolean): Int {
        val db = dbUtils.writableDatabase
        val values = ContentValues().apply {
            put(SQLiteTableContract.SongEntry.COLUMN_NAME_FAVORITE, favorite)
        }
        val selection = "${SQLiteTableContract.SongEntry._ID} = ?"
        val selectionArgs = arrayOf(id)
        val countRowEffect = db.update(
            SQLiteTableContract.SongEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )
        db.close()
        return countRowEffect
    }


    override fun delete(id: String): Int {
        val db = dbUtils.writableDatabase
        val selection = "${SQLiteTableContract.SongEntry._ID} = ?"
        val selectionArgs = arrayOf(id)
        val countRowEffect = db.delete(
            SQLiteTableContract.SongEntry.TABLE_NAME,
            selection,
            selectionArgs
        )
        db.close()
        return countRowEffect
    }

    @SuppressLint("Range")
    override fun findByName(name: String): MutableList<Song> {
        val db = dbUtils.readableDatabase
        val projection = arrayOf(
            SQLiteTableContract.SongEntry._ID,
            SQLiteTableContract.SongEntry.COLUMN_NAME_NAME,
            SQLiteTableContract.SongEntry.COLUMN_NAME_URI,
            SQLiteTableContract.SongEntry.COLUMN_NAME_FAVORITE
        )
        val selection = "${SQLiteTableContract.SongEntry.COLUMN_NAME_NAME} LIKE ?"
        val selectionArgs = arrayOf(name)
        val cursor = db.query(
            SQLiteTableContract.SongEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        val songs = mutableListOf<Song>()
        with(cursor) {
            while (moveToNext()) {
                val id = getString(getColumnIndex(SQLiteTableContract.SongEntry._ID))
                val uri = getString(getColumnIndex(SQLiteTableContract.SongEntry.COLUMN_NAME_URI))
                val favorite =
                    getInt(getColumnIndex(SQLiteTableContract.SongEntry.COLUMN_NAME_FAVORITE))
                try {
                    songs.add(Song.extractSong(id, uri, favorite))
                } catch (ex: Exception) {
                    delete(id)
                }
            }
            cursor.close()
        }
        db.close()
        return songs
    }

    override fun exists(id: String): Boolean {
        val db = dbUtils.readableDatabase
        val projection = arrayOf(
            SQLiteTableContract.SongEntry._ID,
        )
        val selection = "${SQLiteTableContract.SongEntry._ID} = ?"
        val selectionArgs = arrayOf(id)
        val cursor = db.query(
            SQLiteTableContract.SongEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        val result = cursor != null && cursor.count > 0
        cursor?.close()
        db.close()
        return result
    }

    companion object {
        private var instance: SongDBHelper? = null
        fun getInstance(
            sqLiteUtils: SQLiteUtils
        ) = synchronized(this) {
            instance ?: SongDBHelper(sqLiteUtils).also { instance = it }
        }
    }

}
