package me.stuhlmeier.minesweeper

import android.content.SharedPreferences
import me.stuhlmeier.minesweeper.game.Board
import me.stuhlmeier.minesweeper.game.Preset
import kotlin.math.ceil

const val PREFS_NAME = "me.stuhlmeier.minesweeper:preferences"

const val KEY_BOARD = "minesweeper:state:board"

const val KEY_ROWS = "minesweeper:config:rows"
const val KEY_COLUMNS = "minesweeper:config:columns"
const val KEY_MINES = "minesweeper:config:mines"
const val KEY_SAFE = "minesweeper:config:safe"
const val KEY_CHORD = "minesweeper:config:chord"
const val KEY_INVERT = "minesweeper:config:invert"

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

fun Float.roundUp() = ceil(this).toInt()

inline fun Board.forEachEightNeighbor(
    row: Int,
    column: Int,
    action: (row: Int, column: Int) -> Unit
) {
    if (row + 1 in 0 until rows && column in 0 until columns) action(row + 1, column)
    if (row - 1 in 0 until rows && column in 0 until columns) action(row - 1, column)
    if (row in 0 until rows && column + 1 in 0 until columns) action(row, column + 1)
    if (row in 0 until rows && column - 1 in 0 until columns) action(row, column - 1)
    if (row + 1 in 0 until rows && column + 1 in 0 until columns) action(row + 1, column + 1)
    if (row - 1 in 0 until rows && column + 1 in 0 until columns) action(row - 1, column + 1)
    if (row + 1 in 0 until rows && column - 1 in 0 until columns) action(row + 1, column - 1)
    if (row - 1 in 0 until rows && column - 1 in 0 until columns) action(row - 1, column - 1)
}

inline fun Board.forEachFourNeighbor(
    row: Int,
    column: Int,
    action: (row: Int, column: Int) -> Unit
) {
    if (row + 1 in 0 until rows && column in 0 until column) action(row + 1, column)
    if (row - 1 in 0 until rows && column in 0 until column) action(row - 1, column)
    if (row in 0 until rows && column + 1 in 0 until column) action(row, column + 1)
    if (row in 0 until rows && column - 1 in 0 until column) action(row, column - 1)
}

fun Board.eightNeighbors(row: Int, column: Int) = sequence {
    forEachEightNeighbor(row, column) { row, column -> yield(Point(row, column)) }
}

fun Board.fourNeighbors(row: Int, column: Int) = sequence {
    forEachFourNeighbor(row, column) { row, column -> yield(Point(row, column)) }
}
