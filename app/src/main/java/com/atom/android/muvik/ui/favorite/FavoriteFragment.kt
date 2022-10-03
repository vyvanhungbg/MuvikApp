package com.atom.android.muvik.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.atom.android.muvik.base.BaseFragment
import com.atom.android.muvik.data.model.Song
import com.atom.android.muvik.data.repository.SongRepository
import com.atom.android.muvik.data.source.local.SongLocalDataSource

import com.atom.android.muvik.databinding.FragmentFavoriteBinding
import com.atom.android.muvik.ui.home.AdapterSongs
import com.atom.android.muvik.ui.main.MusicService
import com.atom.android.muvik.utils.Constant
import com.atom.android.muvik.utils.sqlite.SQLiteUtils
import com.atom.android.muvik.utils.sqlite.SongDBHelper

class FavoriteFragment : BaseFragment<FragmentFavoriteBinding>(FragmentFavoriteBinding::inflate),
    FavoriteContract.View, AdapterSongs.ItemClickListener {

    private val adapterSongs = AdapterSongs(this)
    private val presenter by lazy {
        FavoriteFragmentPresenter.getInstance(
            SongRepository.getInstance(
                SongLocalDataSource.getInstance(
                    SongDBHelper.getInstance(
                        SQLiteUtils.getInstance(requireContext())
                    )
                )
            ),
            this
        )
    }

    override fun initData() {

    }

    override fun initialize() {
        // late impl

    }


    override fun callData() {
        // late impl
        presenter.getSongs(activity?.applicationContext)
    }

    override fun initEvent() {
        // late impl
    }

    override fun displaySuccess(list: MutableList<Song>) {
        adapterSongs.setData(list)
    }


    override fun displayFail(message: String) {

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            presenter.getSongs(context)
        }
    }


    override fun onItemClick(position: Int) {
        val intent = Intent(context, MusicService::class.java)
        val bundle = Bundle()
        bundle.putParcelableArrayList(Constant.EXTRA_LIST_SONG, ArrayList(adapterSongs.getData()))
        bundle.putInt(Constant.EXTRA_POSITION_SONG, position)
        bundle.putString(Constant.EXTRA_ACTION_FLAG, Constant.ACTION_PLAY)
        intent.putExtras(bundle)
        context?.startService(intent)
    }

    override fun initView() {
        binding.recyclerViewFavoriteSongs.adapter = adapterSongs
    }

}
