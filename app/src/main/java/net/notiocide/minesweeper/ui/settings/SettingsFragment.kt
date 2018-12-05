package net.notiocide.minesweeper.ui.settings

import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_settings.*
import net.notiocide.minesweeper.*
import net.notiocide.minesweeper.game.GameSettings
import java.math.BigInteger

class SettingsFragment : Fragment() {

    companion object {
        private val MAX = BigInteger.valueOf(MAX_SIZE.toLong())
    }

    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        group_difficulty.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_custom -> layout_settings.childrenRecursive.forEach { it.isEnabled = true }
                else -> {
                    layout_settings.childrenRecursive.forEach { it.isEnabled = false }
                    when (checkedId) {
                        R.id.radio_easy -> {
                            text_width.setText(EASY_COLUMNS.toString())
                            text_height.setText(EASY_ROWS.toString())
                            text_mines.setText(EASY_MINES.toString())
                        }
                        R.id.radio_medium -> {
                            text_width.setText(MEDIUM_COLUMNS.toString())
                            text_height.setText(MEDIUM_ROWS.toString())
                            text_mines.setText(MEDIUM_MINES.toString())
                        }
                        R.id.radio_hard -> {
                            text_width.setText(HARD_COLUMNS.toString())
                            text_height.setText(HARD_ROWS.toString())
                            text_mines.setText(HARD_MINES.toString())
                        }
                    }
                }
            }
        }

        when (prefs.getInt(KEY_PRESET, -1)) {
            PRESET_EASY -> group_difficulty.check(R.id.radio_easy)
            PRESET_MEDIUM -> group_difficulty.check(R.id.radio_medium)
            PRESET_HARD -> group_difficulty.check(R.id.radio_hard)
            else -> group_difficulty.check(R.id.radio_custom)
        }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable) {
                if (s.toString() == "0") s.replace(0, 1, "")
                else if (s.isNotEmpty()) {
                    val bigInteger = s.toString().toBigInteger()
                    if (bigInteger > MAX) {
                        for (length in s.length downTo 1) {
                            val cut = s.toString().substring(0, length)
                            if (cut.toBigInteger() <= MAX) {
                                s.replace(0, s.length, cut)
                                break
                            }
                        }
                    }
                }
                button_save.isEnabled = !s.toString().isBlank()
            }
        }

        text_mines.addTextChangedListener(watcher)
        text_width.addTextChangedListener(watcher)
        text_height.addTextChangedListener(watcher)

        val loaded = GameSettings.load(prefs)

        text_mines.setText(loaded.mines.toString())
        text_height.setText(loaded.rows.toString())
        text_width.setText(loaded.columns.toString())
        switch_safe.isChecked = loaded.safe

        switch_safe.setOnCheckedChangeListener { _, isChecked -> prefs.edit { putBoolean(KEY_SAFE, isChecked) } }

        button_save.setOnClickListener {
            val checked = group_difficulty.checkedRadioButtonId
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
                        text_height.text.toString().toIntOrNull()?.let { putInt(KEY_ROWS, it) }
                        text_width.text.toString().toIntOrNull()?.let { putInt(KEY_COLUMNS, it) }
                        text_mines.text.toString().toIntOrNull()?.let { putInt(KEY_MINES, it) }
                    }
                }
            }
        }
    }

}