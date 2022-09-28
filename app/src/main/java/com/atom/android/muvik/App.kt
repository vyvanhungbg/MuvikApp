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

class App : Application() {

    override fun onCreate() {
        super.onCreate()
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
            SharedPreferencesUtils().getBooleanDefault(Constant.SHARED_PREF_LOOP_SONG)
        Constant.MIX_SONG =
            SharedPreferencesUtils().getBooleanDefault(Constant.SHARED_PREF_MIX_SONG)

        val isDarkMode = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .getBoolean(Constant.SHARED_PREF_DARK_MODE, false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        Constant.INPUT_FORMAT = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .getString(Constant.SHARED_PREF_INPUT_FORMAT, Constant.INPUT_FORMAT) ?: Constant.INPUT_FORMAT
    }

    companion object {
        const val CHANNEL_ID = "MUSIC_APP_id"
        const val CHANNEL_NAME = "MUSIC_APP"
    }
}
