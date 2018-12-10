package net.notiocide.minesweeper

import android.view.View
import android.view.ViewGroup
import java.math.BigInteger

const val PREFS_NAME = "net.notiocide.minesweeper:preferences"

const val KEY_BOARD = "minesweeper:state:board"

const val KEY_PRESET = "minesweeper:config:preset"
const val KEY_ROWS = "minesweeper:config:rows"
const val KEY_COLUMNS = "minesweeper:config:columns"
const val KEY_MINES = "minesweeper:config:mines"
const val KEY_SAFE = "minesweeper:config:safe"

const val PRESET_EASY = 0
const val PRESET_MEDIUM = 1
const val PRESET_HARD = 2

const val EASY_ROWS = 9
const val EASY_COLUMNS = 9
const val EASY_MINES = 10

const val MEDIUM_ROWS = 16
const val MEDIUM_COLUMNS = 16
const val MEDIUM_MINES = 40

const val HARD_ROWS = 30
const val HARD_COLUMNS = 16
const val HARD_MINES = 99

const val MAX_SIZE = 99
val MAX_SIZE_BIGINT = BigInteger.valueOf(MAX_SIZE.toLong())

typealias Point = Pair<Int, Int>

val ViewGroup.children: Sequence<View> get() = (0 until childCount).asSequence().map(::getChildAt)

val ViewGroup.childrenRecursive: Sequence<View>
    get() = sequence {
        (0 until childCount).asSequence().map(::getChildAt).forEach {
            yield(it)
            if (it is ViewGroup) yieldAll(it.childrenRecursive)
        }
    }

fun boundsCheck(rows: Int, columns: Int, row: Int, column: Int) {
    if (row !in 0 until rows) throw ArrayIndexOutOfBoundsException(row)
    if (column !in 0 until columns) throw ArrayIndexOutOfBoundsException(column)
}