package com.atom.android.muvik.ui.setting


import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.atom.android.muvik.R
import com.atom.android.muvik.utils.Constant


class SettingsPreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_shared_prefer, rootKey);
        val keyRestoreSetting = findPreference<Preference>(Constant.SHARED_PREF_RESET_SETTING)
        keyRestoreSetting?.setOnPreferenceClickListener(object :
            Preference.OnPreferenceClickListener {
            override fun onPreferenceClick(preference: Preference): Boolean {
                showDialogConfirmRestoreSetting()
                return true
            }

        })
    }

    private fun showDialogConfirmRestoreSetting() {
        AlertDialog.Builder(requireContext()).apply {
            setIcon(android.R.drawable.ic_dialog_alert)
            setTitle(getString(R.string.preferred_reset_setting))
            setMessage(getString(R.string.summary_restore_string))
            setPositiveButton(getString(R.string.yes), object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    restoreDefaultSettings()
                }

            })
            setNegativeButton(getString(R.string.cancel), null)
            show();
        }

    }

    private fun restoreDefaultSettings() {
        val preferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
        PreferenceManager.setDefaultValues(requireContext(), R.xml.setting_shared_prefer, true)
        preferenceScreen.removeAll()
        onCreatePreferences(null, null) //or onCreate(null) in your code
    }
}
