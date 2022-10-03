package com.atom.android.muvik.ui.main

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.atom.android.muvik.R
import com.atom.android.muvik.data.model.Song
import com.atom.android.muvik.utils.Constant
import com.atom.android.muvik.utils.extension.createNotificationSong
import kotlin.random.Random


class MusicService : Service() {

    var mediaPlayer: MediaPlayer? = MediaPlayer()
    private var songs = mutableListOf<Song>()
    private val mBinder = MusicBinder()
    private var currentSongPosition = 0
    private var currentSong: Song? = null
    private var notificationManager: NotificationManagerCompat? = null


    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initData(intent)
        pushNotification()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = mBinder

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.let {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    private fun pushNotification() {
        val notification = createNotificationSong(
            applicationContext,
            currentSong,
            mediaPlayer?.isPlaying
        )

        notificationManager.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForeground(
                    FOREGROUND_NOTIFICATION_ID,
                    notification
                )
            } else {
                this?.notify(
                    FOREGROUND_NOTIFICATION_ID,
                    notification
                )
            }
        }
    }

    private fun initData(intent: Intent?) {
        val DEFAULT_POSITON = 0
        intent?.let {
            if (it.hasExtra(Constant.EXTRA_POSITION_SONG) && it.hasExtra(Constant.EXTRA_LIST_SONG)) {
                currentSongPosition = it.getIntExtra(Constant.EXTRA_POSITION_SONG, DEFAULT_POSITON)
                songs =
                    it.getParcelableArrayListExtra<Song>(Constant.EXTRA_LIST_SONG) as ArrayList<Song>
                playSong(currentSongPosition)
            }
            if (it.hasExtra(Constant.EXTRA_ACTION_FLAG)) {
                val actionFlag = it.getStringExtra(Constant.EXTRA_ACTION_FLAG)
                //handleActionMusic(actionFlag)
            }
        }

    }


    private fun playSong(position: Int) {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            reset()
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            currentSongPosition = position
            mixSong()
            currentSong = songs[currentSongPosition]
            val url = currentSong?.url

            url?.let { setDataSource(it) }
            prepare()
            start()
            handleActionMusic(Constant.ACTION_PLAY)
            setOnCompletionListener {
                nextSong()
            }

            pushNotification()
        }
    }

    private fun pauseSong() {
        mediaPlayer?.apply {
            if (isPlaying) {
                pause()
            }
        }
        pushNotification()
    }

    private fun nextSong() {
        currentSongPosition = (currentSongPosition + 1) % (songs.size)
        playSong(currentSongPosition)
    }

    private fun mixSong() {
        if (Constant.MIX_SONG) {
            val FIRST_SONG = 0
            currentSongPosition = Random.nextInt(FIRST_SONG, songs.size - 1)
        }
    }

    private fun previousSong() {
        currentSongPosition =
            if (currentSongPosition > 0) currentSongPosition - 1 else songs.size - 1
        playSong(currentSongPosition)
    }

    private fun resumeSong() {
        mediaPlayer?.currentPosition?.let {
            mediaPlayer?.seekTo(it)
            mediaPlayer?.start()
        }
        pushNotification()
    }


    fun seekToPosition(position: Int?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            position?.let {
                mediaPlayer?.seekTo(it.toLong() * Constant.SECOND_TO_MIL, MediaPlayer.SEEK_CLOSEST)
            }
        } else {
            position?.let { mediaPlayer?.seekTo(it * Constant.SECOND_TO_MIL.toInt()) }
        }
    }

    fun getDuration() = mediaPlayer?.duration

    fun handleActionMusic(actionFlag: String?) {

        when (actionFlag) {
            Constant.ACTION_PREVIOUS -> previousSong()
            Constant.ACTION_NEXT -> nextSong()
            Constant.ACTION_RESUME -> resumeSong()
            Constant.ACTION_PAUSE -> pauseSong()

        }
        sendActionToActivity(actionFlag)
    }


    private fun sendActionToActivity(actionFlag: String?) {
        val intent = Intent(Constant.INTENT_FILTER_ACTION_SERVICE_TO_ACTIVITY_MUSIC)
        intent.putExtra(Constant.EXTRA_ACTION_FLAG, actionFlag)
        intent.putExtra(Constant.EXTRA_CURRENT_SONG, currentSong)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
        fun getMediaPlayer(): MediaPlayer? = mediaPlayer
    }

    fun getSongPlaying() = currentSong
    fun setSongPlaying(isFavorite: Boolean) {
        currentSong = currentSong?.copy(favorite = isFavorite)
    }

    fun removeNotification() {
        notificationManager?.cancelAll()
    }

    companion object {
        private const val FOREGROUND_NOTIFICATION_ID = 1
    }
}
