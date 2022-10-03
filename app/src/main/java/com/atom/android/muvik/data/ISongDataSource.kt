package com.atom.android.muvik.data

import android.content.Context
import com.atom.android.muvik.data.model.Song

interface ISongDataSource {
    interface Local {
        fun getSongsLocal(context: Context?, listener: IResultListener<MutableList<Song>>)
        fun saveSongsToDataBase(context: Context, list: MutableList<Song>)
        fun getSongsFromDataBase(context: Context?, listener: IResultListener<MutableList<Song>>)
        fun getFavoriteSongsFromDataBase(context: Context?, listener: IResultListener<MutableList<Song>>)
        fun updateFavoriteSong(context: Context, id: String, isFavorite: Boolean, listener: IResultListener<Boolean>)
    }
}
