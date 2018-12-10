package net.notiocide.minesweeper

import android.content.SharedPreferences
import net.notiocide.minesweeper.game.Preset

const val PREFS_NAME = "net.notiocide.minesweeper:preferences"

const val KEY_BOARD = "minesweeper:state:board"

const val KEY_ROWS = "minesweeper:config:rows"
const val KEY_COLUMNS = "minesweeper:config:columns"
const val KEY_MINES = "minesweeper:config:mines"
const val KEY_SAFE = "minesweeper:config:safe"

const val MAX_SIZE = 50

typealias Point = Pair<Int, Int>

fun boundsCheck(rows: Int, columns: Int, row: Int, column: Int) {
    if (row !in 0 until rows) throw ArrayIndexOutOfBoundsException(row)
    if (column !in 0 until columns) throw ArrayIndexOutOfBoundsException(column)
}

fun SharedPreferences.Editor.putPreset(preset: Preset) {
    putInt(KEY_ROWS, preset.rows)
    putInt(KEY_COLUMNS, preset.columns)
    putInt(KEY_MINES, preset.mines)
}