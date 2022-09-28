package com.atom.android.muvik.ui.home

import android.content.Context
import com.atom.android.muvik.base.BasePresenter
import com.atom.android.muvik.data.model.Song

interface HomeContract {
    interface View {
        fun displaySuccess(list: MutableList<Song>)
        fun displayFail(message: String)
    }

    interface Presenter : BasePresenter<HomeContract.View> {
        fun getSongs(context: Context?)
    }
}
