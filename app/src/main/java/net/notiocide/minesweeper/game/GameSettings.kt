package net.notiocide.minesweeper.game

import android.content.SharedPreferences
import androidx.core.content.edit
import net.notiocide.minesweeper.*

data class GameSettings(
    val rows: Int,
    val columns: Int,
    val mines: Int,
    val safe: Boolean
) {

    companion object {

        @JvmStatic
        fun ensure(prefs: SharedPreferences) {
            val rows = prefs.getInt(KEY_ROWS, -1)
            val columns = prefs.getInt(KEY_COLUMNS, -1)
            val mines = prefs.getInt(KEY_MINES, -1)

            if (rows == -1 || columns == -1 || mines == -1) {
                prefs.edit {
                    putInt(KEY_ROWS, EASY_ROWS)
                    putInt(KEY_COLUMNS, EASY_COLUMNS)
                    putInt(KEY_MINES, EASY_MINES)
                }
            }
        }

        @JvmStatic
        fun load(prefs: SharedPreferences): GameSettings {
            ensure(prefs)

            return GameSettings(
                prefs.getInt(KEY_ROWS, EASY_ROWS),
                prefs.getInt(KEY_COLUMNS, EASY_COLUMNS),
                prefs.getInt(KEY_MINES, EASY_MINES),
                prefs.getBoolean(KEY_SAFE, true)
            )
        }

    }

}