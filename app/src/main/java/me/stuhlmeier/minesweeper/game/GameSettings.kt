package me.stuhlmeier.minesweeper.game

import android.content.SharedPreferences
import androidx.core.content.edit
import me.stuhlmeier.minesweeper.*

data class GameSettings(
    val rows: Int,
    val columns: Int,
    val mines: Int,
    val safe: Boolean,
    val chordEnabled: Boolean
) {
    companion object {
        @JvmStatic
        fun ensure(prefs: SharedPreferences) {
            val rows = prefs.getInt(KEY_ROWS, -1)
            val columns = prefs.getInt(KEY_COLUMNS, -1)
            val mines = prefs.getInt(KEY_MINES, -1)

            if (rows == -1 || columns == -1 || mines == -1) {
                prefs.edit {
                    putPreset(Preset.EASY)
                }
            }
        }

        @JvmStatic
        fun load(prefs: SharedPreferences): GameSettings {
            ensure(prefs)

            return GameSettings(
                prefs.getInt(KEY_ROWS, Preset.EASY.rows),
                prefs.getInt(KEY_COLUMNS, Preset.EASY.columns),
                prefs.getInt(KEY_MINES, Preset.EASY.mines),
                prefs.getBoolean(KEY_SAFE, true),
                prefs.getBoolean(KEY_CHORD, true)
            )
        }
    }
}
