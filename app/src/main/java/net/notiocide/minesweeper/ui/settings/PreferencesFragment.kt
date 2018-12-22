package net.notiocide.minesweeper.ui.settings

import android.os.Bundle
import androidx.core.content.edit
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.preference.get
import net.notiocide.minesweeper.*
import kotlin.math.min

class PreferencesFragment : PreferenceFragmentCompat() {

    private fun updateValues() {
        preferenceManager.sharedPreferences.let { prefs ->
            preferenceScreen.let {
                it["width"].summary = prefs.getInt(KEY_COLUMNS, 1).toString()
                it["height"].summary = prefs.getInt(KEY_ROWS, 1).toString()
                it["mines"].summary = prefs.getInt(KEY_MINES, 1).toString()
                (it["safe"] as SwitchPreferenceCompat).isChecked = prefs.getBoolean(KEY_SAFE, true)
                (it["doubletap"] as SwitchPreferenceCompat).isChecked = prefs.getBoolean(KEY_DOUBLETAP, true)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        updateValues()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_game, rootKey)
        preferenceManager.sharedPreferencesName = PREFS_NAME

        preferenceScreen["safe"].setOnPreferenceChangeListener { _, value ->
            preferenceManager.sharedPreferences.edit {
                putBoolean(KEY_SAFE, value as Boolean)
            }

            true
        }

        preferenceScreen["doubletap"].setOnPreferenceChangeListener { _, value ->
            preferenceManager.sharedPreferences.edit {
                putBoolean(KEY_DOUBLETAP, value as Boolean)
            }

            true
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        preferenceManager.sharedPreferences.let { prefs ->
            when (preference.key) {
                "width" -> {
                    val dialog = NumberPickerDialogFragment(
                        false,
                        R.string.dialog_column_spinner,
                        initialValue = prefs.getInt(KEY_COLUMNS, 1)
                    )
                    dialog.setListener {
                        prefs.edit {
                            putInt(KEY_COLUMNS, it)
                            val max = it * prefs.getInt(KEY_ROWS, 1)
                            putInt(KEY_MINES, min(prefs.getInt(KEY_MINES, 1), max))
                        }
                        updateValues()
                    }
                    dialog.show(fragmentManager, "spinner")
                }
                "height" -> {
                    val dialog = NumberPickerDialogFragment(
                        false,
                        R.string.dialog_row_spinner,
                        initialValue = prefs.getInt(KEY_ROWS, 1)
                    )
                    dialog.setListener {
                        prefs.edit {
                            putInt(KEY_ROWS, it)
                            val max = it * prefs.getInt(KEY_COLUMNS, 1)
                            putInt(KEY_MINES, min(prefs.getInt(KEY_MINES, 1), max))
                        }
                        updateValues()
                    }
                    dialog.show(fragmentManager, "spinner")
                }
                "mines" -> {
                    val totalSquares = prefs.getInt(KEY_ROWS, 1) * prefs.getInt(KEY_COLUMNS, 1)
                    val dialog = NumberPickerDialogFragment(
                        true,
                        R.string.dialog_mine_spinner,
                        1..totalSquares,
                        min(prefs.getInt(KEY_MINES, 1), totalSquares)
                    )
                    dialog.setListener {
                        prefs.edit { putInt(KEY_MINES, it) }
                        updateValues()
                    }
                    dialog.show(fragmentManager, "spinner")
                }
                "set_preset" -> {
                    NavHostFragment.findNavController(this)
                        .navigate(R.id.action_settingsFragment_to_presetSettingsFragment)
                }
            }
        }

        return true
    }

}