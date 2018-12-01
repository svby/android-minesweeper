package at.spengergasse.minesweeper.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Switch
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import at.spengergasse.minesweeper.*
import at.spengergasse.minesweeper.game.GameSettings
import kotlinx.android.synthetic.main.settings_fragment.*
import java.math.BigInteger

class SettingsFragment : Fragment() {

    private val MAX = BigInteger.valueOf(Integer.MAX_VALUE.toLong())

    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.settings_fragment, container, false)


        val difficultyGroup = view.findViewById<RadioGroup>(R.id.group_difficulty)
        val minesField = view.findViewById<EditText>(R.id.text_mines)
        val rowsField = view.findViewById<EditText>(R.id.text_height)
        val columnsField = view.findViewById<EditText>(R.id.text_width)
        val safeSwitch = view.findViewById<Switch>(R.id.switch_safe)

        difficultyGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_easy -> {
                    minesField.isEnabled = false
                    rowsField.isEnabled = false
                    columnsField.isEnabled = false

                    columnsField.setText(EASY_COLUMNS.toString())
                    rowsField.setText(EASY_ROWS.toString())
                    minesField.setText(EASY_MINES.toString())

                    prefs.edit {
                        putInt(KEY_PRESET, PRESET_EASY)
                        remove(KEY_ROWS)
                        remove(KEY_COLUMNS)
                        remove(KEY_MINES)
                    }
                }
                R.id.radio_medium -> {
                    minesField.isEnabled = false
                    rowsField.isEnabled = false
                    columnsField.isEnabled = false

                    columnsField.setText(MEDIUM_COLUMNS.toString())
                    rowsField.setText(MEDIUM_ROWS.toString())
                    minesField.setText(MEDIUM_MINES.toString())

                    prefs.edit {
                        putInt(KEY_PRESET, PRESET_MEDIUM)
                        remove(KEY_ROWS)
                        remove(KEY_COLUMNS)
                        remove(KEY_MINES)
                    }
                }
                R.id.radio_hard -> {
                    minesField.isEnabled = false
                    rowsField.isEnabled = false
                    columnsField.isEnabled = false

                    columnsField.setText(HARD_COLUMNS.toString())
                    rowsField.setText(HARD_ROWS.toString())
                    minesField.setText(HARD_MINES.toString())

                    prefs.edit {
                        putInt(KEY_PRESET, PRESET_HARD)
                        remove(KEY_ROWS)
                        remove(KEY_COLUMNS)
                        remove(KEY_MINES)
                    }
                }
                R.id.radio_custom -> {
                    minesField.isEnabled = true
                    rowsField.isEnabled = true
                    columnsField.isEnabled = true
                }
            }
        }

        when (prefs.getInt(KEY_PRESET, -1)) {
            PRESET_EASY -> difficultyGroup.check(R.id.radio_easy)
            PRESET_MEDIUM -> difficultyGroup.check(R.id.radio_medium)
            PRESET_HARD -> difficultyGroup.check(R.id.radio_hard)
            else -> difficultyGroup.check(R.id.radio_custom)
        }

        val loaded = GameSettings.load(prefs)
        minesField.setText(loaded.mines.toString())
        rowsField.setText(loaded.rows.toString())
        columnsField.setText(loaded.columns.toString())
        safeSwitch.isChecked = loaded.safe

        return view
    }

    override fun onPause() {
        super.onPause()

        prefs.edit {
            text_mines.text.toString().let { if (it.isNotEmpty()) putInt(KEY_MINES, it.toInt()) }
            text_height.text.toString().let { if (it.isNotEmpty()) putInt(KEY_ROWS, it.toInt()) }
            text_width.text.toString().let { if (it.isNotEmpty()) putInt(KEY_COLUMNS, it.toInt()) }
            putBoolean(KEY_SAFE, switch_safe.isChecked)
        }
    }

}