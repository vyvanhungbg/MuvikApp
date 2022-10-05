package com.atom.android.muvik.utils

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.preference.PreferenceManager


class SharedPreferencesUtils {

    fun putString(key: String?, value: String?) {
        instance?.edit()?.let {
            it.putString(key, value)
            it.apply()
        }
    }

    fun getString(key: String?): String? {
        return instance?.getString(key, "")
    }

    fun putBoolean(key: String?, value: Boolean) {
        instance?.edit()?.let {
            it.putBoolean(key, value)
            it.apply()
        }
    }

    fun getBooleanDefault(key: String?): Boolean {
        return instance?.getBoolean(key, false) ?: false
    }

    fun remove(key: String?) {
        instance?.edit()?.let {
            it.remove(key).commit()
        }
    }

    fun clearPreferences() {
        instance?.edit()?.let {
            it.clear()
            it.apply()
        }

    }

    fun getFistLaunchingApp(): Boolean {
        return getBooleanDefault(Constant.SHARED_PREF_FIRST_LAUNCH_APP)
    }

    fun setFistLaunchingApp() {
        putBoolean(Constant.SHARED_PREF_FIRST_LAUNCH_APP, true)
    }

    fun getInputFormatSongs(application: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(application)
            .getString(Constant.SHARED_PREF_INPUT_FORMAT, Constant.INPUT_FORMAT)
            ?: Constant.INPUT_FORMAT
    }

    fun getSettingLoopSong(): Boolean {
        return getBooleanDefault(Constant.SHARED_PREF_LOOP_SONG)
    }

    fun getSettingMixSong(): Boolean {
        return getBooleanDefault(Constant.SHARED_PREF_MIX_SONG)
    }

    companion object {
        private var instance: SharedPreferences? = null
        fun getInstance(context: Context) =
            synchronized(this) {
                instance ?: context.getSharedPreferences(Constant.SHARED_PREF, MODE_PRIVATE)
                    .also { instance = it }
            }
    }
}
