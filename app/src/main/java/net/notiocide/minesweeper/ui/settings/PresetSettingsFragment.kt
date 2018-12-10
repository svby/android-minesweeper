package net.notiocide.minesweeper.ui.settings

import android.os.Bundle
import androidx.core.content.edit
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.get
import net.notiocide.minesweeper.*

class PresetSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_presets, rootKey)
        preferenceManager.sharedPreferencesName = PREFS_NAME

        preferenceScreen.let {
            it["preset_easy"].summary = getString(R.string.preset_summary).format(EASY_ROWS, EASY_COLUMNS, EASY_MINES)
            it["preset_medium"].summary =
                    getString(R.string.preset_summary).format(MEDIUM_ROWS, MEDIUM_COLUMNS, MEDIUM_MINES)
            it["preset_hard"].summary = getString(R.string.preset_summary).format(HARD_ROWS, HARD_COLUMNS, HARD_MINES)
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        preferenceManager.sharedPreferences.let { prefs ->
            when (preference.key) {
                "preset_easy" -> {
                    prefs.edit {
                        putInt(KEY_ROWS, EASY_ROWS)
                        putInt(KEY_COLUMNS, EASY_COLUMNS)
                        putInt(KEY_MINES, EASY_MINES)
                    }
                }
                "preset_medium" -> {
                    prefs.edit {
                        putInt(KEY_ROWS, MEDIUM_ROWS)
                        putInt(KEY_COLUMNS, MEDIUM_COLUMNS)
                        putInt(KEY_MINES, MEDIUM_MINES)
                    }
                }
                "preset_hard" -> {
                    prefs.edit {
                        putInt(KEY_ROWS, HARD_ROWS)
                        putInt(KEY_COLUMNS, HARD_COLUMNS)
                        putInt(KEY_MINES, HARD_MINES)
                    }
                }
            }
            NavHostFragment.findNavController(this).navigateUp()
        }

        return true
    }

}