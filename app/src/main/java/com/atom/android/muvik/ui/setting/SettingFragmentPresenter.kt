package com.atom.android.muvik.ui.setting

import android.content.Context
import com.atom.android.muvik.data.IResultListener
import com.atom.android.muvik.data.model.Song
import com.atom.android.muvik.data.repository.SongRepository
import com.atom.android.muvik.ui.home.HomeContract
import com.atom.android.muvik.ui.home.HomeFragmentPresenter

class SettingFragmentPresenter(
    private val repository: SongRepository,
    private val settingView: SettingContract.View
) :
    HomeContract.Presenter {
    override fun getSongs(context: Context?) {
        repository.getSongsLocal(context, object : IResultListener<MutableList<Song>> {
            override fun onSuccess(list: MutableList<Song>) {
                settingView.getSongLocalSuccess(list)
            }

            override fun onFail(message: String) {
                settingView.getSongLocalFail(message)
            }
        })
    }

    override fun onStart() {
        //late impl
    }

    override fun onStop() {
        //late impl
    }

    companion object {
        private var instance: SettingFragmentPresenter? = null
        fun getInstance(repository: SongRepository, settingView: SettingContract.View) =
            synchronized(this) {
                instance ?: SettingFragmentPresenter(repository, settingView).also { instance = it }
            }
    }

}
