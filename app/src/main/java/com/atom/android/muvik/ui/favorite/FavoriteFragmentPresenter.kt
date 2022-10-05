package com.atom.android.muvik.ui.favorite

import android.content.Context
import com.atom.android.muvik.data.IResultListener
import com.atom.android.muvik.data.model.Song
import com.atom.android.muvik.data.repository.SongRepository

class FavoriteFragmentPresenter(
    private val repository: SongRepository,
    private val favoriteView: FavoriteContract.View
) : FavoriteContract.Presenter {

    override fun getSongs(context: Context?) {
        repository.getFavoriteSongsFromDataBase(
            context,
            object : IResultListener<MutableList<Song>> {
                override fun onSuccess(list: MutableList<Song>) {
                    favoriteView.displaySuccess(list)
                }

                override fun onFail(message: String) {
                    favoriteView.displayFail(message)
                }
            })
    }

    override fun onStart() {
        // late impl
    }

    override fun onStop() {
        // late impl
    }

    companion object {
        private var instance: FavoriteFragmentPresenter? = null
        fun getInstance(repository: SongRepository, favoriteView: FavoriteContract.View) =
            synchronized(this) {
                instance ?: FavoriteFragmentPresenter(repository, favoriteView).also {
                    instance = it
                }
            }
    }
}
