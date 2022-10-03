package com.atom.android.muvik.data.source.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import com.atom.android.muvik.R
import com.atom.android.muvik.data.IResultListener
import com.atom.android.muvik.data.ISongDataSource
import com.atom.android.muvik.data.model.Song
import com.atom.android.muvik.data.repository.SongRepository
import com.atom.android.muvik.utils.Constant
import com.atom.android.muvik.utils.extension.extractSong
import com.atom.android.muvik.utils.sqlite.SongDBHelper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SongLocalDataSource(private val songDBHelper: SongDBHelper) : ISongDataSource.Local {

    override fun getSongsLocal(context: Context?, listener: IResultListener<MutableList<Song>>) {
        val songs: MutableList<Song> = mutableListOf()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val inputFormat = context?.resources?.getStringArray(R.array.array_source_input_value)
        val selection: String? = when (Constant.INPUT_FORMAT) {
            inputFormat?.get(IS_MUSIC) -> {
                MediaStore.Audio.Media.IS_MUSIC + "!=0"
            }
            inputFormat?.get(IS_PODCAST) -> {
                MediaStore.Audio.Media.IS_PODCAST + "!=0"
            }
            else -> null
        }

        val projection = arrayOf(
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns._ID
        )
        val c: Cursor? = context?.contentResolver?.query(uri, projection, selection, null, null)
        val message_error = context?.getString(R.string.error_no_songs_founded) ?: ""
        if (c != null) {
            while (c.moveToNext()) {
                val path = c.getString(INDEX_COLUM_PATH)
                val id = c.getString(INDEX_COLUM_ID)
                songs.add(Song.extractSong(id, path))
            }
            c.close()
        } else {
            listener.onFail(message_error)
        }
        listener.onSuccess(songs)
        if (songs.size > 0 && context != null) {
            saveSongsToDataBase(context, songs)
        }

    }

    override fun saveSongsToDataBase(context: Context, list: MutableList<Song>) {
        val executorService: ExecutorService = Executors.newSingleThreadExecutor()
        executorService.submit(
            object : Runnable {
                override fun run() {
                    for (song in list) {
                        songDBHelper.insert(song)
                    }
                }
            }
        )
        executorService.shutdown()
    }

    override fun getSongsFromDataBase(
        context: Context?,
        listener: IResultListener<MutableList<Song>>
    ) {
        context?.let {
            val songs = songDBHelper.findAll()
            listener.onSuccess(songs)
        }
    }

    override fun getFavoriteSongsFromDataBase(
        context: Context?,
        listener: IResultListener<MutableList<Song>>
    ) {
        context?.let {
            val songs = songDBHelper.findFavoriteSongs()
            listener.onSuccess(songs)
        }
    }

    override fun updateFavoriteSong(
        context: Context,
        id: String,
        isFavorite: Boolean,
        listener: IResultListener<Boolean>
    ) {
        val executorService: ExecutorService = Executors.newSingleThreadExecutor()
        executorService.submit(
            object : Runnable {
                override fun run() {
                    val rowEffect = songDBHelper.updateFavorite(id, isFavorite)
                    if (rowEffect > 0) {
                        listener.onSuccess(isFavorite)
                    } else {
                        listener.onFail(context.getString(R.string.text_save_favorite_failed))
                    }
                }
            }
        )
        executorService.shutdown()
    }


    companion object {
        private var instance: SongLocalDataSource? = null
        fun getInstance(
            songDBHelper: SongDBHelper
        ) = synchronized(this) {
            instance ?: SongLocalDataSource(songDBHelper).also { instance = it }
        }

        const val IS_MUSIC = 0
        const val IS_PODCAST = 1
        const val INDEX_COLUM_PATH = 0
        const val INDEX_COLUM_ID = 1
    }


}
