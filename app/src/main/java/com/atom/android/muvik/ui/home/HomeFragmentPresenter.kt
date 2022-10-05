package com.atom.android.muvik.ui.home

import android.content.Context
import com.atom.android.muvik.data.IResultListener
import com.atom.android.muvik.data.model.Song
import com.atom.android.muvik.data.repository.SongRepository
import com.atom.android.muvik.utils.Constant

class HomeFragmentPresenter(
    private val repository: SongRepository,
    private val homeView: HomeContract.View
) :
    HomeContract.Presenter {

    override fun getSongs(context: Context?) {
        val listener = object : IResultListener<MutableList<Song>> {
            override fun onSuccess(list: MutableList<Song>) {
                homeView.displaySuccess(list)
            }

            override fun onFail(message: String) {
                homeView.displayFail(message)
            }
        }
        if (Constant.FIRST_LAUNCH_APP) {
            repository.getSongsLocal(context, listener)
        } else {
            repository.getSongsFromDataBase(context, listener)
        }
    }

    override fun onStart() {

    }

    override fun onStop() {

    }

    companion object {
        private var instance: HomeFragmentPresenter? = null
        fun getInstance(repository: SongRepository, homeView: HomeContract.View) =
            synchronized(this) {
                instance ?: HomeFragmentPresenter(repository, homeView).also { instance = it }
            }
    }


}
