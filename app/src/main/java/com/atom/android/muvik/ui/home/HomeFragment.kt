package com.atom.android.muvik.ui.home

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.atom.android.muvik.R
import com.atom.android.muvik.base.BaseFragment
import com.atom.android.muvik.data.model.Song
import com.atom.android.muvik.data.repository.SongRepository
import com.atom.android.muvik.data.source.local.SongLocalDataSource
import com.atom.android.muvik.databinding.FragmentHomeBinding
import com.atom.android.muvik.ui.main.MusicService
import com.atom.android.muvik.utils.Constant
import com.atom.android.muvik.utils.extension.toast
import com.atom.android.muvik.utils.sqlite.SQLiteUtils
import com.atom.android.muvik.utils.sqlite.SongDBHelper


class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    HomeContract.View, AdapterSongs.ItemClickListener {

    private val adapterSongs by lazy { AdapterSongs(this) }
    private val presenter by lazy {
        HomeFragmentPresenter.getInstance(
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
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                presenter.getSongs(activity?.applicationContext)
            } else {
                context?.toast(getString(R.string.string_permission))
            }
        }
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    override fun initialize() {

    }

    override fun callData() {

    }

    override fun initEvent() {

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

    override fun displaySuccess(list: MutableList<Song>) {
        adapterSongs.setData(list)
    }

    override fun displayFail(message: String) {
        context?.toast(message)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            presenter.getSongs(context)
        }
    }

    override fun initView() {
        binding.recyclerViewSongs.adapter = adapterSongs
    }

}
