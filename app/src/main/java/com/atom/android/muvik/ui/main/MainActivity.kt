package com.atom.android.muvik.ui.main

import android.content.*
import android.os.Handler
import android.os.IBinder
import android.view.View
import android.widget.SeekBar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.atom.android.muvik.R
import com.atom.android.muvik.base.BaseActivity
import com.atom.android.muvik.data.model.Song
import com.atom.android.muvik.databinding.ActivityMainBinding
import com.atom.android.muvik.utils.Constant
import com.atom.android.muvik.utils.extension.setImage
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        binding.navView.setupWithNavController(navController)
        setBottomSheetState(navController)
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

    private fun setBottomSheetState(navController: NavController) {
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

        // set state  state bottom sheet with of navigation bottom
        binding.navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_setting -> {
                    setBottomSheetVisibility(BottomSheetBehavior.STATE_HIDDEN)
                    NavigationUI.onNavDestinationSelected(item, navController)
                }
                else -> {
                    if (musicService == null && isPlaying == false) {
                        setBottomSheetVisibility(BottomSheetBehavior.STATE_HIDDEN)
                    } else {
                        setBottomSheetVisibility(BottomSheetBehavior.STATE_COLLAPSED)
                    }
                    NavigationUI.onNavDestinationSelected(item, navController)
                }
            }


        }


    }


    private fun setSongInfo(song: Song) {
        song.setImage(binding.imageSongSub)
        binding.textNameSongSub.text = song.name
        binding.textNameSingerSub.text = song.artist
        song.setImage(binding.layoutDetailSong.imageViewSongMain)
        binding.layoutDetailSong.textViewNameSongMain.text = song.name
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
        super.onDestroy()
        presenter.unRegisterLocalBroadcastActionFromService(this)
        unbindService(serviceConnection)
        progressService.shutdown()
    }

}
