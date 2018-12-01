package at.spengergasse.minesweeper.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Switch
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import at.spengergasse.minesweeper.*
import at.spengergasse.minesweeper.game.GameSettings
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
        val saveButton = view.findViewById<Button>(R.id.button_save)

        difficultyGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_custom -> {
                    minesField.isEnabled = true
                    rowsField.isEnabled = true
                    columnsField.isEnabled = true
                }
                else -> {
                    minesField.isEnabled = false
                    rowsField.isEnabled = false
                    columnsField.isEnabled = false
                    when (checkedId) {
                        R.id.radio_easy -> {
                            columnsField.setText(EASY_COLUMNS.toString())
                            rowsField.setText(EASY_ROWS.toString())
                            minesField.setText(EASY_MINES.toString())
                        }
                        R.id.radio_medium -> {
                            columnsField.setText(MEDIUM_COLUMNS.toString())
                            rowsField.setText(MEDIUM_ROWS.toString())
                            minesField.setText(MEDIUM_MINES.toString())
                        }
                        R.id.radio_hard -> {
                            columnsField.setText(HARD_COLUMNS.toString())
                            rowsField.setText(HARD_ROWS.toString())
                            minesField.setText(HARD_MINES.toString())
                        }
                    }
                }
            }
        }

        when (prefs.getInt(KEY_PRESET, -1)) {
            PRESET_EASY -> difficultyGroup.check(R.id.radio_easy)
            PRESET_MEDIUM -> difficultyGroup.check(R.id.radio_medium)
            PRESET_HARD -> difficultyGroup.check(R.id.radio_hard)
            else -> difficultyGroup.check(R.id.radio_custom)
        }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable) {
                if (s.toString() == "0") s.replace(0, 1, "")
                else if (s.isNotEmpty()) {
                    val bigInteger = s.toString().toBigInteger()
                    if (bigInteger > MAX) {
                        for (length in 10 downTo 9) {
                            val cut = s.toString().substring(0, length)
                            if (cut.toBigInteger() <= MAX) {
                                s.replace(0, s.length, cut)
                                break
                            }
                        }
                    }
                }
                saveButton.isEnabled = !s.toString().isBlank()
            }
        }

        minesField.addTextChangedListener(watcher)
        rowsField.addTextChangedListener(watcher)
        columnsField.addTextChangedListener(watcher)

        val loaded = GameSettings.load(prefs)
        minesField.setText(loaded.mines.toString())
        rowsField.setText(loaded.rows.toString())
        columnsField.setText(loaded.columns.toString())
        safeSwitch.isChecked = loaded.safe

        safeSwitch.setOnCheckedChangeListener { _, isChecked -> prefs.edit { putBoolean(KEY_SAFE, isChecked) } }

        saveButton.setOnClickListener {
            val checked = difficultyGroup.checkedRadioButtonId
            prefs.edit {
                remove(KEY_MINES)
                remove(KEY_ROWS)
                remove(KEY_COLUMNS)
                remove(KEY_PRESET)

                when (checked) {
                    R.id.radio_easy -> putInt(KEY_PRESET, PRESET_EASY)
                    R.id.radio_medium -> putInt(KEY_PRESET, PRESET_MEDIUM)
                    R.id.radio_hard -> putInt(KEY_PRESET, PRESET_HARD)
                    R.id.radio_custom -> {
                        rowsField.text.toString().toIntOrNull()?.let { putInt(KEY_ROWS, it) }
                        columnsField.text.toString().toIntOrNull()?.let { putInt(KEY_COLUMNS, it) }
                        minesField.text.toString().toIntOrNull()?.let { putInt(KEY_MINES, it) }
                    }
                }
            }
        }

        return view
    }

}