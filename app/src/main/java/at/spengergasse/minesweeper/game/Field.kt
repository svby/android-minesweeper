package at.spengergasse.minesweeper.game

import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

class Field private constructor(private val data: ByteArray, mines: Int, val rows: Int, val columns: Int) {

    val fields = rows * columns

    var mines: Int = mines
        private set

    constructor(rows: Int, columns: Int) : this(
        ByteArray(Math.ceil(rows * columns / 8.0).toInt()) { 0 },
        0,
        rows,
        columns
    )

    constructor(other: Field) : this(other.data.copyOf(), other.mines, other.rows, other.columns)

    operator fun set(row: Int, column: Int, mine: Boolean) {
        if (row !in 0 until rows) throw ArrayIndexOutOfBoundsException(row)
        if (column !in 0 until columns) throw ArrayIndexOutOfBoundsException(column)

        val whichField = columns * row + column
        val whichByte = whichField ushr 3
        val whichBit = whichField % 8
            val oldValue = data[whichByte]
            val newValue =
                if (mine) oldValue or (1 shl whichBit).toByte() else oldValue and (1 shl whichBit).toByte().inv()
            if (oldValue == newValue) return
            data[whichByte] = newValue
            if (mine) mines++ else mines--
    }

    private fun unsynchronizedGetInt(row: Int, column: Int): Int {
        if (row !in 0 until rows || column !in 0 until columns) return 0
        return if (unsynchronizedUncheckedGet(row, column)) 1 else 0
    }

    fun getAdjacent(row: Int, column: Int): Int {
        if (row !in 0 until rows) throw ArrayIndexOutOfBoundsException(row)
        if (column !in 0 until columns) throw ArrayIndexOutOfBoundsException(column)

            return 0 +
                    unsynchronizedGetInt(row, column - 1) +
                    unsynchronizedGetInt(row - 1, column - 1) +
                    unsynchronizedGetInt(row - 1, column) +
                    unsynchronizedGetInt(row - 1, column + 1) +
                    unsynchronizedGetInt(row, column + 1) +
                    unsynchronizedGetInt(row + 1, column + 1) +
                    unsynchronizedGetInt(row + 1, column) +
                    unsynchronizedGetInt(row + 1, column - 1)
    }

    private fun unsynchronizedUncheckedGet(row: Int, column: Int): Boolean {
        val whichField = columns * row + column
        val whichByte = whichField ushr 3
        val whichBit = whichField % 8

        return data[whichByte].toInt() and 0b11111111 ushr whichBit and 1 == 1
    }

    private fun uncheckedGet(row: Int, column: Int): Boolean {
        val whichField = columns * row + column
        val whichByte = whichField ushr 3
        val whichBit = whichField % 8

            return data[whichByte].toInt() and 0b11111111 ushr whichBit and 1 == 1
    }

    operator fun get(row: Int, column: Int): Boolean {
        if (row !in 0 until rows) throw ArrayIndexOutOfBoundsException(row)
        if (column !in 0 until columns) throw ArrayIndexOutOfBoundsException(column)

        return uncheckedGet(row, column)
    }

}