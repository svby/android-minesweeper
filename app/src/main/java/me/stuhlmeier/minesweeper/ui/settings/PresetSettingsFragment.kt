package me.stuhlmeier.minesweeper.ui.settings

import android.os.Bundle
import androidx.core.content.edit
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.get
import me.stuhlmeier.minesweeper.PREFS_NAME
import me.stuhlmeier.minesweeper.R
import me.stuhlmeier.minesweeper.game.Preset
import me.stuhlmeier.minesweeper.putPreset

class PresetSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_presets, rootKey)
        preferenceManager.sharedPreferencesName = PREFS_NAME

        preferenceScreen.let {
            val formatString = getString(R.string.summary_set_preset_individual)
            it.get<Preference>("preset_easy")!!.summary =
                formatString.format(Preset.EASY.rows, Preset.EASY.columns, Preset.EASY.mines)
            it.get<Preference>("preset_medium")!!.summary =
                formatString.format(Preset.MEDIUM.rows, Preset.MEDIUM.columns, Preset.MEDIUM.mines)
            it.get<Preference>("preset_hard")!!.summary =
                formatString.format(Preset.HARD.rows, Preset.HARD.columns, Preset.HARD.mines)
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        preferenceManager.sharedPreferences.let { prefs ->
            when (preference.key) {
                "preset_easy" -> prefs.edit { putPreset(Preset.EASY) }
                "preset_medium" -> prefs.edit { putPreset(Preset.MEDIUM) }
                "preset_hard" -> prefs.edit { putPreset(Preset.HARD) }
            }
            NavHostFragment.findNavController(this).navigateUp()
        }

        return true
    }
}
