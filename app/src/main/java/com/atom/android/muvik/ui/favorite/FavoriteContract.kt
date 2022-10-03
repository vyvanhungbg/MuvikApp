package com.atom.android.muvik.ui.favorite

import android.content.Context
import com.atom.android.muvik.base.BasePresenter
import com.atom.android.muvik.data.model.Song
import com.atom.android.muvik.ui.home.HomeContract

interface FavoriteContract {
    interface View {
        fun displaySuccess(list: MutableList<Song>)
        fun displayFail(message: String)
    }

    interface Presenter : BasePresenter<FavoriteContract.View> {
        fun getSongs(context: Context?)
    }
}
