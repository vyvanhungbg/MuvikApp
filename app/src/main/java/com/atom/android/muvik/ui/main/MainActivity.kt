package com.atom.android.muvik.ui.main

import android.content.*
import android.os.IBinder
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.atom.android.muvik.R
import com.atom.android.muvik.base.BaseActivity
import com.atom.android.muvik.data.model.Song
import com.atom.android.muvik.data.repository.SongRepository
import com.atom.android.muvik.data.source.local.SongLocalDataSource
import com.atom.android.muvik.databinding.ActivityMainBinding
import com.atom.android.muvik.ui.favorite.FavoriteFragment
import com.atom.android.muvik.ui.home.HomeFragment
import com.atom.android.muvik.ui.setting.SettingFragment
import com.atom.android.muvik.utils.Constant
import com.atom.android.muvik.utils.extension.setImage
import com.atom.android.muvik.utils.extension.toast
import com.atom.android.muvik.utils.sqlite.SQLiteUtils
import com.atom.android.muvik.utils.sqlite.SongDBHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate),
    MainContract.View {

    var musicService: MusicService? = null
    val progressService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var isPlaying = false
    private val presenter by lazy {
        MainActivityPresenter.getInstance(
            SongRepository.getInstance(
                SongLocalDataSource.getInstance(
                    SongDBHelper.getInstance(
                        SQLiteUtils.getInstance(applicationContext)
                    )
                )
            ),
            this
        )
    }
    val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, iBinder: IBinder?) {
            val mBinder: MusicService.MusicBinder = iBinder as MusicService.MusicBinder
            musicService = mBinder.getService()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {

        }

    }

    override fun initView() {
        binding.viewPage.adapter = ViewPagerMainAdapter(
            this@MainActivity,
            listOf<Fragment>(HomeFragment(), FavoriteFragment(), SettingFragment())
        )
        setBottomSheetState()
        setBottomSheetVisibility(BottomSheetBehavior.STATE_HIDDEN)
    }


    override fun initData() {
        bindMusicService()
    }

    override fun initEvent() {
        binding.layoutDetailSong.imageViewDropDown.setOnClickListener {
            val bottomSheetBehavior = BottomSheetBehavior.from(binding.designBottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.imageViewPlaySongSub.setOnClickListener {
            playORPauseMusic()
        }
        binding.imageViewNextSongSub.setOnClickListener {
            sendActionToService(Constant.ACTION_NEXT)
        }
        binding.layoutDetailSong.imageViewPlaySongMain.setOnClickListener {
            playORPauseMusic()
        }
        binding.layoutDetailSong.imageViewNextSong.setOnClickListener {
            sendActionToService(Constant.ACTION_NEXT)
        }
        binding.layoutDetailSong.imageViewPreviousSong.setOnClickListener {
            sendActionToService(Constant.ACTION_PREVIOUS)
        }
        binding.layoutDetailSong.imageViewLoopSong.setOnClickListener {
            presenter.saveLoopSetting(this)
        }
        binding.layoutDetailSong.imageViewMixList.setOnClickListener {
            presenter.saveMixSongSetting(this)
        }
        fun clickFavorite() {
            musicService?.getSongPlaying()?.let {
                presenter.updateFavoriteSong(this, it.id, !it.favorite)
            }
        }
        binding.imageViewFavoriteSongSub.setOnClickListener {
            clickFavorite()
        }
        binding.layoutDetailSong.imageViewFavoriteSong.setOnClickListener {
            clickFavorite()
        }
        presenter.registerActionFromService(this)
    }


    private fun setBottomSheetVisibility(state: Int) {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.designBottomSheet)
        when (state) {
            BottomSheetBehavior.STATE_COLLAPSED -> {
                bottomSheetBehavior.isHideable = false
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                binding.navView.visibility = View.VISIBLE
            }
            BottomSheetBehavior.STATE_HIDDEN -> {
                bottomSheetBehavior.isHideable = true
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.navView.visibility = View.VISIBLE
            }

            BottomSheetBehavior.STATE_EXPANDED -> {
                bottomSheetBehavior.isHideable = false
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                binding.navView.visibility = View.GONE
            }
        }

    }

    private fun setBottomSheetState() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.designBottomSheet)
        val navHeight: Float by lazy { binding.navView.height.toFloat() }
        // set state of layout control song with state bottom sheet
        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            binding.layoutControlSongSub.visibility = View.VISIBLE
                            binding.navView.visibility = View.VISIBLE
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            binding.layoutControlSongSub.visibility = View.GONE
                        }

                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // Start the animation of navigation View
                    if (slideOffset >= 0)
                        binding.navView.animate()
                            .translationY(navHeight * slideOffset)
                            .alpha(1.0f)
                            .setDuration(0)
                            .setListener(null);
                }
            }
        )

        //
        fun setBottomSheetWhenNoSongPlaying() {
            if (musicService == null || musicService?.getSongPlaying() == null) {
                setBottomSheetVisibility(BottomSheetBehavior.STATE_HIDDEN)
            } else {
                setBottomSheetVisibility(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }
        // set state  state bottom sheet with of navigation bottom
        binding.navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    setBottomSheetWhenNoSongPlaying()
                    binding.viewPage.currentItem = POSITION_FRAGMENT_HOME
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_favorite -> {
                    setBottomSheetWhenNoSongPlaying()
                    binding.viewPage.currentItem = POSITION_FRAGMENT_FAVORITE
                    return@setOnItemSelectedListener true
                }
                else -> {
                    setBottomSheetVisibility(BottomSheetBehavior.STATE_HIDDEN)
                    binding.viewPage.currentItem = POSITION_FRAGMENT_SETTING
                    return@setOnItemSelectedListener true
                }
            }

        }

        // set state when swipe viewpager
        binding.viewPage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    POSITION_FRAGMENT_HOME -> {
                        setBottomSheetWhenNoSongPlaying()
                        binding.navView.selectedItemId = R.id.navigation_home
                    }
                    POSITION_FRAGMENT_FAVORITE -> {
                        setBottomSheetWhenNoSongPlaying()
                        binding.navView.selectedItemId = R.id.navigation_favorite
                    }
                    else -> {
                        setBottomSheetVisibility(BottomSheetBehavior.STATE_HIDDEN)
                        binding.navView.selectedItemId = R.id.navigation_setting
                    }
                }
            }
        })

    }


    private fun setSongInfo(song: Song) {
        song.setImage(binding.imageSongSub)
        binding.textNameSongSub.text = song.name
        binding.textNameSingerSub.text = song.artist
        song.setImage(binding.layoutDetailSong.imageViewSongMain)
        binding.layoutDetailSong.textViewNameSongMain.text = song.name
        updateFavoriteSongSuccess(song.favorite)
    }

    private fun setProgressSeekbar() {
        binding.layoutDetailSong.seekbarTimeSong.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, process: Int, p2: Boolean) {
                if (p2) {
                    musicService?.seekToPosition(process)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //late impl
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                // late impl
            }
        })
    }

    private fun sendActionToService(actionFlag: String?) {
        musicService?.handleActionMusic(actionFlag)
    }

    private fun bindMusicService() {
        val intent = Intent(this, MusicService::class.java)
        bindService(
            intent,
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }


    private fun playORPauseMusic() {
        if (isPlaying) {
            sendActionToService(Constant.ACTION_PAUSE)
            binding.imageViewPlaySongSub.setImageResource(R.drawable.ic_play_white_24dp)
        } else {
            sendActionToService(Constant.ACTION_RESUME)
            binding.imageViewPlaySongSub.setImageResource(R.drawable.ic_pause_24dp)
        }
    }

    override fun handlerActionFromService(actionFlag: String?, song: Song?) {
        when (actionFlag) {
            Constant.ACTION_PAUSE -> {
                binding.imageViewPlaySongSub.setImageResource(R.drawable.ic_play_white_24dp)
                binding.layoutDetailSong.imageViewPlaySongMain.setImageResource(R.drawable.ic_play_white_24dp)
                isPlaying = false
            }
            Constant.ACTION_RESUME -> {
                binding.imageViewPlaySongSub.setImageResource(R.drawable.ic_pause_24dp)
                binding.layoutDetailSong.imageViewPlaySongMain.setImageResource(R.drawable.ic_pause_24dp)
                isPlaying = true
            }
            Constant.ACTION_PLAY -> {
                binding.imageViewPlaySongSub.setImageResource(R.drawable.ic_pause_24dp)
                binding.layoutDetailSong.imageViewPlaySongMain.setImageResource(R.drawable.ic_pause_24dp)
                isPlaying = true
                sendActionToService(Constant.EXTRA_START)
                setBottomSheetVisibility(BottomSheetBehavior.STATE_EXPANDED)

                song?.let {
                    setSongInfo(it)
                }
                updateSeekBarAndTimeSong()
                setProgressSeekbar()
                updateLoopSetting()
                updateMixSongSetting()
            }
            Constant.ACTION_NEXT -> {
                isPlaying = true
                song?.let {
                    setSongInfo(it)
                }
            }
            Constant.ACTION_PREVIOUS -> {
                isPlaying = true
                song?.let {
                    setSongInfo(it)
                }
            }
        }

    }

    override fun updateTimeSong(elapsedTime: String, remainingTime: String) {
        binding.layoutDetailSong.textViewStartTime.text = elapsedTime
        binding.layoutDetailSong.textViewTotalTime.text = remainingTime
    }

    override fun updateLoopSetting() {
        musicService?.mediaPlayer?.isLooping = Constant.LOOP_SONG
        if (Constant.LOOP_SONG) {
            binding.layoutDetailSong.imageViewLoopSong.setImageResource(R.drawable.ic_loop_one_24dp)
        } else {
            binding.layoutDetailSong.imageViewLoopSong.setImageResource(R.drawable.ic_no_loop_24dp)
        }
    }

    override fun updateMixSongSetting() {
        if (Constant.MIX_SONG) {
            binding.layoutDetailSong.imageViewMixList.setImageResource(R.drawable.ic_mix_list_24dp)
        } else {
            binding.layoutDetailSong.imageViewMixList.setImageResource(R.drawable.ic_no_mix_24)
        }
    }

    override fun updateFavoriteSongSuccess(isFavorite: Boolean) {
        musicService?.setSongPlaying(isFavorite)
        runOnUiThread {
            if (isFavorite) {
                binding.imageViewFavoriteSongSub.setImageResource(ICON_FAVORITE)
                binding.layoutDetailSong.imageViewFavoriteSong.setImageResource(ICON_FAVORITE)

            } else {

                binding.imageViewFavoriteSongSub.setImageResource(ICON_NOT_FAVORITE)
                binding.layoutDetailSong.imageViewFavoriteSong.setImageResource(ICON_NOT_FAVORITE)
            }
        }

    }

    override fun updateFavoriteSongFailed(mess: String) {
        toast(Toast.LENGTH_SHORT)
    }


    fun updateSeekBarAndTimeSong() {
        musicService?.getDuration()?.let {
            val maxProgressBar = it / Constant.SECOND_TO_MIL
            binding.layoutDetailSong.seekbarTimeSong.max = maxProgressBar
            binding.layoutDetailSong.customProgressBar.max = maxProgressBar

            progressService.scheduleAtFixedRate(
                object : TimerTask() {
                    override fun run() {
                        if (isPlaying) {
                            runOnUiThread {
                                musicService?.mediaPlayer?.getCurrentPosition()?.let {
                                    val mCurrentPosition: Int = it / Constant.SECOND_TO_MIL
                                    binding.layoutDetailSong.seekbarTimeSong.progress =
                                        mCurrentPosition
                                    presenter.calcTimeOfSong(
                                        mCurrentPosition,
                                        binding.layoutDetailSong.seekbarTimeSong.max
                                    )

                                    binding.layoutDetailSong.imageViewSongMain.rotation =
                                        (mCurrentPosition % Constant.DEGREE).toFloat()
                                    binding.imageSongSub.rotation =
                                        (mCurrentPosition % Constant.DEGREE).toFloat()
                                    binding.layoutDetailSong.customProgressBar.setProgressWithAnimation(
                                        mCurrentPosition.toFloat()
                                    )
                                }
                            }
                        }
                    }

                },
                Constant.TIME_START_UPDATE_TIME_SONG,
                Constant.TIME_UPDATE_SEEK_BAR,
                TimeUnit.MILLISECONDS
            )
        }


    }

    override fun onDestroy() {
        presenter.unRegisterLocalBroadcastActionFromService(this)
        musicService?.removeNotification()
        unbindService(serviceConnection)
        progressService.shutdown()
        super.onDestroy()
    }


    override fun onBackPressed() {
        val exitIntent = Intent(Intent.ACTION_MAIN)
        exitIntent.addCategory(Intent.CATEGORY_HOME)
        exitIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(exitIntent)
    }

    companion object {
        const val ICON_FAVORITE = R.drawable.ic_baseline_favorite_24
        const val ICON_NOT_FAVORITE = R.drawable.ic_baseline_favorite_white_24dp
        const val POSITION_FRAGMENT_HOME = 0
        const val POSITION_FRAGMENT_FAVORITE = 1
        const val POSITION_FRAGMENT_SETTING = 2
    }
}
