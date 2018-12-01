package at.spengergasse.minesweeper.game

import android.content.SharedPreferences
import androidx.core.content.edit
import at.spengergasse.minesweeper.*

data class GameSettings(
    val rows: Int,
    val columns: Int,
    val mines: Int,
    val safe: Boolean
) {

    companion object {

        @JvmStatic
        fun ensure(prefs: SharedPreferences) {
            val preset = prefs.getInt(KEY_PRESET, -1)
            if (preset != -1) return

            val rows = prefs.getInt(KEY_ROWS, -1)
            val columns = prefs.getInt(KEY_COLUMNS, -1)
            val mines = prefs.getInt(KEY_MINES, -1)

            if (rows == -1 || columns == -1 || mines == -1) {
                prefs.edit {
                    putInt(KEY_PRESET, PRESET_EASY)
                }
            }
        }

        @JvmStatic
        fun load(prefs: SharedPreferences): GameSettings {
            ensure(prefs)

            val safe = prefs.getBoolean(KEY_SAFE, true)
            return when (prefs.getInt(KEY_PRESET, -1)) {
                PRESET_EASY -> GameSettings(EASY_ROWS, EASY_COLUMNS, EASY_MINES, safe)
                PRESET_MEDIUM -> GameSettings(MEDIUM_ROWS, MEDIUM_COLUMNS, MEDIUM_MINES, safe)
                PRESET_HARD -> GameSettings(HARD_ROWS, HARD_COLUMNS, HARD_MINES, safe)
                else -> GameSettings(
                    prefs.getInt(KEY_ROWS, 1),
                    prefs.getInt(KEY_COLUMNS, 1),
                    prefs.getInt(KEY_MINES, 1),
                    safe
                )
            }
        }

    }

}