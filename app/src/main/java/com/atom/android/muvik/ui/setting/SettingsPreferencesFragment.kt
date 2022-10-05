package com.atom.android.muvik.ui.setting


import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.atom.android.muvik.R
import com.atom.android.muvik.data.model.Song
import com.atom.android.muvik.data.repository.SongRepository
import com.atom.android.muvik.data.source.local.SongLocalDataSource
import com.atom.android.muvik.utils.Constant
import com.atom.android.muvik.utils.extension.toast
import com.atom.android.muvik.utils.sqlite.SQLiteUtils
import com.atom.android.muvik.utils.sqlite.SongDBHelper


class SettingsPreferencesFragment : PreferenceFragmentCompat(), SettingContract.View {

    private val presenter by lazy {
        SettingFragmentPresenter.getInstance(
            SongRepository.getInstance(
                SongLocalDataSource.getInstance(
                    SongDBHelper.getInstance(
                        SQLiteUtils.getInstance(requireActivity().applicationContext)
                    )
                )
            ),
            this
        )
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_shared_prefer, rootKey);

        val keyRestoreSetting = findPreference<Preference>(Constant.SHARED_PREF_RESET_SETTING)
        val keyGetLocalSong = findPreference<Preference>(Constant.SHARED_PREF_GET_LOCAL_SONG)
        keyRestoreSetting?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            showDialogConfirmRestoreSetting()
            true
        }

        keyGetLocalSong?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            presenter.getSongs(context)
            true
        }
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

    override fun getSongLocalSuccess(list: MutableList<Song>) {
        context?.toast(
            R.string.text_get_local_success
        )
    }

    override fun getSongLocalFail(message: String) {
        context?.toast(R.string.text_get_local_failed)
    }
}
