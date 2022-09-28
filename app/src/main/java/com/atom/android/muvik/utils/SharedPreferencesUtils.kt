package com.atom.android.muvik.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences


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

    companion object {
        private var instance: SharedPreferences? = null
        fun getInstance(context: Context) =
            synchronized(this) {
                instance ?: context.getSharedPreferences(Constant.SHARED_PREF, MODE_PRIVATE)
                    .also { instance = it }
            }
    }
}
