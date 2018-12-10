package net.notiocide.minesweeper

const val PREFS_NAME = "net.notiocide.minesweeper:preferences"

const val KEY_BOARD = "minesweeper:state:board"

const val KEY_ROWS = "minesweeper:config:rows"
const val KEY_COLUMNS = "minesweeper:config:columns"
const val KEY_MINES = "minesweeper:config:mines"
const val KEY_SAFE = "minesweeper:config:safe"

const val EASY_ROWS = 9
const val EASY_COLUMNS = 9
const val EASY_MINES = 10

const val MEDIUM_ROWS = 16
const val MEDIUM_COLUMNS = 16
const val MEDIUM_MINES = 40

const val HARD_ROWS = 30
const val HARD_COLUMNS = 16
const val HARD_MINES = 99

const val MAX_SIZE = 50

typealias Point = Pair<Int, Int>

fun boundsCheck(rows: Int, columns: Int, row: Int, column: Int) {
    if (row !in 0 until rows) throw ArrayIndexOutOfBoundsException(row)
    if (column !in 0 until columns) throw ArrayIndexOutOfBoundsException(column)
}