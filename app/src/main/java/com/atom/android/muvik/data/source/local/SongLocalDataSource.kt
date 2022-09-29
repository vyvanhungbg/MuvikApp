package com.atom.android.muvik.data.source.local

import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import com.atom.android.muvik.R
import com.atom.android.muvik.data.IResultListener
import com.atom.android.muvik.data.ISongDataSource
import com.atom.android.muvik.data.model.Song
import com.atom.android.muvik.utils.Constant

class SongLocalDataSource : ISongDataSource.Local {

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
                songs.add(extractSong(id, path))
            }
            c.close()
        }else{
            listener.onFail(message_error)
        }
        listener.onSuccess(songs)

    }

    private fun extractSong(id: String, path: String): Song {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(path)
        val image = mediaMetadataRetriever.embeddedPicture

        val album = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
        val artist = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        val genre = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)

        val duration = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

        val name = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        val song = Song(id, name, artist, album, duration, path, image)
        return song
    }

    companion object {
        private var instance: SongLocalDataSource? = null
        fun getInstance() = synchronized(this) {
            instance ?: SongLocalDataSource().also { instance = it }
        }

        const val IS_MUSIC = 0
        const val IS_PODCAST = 1
        val INDEX_COLUM_PATH = 0
        val INDEX_COLUM_ID = 1
    }
}
