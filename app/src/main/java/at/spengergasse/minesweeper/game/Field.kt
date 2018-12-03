package at.spengergasse.minesweeper.game

import at.spengergasse.minesweeper.boundsCheck
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

class Field private constructor(
    private val data: ByteArray, mines: Int, val rows: Int, val columns: Int
) {

    val fields = rows * columns

    var mines = mines
        private set

    constructor(rows: Int, columns: Int) : this(createEmptyDataArray(rows, columns), 0, rows, columns)
    constructor(other: Field) : this(other.data.copyOf(), other.mines, other.rows, other.columns)

    private companion object Util {
        @JvmStatic
        private fun createEmptyDataArray(rows: Int, columns: Int) =
            ByteArray(Math.ceil(rows * columns / 8.0).toInt()) { 0 }

        @JvmStatic
        private fun setUnchecked(data: ByteArray, rows: Int, columns: Int, row: Int, column: Int, mine: Boolean) {
            val whichField = columns * row + column
            val whichByte = whichField ushr 3
            val whichBit = whichField % 8
            val oldValue = data[whichByte]
            val newValue =
                if (mine) oldValue or (1 shl whichBit).toByte() else oldValue and (1 shl whichBit).toByte().inv()
            if (oldValue == newValue) return
            data[whichByte] = newValue
        }

        @JvmStatic
        private fun getUnchecked(data: ByteArray, rows: Int, columns: Int, row: Int, column: Int): Boolean {
            val whichField = columns * row + column
            val whichByte = whichField ushr 3
            val whichBit = whichField % 8

            return data[whichByte].toInt() and 0b11111111 ushr whichBit and 1 == 1
        }
    }

    operator fun set(row: Int, column: Int, mine: Boolean) {
        boundsCheck(rows, columns, row, column)
        setUnchecked(data, rows, columns, row, column, mine)
        if (mine) mines++ else mines--
    }

    operator fun get(row: Int, column: Int): Boolean {
        boundsCheck(rows, columns, row, column)
        return getUnchecked(data, rows, columns, row, column)
    }

    private fun getInt(row: Int, column: Int) = when {
        row !in 0 until rows || column !in 0 until columns -> 0
        getUnchecked(data, rows, columns, row, column) -> 1
        else -> 0
    }

    fun getAdjacentMines(row: Int, column: Int): Int {
        boundsCheck(rows, columns, row, column)

        return 0 +
                getInt(row, column - 1) +
                getInt(row - 1, column - 1) +
                getInt(row - 1, column) +
                getInt(row - 1, column + 1) +
                getInt(row, column + 1) +
                getInt(row + 1, column + 1) +
                getInt(row + 1, column) +
                getInt(row + 1, column - 1)
    }

}