package com.atom.android.muvik.ui.setting


import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import com.atom.android.muvik.R
import com.atom.android.muvik.base.BaseFragment
import com.atom.android.muvik.databinding.FragmentSettingBinding
import com.atom.android.muvik.utils.Constant
import com.atom.android.muvik.utils.SharedPreferencesUtils


class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {

    var sharedPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPref, key ->
            if (key.equals(Constant.SHARED_PREF_DARK_MODE)) {
                val isDarkMode =
                    sharedPref.getBoolean(Constant.SHARED_PREF_DARK_MODE, false)
                if (isDarkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
            if (key.equals(Constant.SHARED_PREF_INPUT_FORMAT)) {
                Constant.INPUT_FORMAT =
                    sharedPref.getString(Constant.SHARED_PREF_INPUT_FORMAT, Constant.INPUT_FORMAT)
                        ?: Constant.INPUT_FORMAT
            }
            Log.e("TAg", Constant.INPUT_FORMAT)
        }

    override fun initData() {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(sharedPreferencesListener)

    }

    override fun initialize() {

    }

    override fun initView() {
        val childFragment: Fragment = SettingsPreferencesFragment()
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.container_setting, childFragment).commit()
    }

    override fun callData() {

    }

    override fun initEvent() {

    }

    override fun onStop() {
        super.onStop()
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener)
    }

}
