package com.atom.android.muvik.data.repository

import android.content.Context
import com.atom.android.muvik.data.IResultListener
import com.atom.android.muvik.data.ISongDataSource
import com.atom.android.muvik.data.model.Song
import com.atom.android.muvik.data.source.local.SongLocalDataSource

class SongRepository(private val localDataSource: SongLocalDataSource) : ISongDataSource.Local {

    override fun getSongsLocal(context: Context?, list: IResultListener<MutableList<Song>>) {
        localDataSource.getSongsLocal(context, list)
    }

    companion object {
        private var instance: SongRepository? = null
        fun getInstance(
            localDataSource: SongLocalDataSource
        ) = synchronized(this) {
            instance ?: SongRepository(localDataSource).also { instance = it }
        }
    }
}
