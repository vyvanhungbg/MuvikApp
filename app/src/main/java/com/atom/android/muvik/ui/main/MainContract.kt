package com.atom.android.muvik.ui.main

import android.content.Context
import android.os.Bundle
import com.atom.android.muvik.base.BasePresenter
import com.atom.android.muvik.data.model.Song
import com.atom.android.muvik.ui.home.HomeContract

interface MainContract {
    interface View {
        fun handlerActionFromService(actionFlag: String?, song: Song?)
        fun updateTimeSong(elapsedTime: String, remainingTime: String)
        fun updateLoopSetting()
        fun updateMixSongSetting()
        fun updateFavoriteSongSuccess(isFavorite: Boolean)
        fun updateFavoriteSongFailed(mess: String)
    }

    interface Presenter : BasePresenter<MainContract.View> {
        fun registerActionFromService(context: Context)
        fun calcTimeOfSong(mCurrentPosition: Int, totalTime: Int)
        fun unRegisterLocalBroadcastActionFromService(context: Context)
        fun updateFavoriteSong(context: Context, id: String, isFavorite: Boolean)
    }
}
