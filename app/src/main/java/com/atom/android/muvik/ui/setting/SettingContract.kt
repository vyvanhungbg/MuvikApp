package com.atom.android.muvik.ui.setting

import android.content.Context
import com.atom.android.muvik.base.BasePresenter
import com.atom.android.muvik.data.model.Song

interface SettingContract {

    interface View {
        fun getSongLocalSuccess(list: MutableList<Song>)
        fun getSongLocalFail(message: String)
    }

    interface Presenter : BasePresenter<View> {
        fun getSongsLocal(context: Context?)
    }
}
