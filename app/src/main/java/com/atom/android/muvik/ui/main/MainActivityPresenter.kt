package com.atom.android.muvik.ui.main

import android.content.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.atom.android.muvik.data.IResultListener
import com.atom.android.muvik.data.model.Song
import com.atom.android.muvik.data.repository.SongRepository
import com.atom.android.muvik.utils.Constant
import com.atom.android.muvik.utils.SharedPreferencesUtils

class MainActivityPresenter(
    private val repository: SongRepository,
    private val mainView: MainContract.View
) : MainContract.Presenter {

    private val mMessageReceiverActionFromService: BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val actionFlag = intent.getStringExtra(Constant.EXTRA_ACTION_FLAG)
                val song: Song? = intent.getParcelableExtra<Song>(Constant.EXTRA_CURRENT_SONG)
                mainView.handlerActionFromService(actionFlag, song)
            }
        }

    override fun registerActionFromService(context: Context) {
        LocalBroadcastManager.getInstance(context).registerReceiver(
            mMessageReceiverActionFromService,
            IntentFilter(Constant.INTENT_FILTER_ACTION_SERVICE_TO_ACTIVITY_MUSIC)
        );
    }

    override fun calcTimeOfSong(mCurrentPosition: Int, totalTime: Int) {
        var elapsedTime: String?
        val minElapsed: Int = mCurrentPosition / Constant.HOUR_TO_SECOND
        val secElapsed: Int = mCurrentPosition % Constant.HOUR_TO_SECOND
        elapsedTime = "$minElapsed:"
        if (secElapsed < 10) {
            elapsedTime += "0"
        }
        elapsedTime += secElapsed

        var remainingTime: String?
        val minRemaining: Int = (totalTime - mCurrentPosition) / Constant.HOUR_TO_SECOND
        val secRemaining: Int = (totalTime - mCurrentPosition) % Constant.HOUR_TO_SECOND
        remainingTime = "$minRemaining:"
        if (secRemaining < 10) {
            remainingTime += "0"
        }
        remainingTime += secRemaining
        mainView.updateTimeSong(elapsedTime, remainingTime)
    }


    override fun unRegisterLocalBroadcastActionFromService(context: Context) {
        LocalBroadcastManager.getInstance(context)
            .unregisterReceiver(mMessageReceiverActionFromService)
    }

    override fun updateFavoriteSong(context: Context, id: String, isFavorite: Boolean) {
        repository.updateFavoriteSong(context, id, isFavorite,
            object : IResultListener<Boolean> {
                override fun onSuccess(list: Boolean) {
                    mainView.updateFavoriteSongSuccess(list)
                }

                override fun onFail(message: String) {
                    mainView.updateFavoriteSongFailed(message)
                }

            }
        )
    }

    fun saveLoopSetting(context: Context) {
        SharedPreferencesUtils.getInstance(context)
        Constant.LOOP_SONG = !Constant.LOOP_SONG
        SharedPreferencesUtils().putBoolean(Constant.SHARED_PREF_LOOP_SONG, Constant.LOOP_SONG)
        mainView.updateLoopSetting()
    }

    fun saveMixSongSetting(context: Context) {
        SharedPreferencesUtils.getInstance(context)
        Constant.MIX_SONG = !Constant.MIX_SONG
        SharedPreferencesUtils().putBoolean(Constant.SHARED_PREF_MIX_SONG, Constant.MIX_SONG)
        mainView.updateMixSongSetting()
    }

    override fun onStart() {
        // late impl
    }

    override fun onStop() {
        //late impl
    }

    companion object {
        private var instance: MainActivityPresenter? = null
        fun getInstance(repository: SongRepository, mainView: MainContract.View) =
            synchronized(this) {
                instance ?: MainActivityPresenter(repository, mainView).also { instance = it }
            }
    }

}
