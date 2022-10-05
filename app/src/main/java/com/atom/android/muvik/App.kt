package com.atom.android.muvik

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.atom.android.muvik.utils.Constant
import com.atom.android.muvik.utils.SharedPreferencesUtils
import com.facebook.stetho.Stetho

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        createChannelNotification()
        loadSetting()
    }

    private fun createChannelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.setSound(null, null)
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }

    private fun loadSetting() {
        SharedPreferencesUtils.getInstance(applicationContext)
        Constant.LOOP_SONG =
            SharedPreferencesUtils().getSettingLoopSong()
        Constant.MIX_SONG =
            SharedPreferencesUtils().getSettingMixSong()
        Constant.INPUT_FORMAT = SharedPreferencesUtils().getInputFormatSongs(applicationContext)

        val isFirstLaunchApp = SharedPreferencesUtils().getFistLaunchingApp()
        if (!isFirstLaunchApp) {
            SharedPreferencesUtils().setFistLaunchingApp()
            Constant.FIRST_LAUNCH_APP = true
        }
    }

    companion object {
        const val CHANNEL_ID = "MUSIC_APP_id"
        const val CHANNEL_NAME = "MUSIC_APP"
    }
}
