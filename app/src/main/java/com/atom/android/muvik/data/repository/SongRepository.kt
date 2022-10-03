package com.atom.android.muvik.data.repository

import android.content.Context
import com.atom.android.muvik.data.IResultListener
import com.atom.android.muvik.data.ISongDataSource
import com.atom.android.muvik.data.model.Song
import com.atom.android.muvik.data.source.local.SongLocalDataSource

class SongRepository(private val localDataSource: ISongDataSource.Local) : ISongDataSource.Local {

    override fun getSongsLocal(context: Context?, listener: IResultListener<MutableList<Song>>) {
        localDataSource.getSongsLocal(context, listener)

    }

    override fun saveSongsToDataBase(context: Context, list: MutableList<Song>) {
        localDataSource.saveSongsToDataBase(context, list)
    }

    override fun getSongsFromDataBase(
        context: Context?,
        listener: IResultListener<MutableList<Song>>
    ) {
        localDataSource.getSongsFromDataBase(context, listener)
    }

    override fun getFavoriteSongsFromDataBase(
        context: Context?,
        listener: IResultListener<MutableList<Song>>
    ) {
        localDataSource.getFavoriteSongsFromDataBase(context, listener)
    }

    override fun updateFavoriteSong(
        context: Context,
        id: String,
        isFavorite: Boolean,
        listener: IResultListener<Boolean>
    ) {
        localDataSource.updateFavoriteSong(context, id, isFavorite, listener)
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
