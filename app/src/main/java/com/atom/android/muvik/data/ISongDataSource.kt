package com.atom.android.muvik.data

import android.content.Context
import com.atom.android.muvik.data.model.Song

interface ISongDataSource {
    interface Local {
        fun getSongsLocal(context: Context?, list: IResultListener<MutableList<Song>>)
    }
}
